package cross.platform.test.suite.test.setup;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.properties.ServerArguments;
import cross.platform.test.suite.utility.ScreenUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.util.Throwables;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public abstract class AbstractSetupTest {

    private AppiumDriverLocalService appiumDriverLocalService;

    protected abstract MobileConfig getMobileConfig();

    protected abstract DriverManager getDriverManager();

    @BeforeSuite(alwaysRun = true)
    protected void beforeSuite() {
        String timeStamp = DateTimeFormatter.ofPattern("MM-dd-yyyy-HH-mm-ss")
                                            .withZone(ZoneId.systemDefault())
                                            .format(Instant.now());
        String filePath = "cross-platform-test-suite" + "-" + timeStamp + ".html";
        String reportFilePath = TestConst.REPORT_PATH + filePath;
        ExtentSparkReporter spark = new ExtentSparkReporter(reportFilePath);
        spark.config().setCss(".col-md-3 > img { max-width: 180px; max-height: 260px; } .col-md-3 > .title { max-width: 180px; }");
        ReportManager.attachReporter(spark);

        if (!Boolean.parseBoolean(System.getProperty("parallel"))) {
            if (!this.getMobileConfig().getServerArguments().isHub()) {
                this.startServer();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(this::cleanUpSessionHook));
            this.startSession();
        }
    }

    @AfterSuite(alwaysRun = true)
    protected void afterSuite() {
        if (!Boolean.parseBoolean(System.getProperty("parallel"))) {
            this.stopSession();
            if (!this.getMobileConfig().getServerArguments().isHub()) {
                this.stopServer();
            }
        }

        log.info("Writing extent report output to reporters...");
        ReportManager.flush();

        for (String reporterFilePath: ReportManager.getReporterFilePaths()) {
            System.out.println("Generated report file at: " + reporterFilePath);
        }

        try {
            LoggingAssertion.assertAll();
        } catch (AssertionError e) {
            log.error("AssertionError: ", e);
            throw e;
        }
    }

    @BeforeTest(alwaysRun = true)
    protected void beforeTest() {
        if (Boolean.parseBoolean(System.getProperty("parallel"))) {
            if (!this.getMobileConfig().getServerArguments().isHub()) {
                this.startServer();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(this::cleanUpSessionHook));
            this.startSession();
        }
    }

    @AfterTest(alwaysRun = true)
    protected void afterTest() {
        if (Boolean.parseBoolean(System.getProperty("parallel"))) {
            this.stopSession();
            if (!this.getMobileConfig().getServerArguments().isHub()) {
                this.stopServer();
            }
        }
    }

    protected void startSession() {
        ServerArguments serverArguments = this.getMobileConfig().getServerArguments();
        DesiredCapabilities desiredCapabilities = this.getMobileConfig().getDesiredCapabilities();
        Platform platform = desiredCapabilities.getPlatformName();
        String serverURL;
        if (serverArguments.isHub()) {
            serverURL = String.format("http://%s:%s", serverArguments.getAddress(), serverArguments.getPort());
        } else {
            serverURL = String.format("http://%s:%s%s", serverArguments.getAddress(), serverArguments.getPort(), serverArguments.getBasePath());
        }
        URL remoteAddress;
        try {
            remoteAddress = new URL(serverURL);
        } catch (MalformedURLException ex) {
            log.info(ex.getMessage());
            return;
        }

        for (int i = 0; i < TestConst.APPIUM_SESSION_START_RETRIES; i++) {
            try {
                if (platform.is(Platform.ANDROID)) {
                    UiAutomator2Options uiAutomator2Options = new UiAutomator2Options(desiredCapabilities);
                    AppiumDriver appiumDriver = new AndroidDriver(remoteAddress, uiAutomator2Options);
                    this.getDriverManager().setDriver(appiumDriver);
                    log.info("Driver session created with id {}!", appiumDriver.getSessionId());
                }
                return;
            } catch (WebDriverException ex) {
                if (ex.getCause() != null) {
                    Throwable rootCause = Throwables.getRootCause(ex.getCause());
                    log.info(rootCause.getMessage());
                } else {
                    log.info(ex.getMessage());
                }
            }
        }
    }

    protected void stopSession() {
        if (this.getDriverManager().hasDriver()) {
            AppiumDriver appiumDriver = this.getDriverManager().getDriver();
            String id = appiumDriver.getSessionId().toString();
            appiumDriver.quit();
            this.getDriverManager().removeDriver();
            log.info("Driver session with id '{}' has quit!", id);
        }
    }

    protected void cleanUpSessionHook() {
        if (this.getDriverManager().hasDriver()) {
            ScreenUtil.stopRecordingScreen(this.getDriverManager().getDriver());
        }
        this.stopSession();
    }

    protected void startServer() {
        ServerArguments serverArguments = this.getMobileConfig().getServerArguments();
        if (!isAppiumServerRunningAtPort(serverArguments.getPort())) {
            AppiumServiceBuilder appiumServiceBuilder = new AppiumServiceBuilder();
            appiumServiceBuilder.withIPAddress(serverArguments.getAddress());
            appiumServiceBuilder.usingPort(serverArguments.getPort());
            appiumServiceBuilder.withArgument(GeneralServerFlag.BASEPATH, serverArguments.getBasePath());
            if (serverArguments.hasConfig("logLevel")) {
                appiumServiceBuilder.withArgument(GeneralServerFlag.LOG_LEVEL,
                                                  serverArguments.getAsString("logLevel"));
            }
            if (serverArguments.hasConfig("allowInsecure")) {
                appiumServiceBuilder.withArgument(GeneralServerFlag.ALLOW_INSECURE, serverArguments.getAsString("allowInsecure"));
            }

            this.appiumDriverLocalService = AppiumDriverLocalService.buildService(appiumServiceBuilder);
            for (int i = 0; i < TestConst.APPIUM_SERVER_START_RETRIES; i++) {
                try {
                    this.appiumDriverLocalService.start();
                    log.info("Server started at '{}'!", this.appiumDriverLocalService.getUrl().toString());
                    return;
                } catch (AppiumServerHasNotBeenStartedLocallyException ex) {
                    log.info(ex.getMessage());
                }
            }
        }
    }

    protected void stopServer() {
        if (appiumDriverLocalService != null) {
            String address = appiumDriverLocalService.getUrl().toString();
            appiumDriverLocalService.stop();
            log.info("Appium server at '{}' has stopped!", address);
        }
    }

    protected boolean isAppiumServerRunningAtPort(int port) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (IOException ex) {
            log.info("Appium is already running at port {}!", port);
            return true;
        }
        return false;
    }

    protected void setAppiumDriverLocalService(AppiumDriverLocalService appiumDriverLocalService) {
        this.appiumDriverLocalService = appiumDriverLocalService;
    }

    protected AppiumDriverLocalService getAppiumDriverLocalService() {
        return appiumDriverLocalService;
    }
}