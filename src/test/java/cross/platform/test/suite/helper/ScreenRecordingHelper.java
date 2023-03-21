package cross.platform.test.suite.helper;

import com.aventstack.extentreports.ExtentTest;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.utility.DriverUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Path;

public interface ScreenRecordingHelper {
    DriverManager getDriverManager();

    ReportManager getReportManager();
    
    LoggingAssertion getAssertion();

    @BeforeClass
    default void screenRecordingHelperBeforeClass() {
        this.startRecordingScreen(180);
    }

    @AfterClass
    default void screenRecordingHelperAfterClass() {
        if (getAssertion().getLocalErrorCount() > 0) {
            this.stopRecordingScreen(this.getClass().getSimpleName());
        } else {
            DriverUtil.stopRecordingScreen(getDriverManager().getDriver());
        }
    }

    @Test(enabled = false)
    default void startRecordingScreen(int timeLimitInSeconds) {
        DriverUtil.startRecordingScreen(getDriverManager().getDriver(), timeLimitInSeconds);
    }

    @Test(enabled = false)
    default void stopRecordingScreen(String recordingTitle) {
        Path recordingPath = DriverUtil.stopRecordingScreen(getDriverManager().getDriver(), recordingTitle);
        if (recordingPath != null) {
            String className = this.getClass().getSimpleName();
            ExtentTest classReport = getReportManager().getClassReport(className);
            if (classReport != null) {
                String source = "file:///".concat(recordingPath.toAbsolutePath().toString());
                classReport.info("<video width='320' height='480' controls> " +
                                         "<source src='" + source + "' type='video/mp4'>  " +
                                         "Your browser does not support the video tag." +
                                         "</video>");
            }
        }
    }
}
