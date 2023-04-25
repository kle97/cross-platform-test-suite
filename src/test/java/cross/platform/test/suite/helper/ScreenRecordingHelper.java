package cross.platform.test.suite.helper;

import com.aventstack.extentreports.ExtentTest;
import cross.platform.test.suite.annotation.ScreenRecord;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.service.DriverManager;
import cross.platform.test.suite.service.Reporter;
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
    Reporter getReportManager();

    @BeforeMethod
    default void screenRecordingHelperBeforeMethod(Method method) {
        ScreenRecord screenRecordAnnotation = method.getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation != null) {
            this.startRecordingScreen(screenRecordAnnotation.timeLimit());
        }
    }

    @AfterMethod
    default void screenRecordingHelperAfterMethod(Method method) {
        ScreenRecord screenRecordAnnotation = method.getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation != null) {
            ExtentTest methodReport = getReportManager().findReport(method.getName());
            if (methodReport != null && methodReport.getStatus().getName().equals("Fail")) {
                this.stopRecordingScreen(methodReport, this.getClass().getSimpleName());
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
        stopRecordingScreen(null, recordingTitle);
    }

    @Test(enabled = false)
    default void stopRecordingScreen(ExtentTest report, String recordingTitle) {
        AppiumDriver appiumDriver = getDriverManager().getDriver();
        Dimension dimension = DriverUtil.getWindowSize(appiumDriver);
        int width = dimension.getWidth();
        int height = dimension.getHeight();
        Path recordingPath = ScreenUtil.stopRecordingScreen(appiumDriver, TestConst.SCREEN_RECORDING_DIRECTORY, recordingTitle);
        if (recordingPath != null) {
            String source = "file:///".concat(recordingPath.toAbsolutePath().toString());
            String attachment = "<video width='" + width + "' height='" + height + "' controls> " +
                    "<source src='" + source + "' type='video/mp4'> Your browser does not support the video tag.</video>";
            if (report != null) {
                report.info(attachment);
            } else {
                getReportManager().info(attachment);
            }
        }
    }
}