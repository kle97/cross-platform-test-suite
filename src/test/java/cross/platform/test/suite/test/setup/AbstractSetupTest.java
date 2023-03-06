package cross.platform.test.suite.test.setup;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.properties.ServerArguments;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.util.Throwables;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public abstract class AbstractSetupTest {

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
            this.startSession();
        }
    }

    @AfterSuite(alwaysRun = true)
    protected void afterSuite() {
        if (!Boolean.parseBoolean(System.getProperty("parallel"))) {
            this.stopSession();
        }

        log.info("Writing extent report output to reporters...");
        ReportManager.flush();

        for (String reporterFilePath: ReportManager.getReporterFilePaths()) {
            System.out.println("Generated report file at: " + reporterFilePath);
        }

        LoggingAssertion.assertAll();
    }

    @BeforeTest(alwaysRun = true)
    protected void beforeTest() {
        if (Boolean.parseBoolean(System.getProperty("parallel"))) {
            this.startSession();
        }
    }

    @AfterTest(alwaysRun = true)
    protected void afterTest() {
        if (Boolean.parseBoolean(System.getProperty("parallel"))) {
            this.stopSession();
        }
    }

    protected void startSession() {
        ServerArguments serverArguments = this.getMobileConfig().getServerArguments();
        DesiredCapabilities desiredCapabilities = this.getMobileConfig().getDesiredCapabilities();
        Platform platform = desiredCapabilities.getPlatformName();
        String serverURL = String.format("http://%s:%s/%s", serverArguments.getAddress(), serverArguments.getPort(),
                                         serverArguments.getBasePath());
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
}
