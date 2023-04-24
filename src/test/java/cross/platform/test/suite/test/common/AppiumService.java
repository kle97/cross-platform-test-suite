package cross.platform.test.suite.test.common;

import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.properties.ServerArguments;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.inject.Inject;
import java.io.IOException;
import java.net.*;

@Slf4j
public class AppiumService {

    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
    private AppiumDriver appiumDriver;

    private AppiumDriverLocalService appiumDriverLocalService;
    
    @Inject
    public AppiumService(DriverManager driverManager, MobileConfig mobileConfig) {
        this.driverManager = driverManager;
        this.mobileConfig = mobileConfig;
    }

    public void startSession() {
        ServerArguments serverArguments = this.mobileConfig.getServerArguments();
        DesiredCapabilities desiredCapabilities = this.mobileConfig.getDesiredCapabilities();
        Platform platform = desiredCapabilities.getPlatformName();
        String serverURL;
        if (serverArguments.isHub()) {
            serverURL = String.format("http://%s:%s", serverArguments.getAddress(), serverArguments.getPort());
        } else {
            serverURL = String.format("http://%s:%s%s", serverArguments.getAddress(), serverArguments.getPort(), serverArguments.getBasePath());
        }

        for (int i = 0; i < TestConst.APPIUM_SESSION_START_RETRIES; i++) {
            try {
                URL remoteAddress = new URL(serverURL);
                if (platform.is(Platform.ANDROID)) {
                    UiAutomator2Options uiAutomator2Options = new UiAutomator2Options(desiredCapabilities);
                    Runtime.getRuntime().addShutdownHook(new Thread(this::cleanUpSessionHook));
                    appiumDriver = new AndroidDriver(remoteAddress, uiAutomator2Options);
                    driverManager.setDriver(appiumDriver);
                    log.info("Driver session created with id {}!", appiumDriver.getSessionId());
                }
                break;
            } catch (WebDriverException ex) {
                log.error("WebDriverException: ", ex);
            } catch (MalformedURLException ex) {
                log.error("MalformedURLException: ", ex);
            } finally {
                if (i < TestConst.APPIUM_SESSION_START_RETRIES - 1) {
                     log.debug("Retrying to start driver session!");
                }
            }
        }
    }

    public void stopSession() {
        if (this.driverManager.hasDriver()) {
            AppiumDriver appiumDriver = this.driverManager.getDriver();
            String id = appiumDriver.getSessionId().toString();
            appiumDriver.quit();
            this.driverManager.removeDriver();
            log.info("Driver session with id '{}' has quit!", id);
        }
    }

    public void cleanUpSessionHook() {
        log.info("Shutdown hook: quitting Appium driver!");
        appiumDriver.quit();
    }

    public AppiumDriverLocalService startServer() {
        ServerArguments serverArguments = this.mobileConfig.getServerArguments();
        if (isPortFree(serverArguments.getPort())) {
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
                    break;
                } catch (AppiumServerHasNotBeenStartedLocallyException ex) {
                    log.info(ex.getMessage());
                }
            }
        }
        return this.appiumDriverLocalService;
    }

    public void stopServer() {
        if (appiumDriverLocalService != null) {
            String address = appiumDriverLocalService.getUrl().toString();
            appiumDriverLocalService.stop();
            log.info("Appium server at '{}' has stopped!", address);
        }
    }

    public boolean isPortFree(int port) {
        try (ServerSocket socket = new ServerSocket()) {
            // setReuseAddress(false) is required only on macOS, 
            // otherwise the code will not work correctly on that platform   
            socket.setReuseAddress(false);
            socket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
            return true;
        } catch (IOException ex) {
            log.info("Port {} is occupied!", port);
            return false;
        }
    }
}
