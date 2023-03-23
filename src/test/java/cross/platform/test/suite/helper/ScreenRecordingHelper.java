package cross.platform.test.suite.helper;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import cross.platform.test.suite.annotation.ScreenRecord;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.utility.DriverUtil;
import cross.platform.test.suite.utility.ScreenUtil;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.nio.file.Path;

public interface ScreenRecordingHelper {
    
    DriverManager getDriverManager();
    ReportManager getReportManager();

    @BeforeMethod
    default void screenRecordingHelperBeforeMethod(Method method) {
        ScreenRecord screenRecordAnnotation = method.getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation == null) {
            screenRecordAnnotation = getClass().getAnnotation(ScreenRecord.class);
        }
        if (screenRecordAnnotation != null) {
            this.startRecordingScreen(screenRecordAnnotation.timeLimit());
        }
    }

    @AfterMethod
    default void screenRecordingHelperAfterMethod(Method method) {
        ScreenRecord screenRecordAnnotation = method.getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation == null) {
            screenRecordAnnotation = getClass().getAnnotation(ScreenRecord.class);
        }
        if (screenRecordAnnotation != null) {
            ExtentTest currentReport = getReportManager().getCurrentReport();
            if (currentReport != null && currentReport.getStatus().equals(Status.FAIL)) {
                this.stopRecordingScreen(this.getClass().getSimpleName());
            } else {
                ScreenUtil.stopRecordingScreen(getDriverManager().getDriver());
            }
        }
    }

    @Test(enabled = false)
    default void startRecordingScreen(int timeLimitInSeconds) {
        ScreenUtil.startRecordingScreen(getDriverManager().getDriver(), timeLimitInSeconds);
    }

    @Test(enabled = false)
    default void stopRecordingScreen(String recordingTitle) {
        AppiumDriver appiumDriver = getDriverManager().getDriver();
        Path recordingPath = ScreenUtil.stopRecordingScreen(appiumDriver, recordingTitle);
        if (recordingPath != null) {
            Dimension dimension = DriverUtil.getWindowSize(appiumDriver);
            int width = dimension.getWidth();
            int height = dimension.getHeight();
            String source = "file:///".concat(recordingPath.toAbsolutePath().toString());
            getReportManager().info("<video width='" + width + "' height='" + height + "' controls> " +
                                            "<source src='" + source + "' type='video/mp4'>  " +
                                            "Your browser does not support the video tag.</video>");
        }
    }
}