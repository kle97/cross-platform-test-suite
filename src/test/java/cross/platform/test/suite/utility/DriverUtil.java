package cross.platform.test.suite.utility;

import cross.platform.test.suite.constant.TestConst;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.*;
import java.util.UUID;

@Slf4j
public final class DriverUtil {

    private DriverUtil() {
    }

    public static File saveScreenshot(WebDriver webDriver, String title) {
        return saveScreenshot(webDriver, title, TestConst.SCREENSHOT_PATH);
    }

    public static File saveScreenshot(WebDriver webDriver, String title, String directory) {
        File tempScreenshotFile = getScreenshotAsFile(webDriver);
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

    public static String getScreenshotAsBase64(WebDriver webDriver) {
        String base64EncodedPNG = "CAN_NOT_CAPTURE_SCREENSHOT";
        try {
            base64EncodedPNG = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BASE64);
        } catch (WebDriverException ex) {
            log.trace(ex.getMessage());
        }
        return base64EncodedPNG;
    }

    public static File getScreenshotAsFile(WebDriver webDriver) {
        File screenshotFile = null;
        try {
            screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        } catch (WebDriverException ex) {
            log.trace(ex.getMessage());
        }
        return screenshotFile;
    }
}
