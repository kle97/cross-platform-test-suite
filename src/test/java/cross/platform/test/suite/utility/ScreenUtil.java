package cross.platform.test.suite.utility;

import cross.platform.test.suite.constant.TestConst;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Slf4j
public final class ScreenUtil {

    private ScreenUtil() {
    }

    public static File saveScreenshot(AppiumDriver appiumDriver, String title) {
        return saveScreenshot(appiumDriver, title, TestConst.SCREENSHOT_PATH);
    }

    public static File saveScreenshot(AppiumDriver appiumDriver, String title, String directory) {
        File tempScreenshotFile = getScreenshotAsFile(appiumDriver);
        if (tempScreenshotFile != null) {
            File directoryFile = new File(directory);
            if (directoryFile.exists() || directoryFile.mkdirs()) {
                String filePath = directory + UUID.randomUUID() + "-" + title + ".jpg";
                File savedScreenshotFile = new File(filePath);
                if (copyFile(tempScreenshotFile, savedScreenshotFile)) {
                    return savedScreenshotFile;
                }
            }
        }
        log.debug("Couldn't save screenshot with title '{}' to path '{}'", title, directory);
        return null;
    }

    public static boolean copyFile(File srcFile, File destFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(srcFile));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(destFile))) {
            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
            return true;
        } catch (IOException ex) {
            log.trace("Couldn't copy to file: " + ex.getMessage());
            return false;
        }
    }

    public static String getScreenshotAsBase64(AppiumDriver appiumDriver) {
        String base64EncodedPNG = "CAN_NOT_CAPTURE_SCREENSHOT";
        try {
            base64EncodedPNG = appiumDriver.getScreenshotAs(OutputType.BASE64);
        } catch (WebDriverException ex) {
            log.trace(ex.getMessage());
        }
        return base64EncodedPNG;
    }

    public static File getScreenshotAsFile(AppiumDriver appiumDriver) {
        File screenshotFile = null;
        try {
            screenshotFile = appiumDriver.getScreenshotAs(OutputType.FILE);
        } catch (WebDriverException ex) {
            log.trace(ex.getMessage());
        }
        return screenshotFile;
    }

    public static void startRecordingScreen(AppiumDriver appiumDriver, int timeLimitInSeconds) {
        timeLimitInSeconds = Math.min(timeLimitInSeconds, TestConst.DEFAULT_RECORDING_LIMIT_IN_SECONDS);
        if (appiumDriver instanceof AndroidDriver) {
            try {
                AndroidStartScreenRecordingOptions options = new AndroidStartScreenRecordingOptions()
                        .withTimeLimit(Duration.ofSeconds(timeLimitInSeconds));
                ((CanRecordScreen) appiumDriver).startRecordingScreen(options);
            } catch (WebDriverException e) {
                log.debug(e.getMessage());
            }
        } else if (appiumDriver instanceof IOSDriver) {
            try {
                IOSStartScreenRecordingOptions options = new IOSStartScreenRecordingOptions()
                        .withTimeLimit(Duration.ofSeconds(timeLimitInSeconds));
                ((CanRecordScreen) appiumDriver).startRecordingScreen(options);
            } catch (WebDriverException e) {
                log.debug(e.getMessage());
            }
        }
    }

    public static String stopRecordingScreen(AppiumDriver appiumDriver) {
        String base64EncodedVideo = "";
        if (appiumDriver instanceof CanRecordScreen) {
            try {
                base64EncodedVideo = ((CanRecordScreen) appiumDriver).stopRecordingScreen();
            } catch (WebDriverException e) {
                log.debug(e.getMessage());
            }
        }
        return base64EncodedVideo;
    }

    public static Path stopRecordingScreen(AppiumDriver appiumDriver, String recordingTitle) {
        if (appiumDriver instanceof CanRecordScreen) {
            try {
                File directoryFile = new File(TestConst.SCREEN_RECORDING_PATH);
                if (directoryFile.exists() || directoryFile.mkdirs()) {
                    try {
                        Thread.sleep(TestConst.DEFAULT_RECORDING_DELAY);
                    } catch (InterruptedException e) {
                        log.debug(e.getMessage());
                    }
                    String base64EncodedVideo = ((CanRecordScreen) appiumDriver).stopRecordingScreen();
                    byte[] decodedVideo = Base64.getDecoder().decode(base64EncodedVideo);
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    String fileName = TestConst.SCREEN_RECORDING_PATH + recordingTitle + "-" + timestamp + ".mp4";
                    Path path = Paths.get(fileName);
                    return Files.write(path, decodedVideo);
                }
            } catch (IOException | IllegalArgumentException e) {
                log.debug("Cannot save screen recording to file!");
                log.debug(e.getMessage());
            } catch (WebDriverException e) {
                log.debug(e.getMessage());
            }
        }
        return null;
    }
}