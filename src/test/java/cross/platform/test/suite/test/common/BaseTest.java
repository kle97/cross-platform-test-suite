package cross.platform.test.suite.test.common;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Media;
import cross.platform.test.suite.annotation.ScreenRecord;
import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.constant.When;
import cross.platform.test.suite.utility.DriverUtil;
import cross.platform.test.suite.utility.ScreenUtil;
import io.appium.java_client.AppiumDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;

@Slf4j
public abstract class BaseTest {
    
    public abstract ReportManager getReportManager();
    public abstract DriverManager getDriverManager();

    @BeforeClass
    protected void beforeClass(ITestContext context) {
        String className = this.getClass().getSimpleName();
        String testName = context.getName();
        if (testName.isBlank()) {
            testName = TestConst.DEFAULT_TEST_NAME;
        }
        this.getReportManager().createClassReport(className, testName);

        ScreenRecord screenRecordAnnotation = this.getClass().getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation != null) {
            this.startRecordingScreen(screenRecordAnnotation.timeLimit());
        }
    }
    
    @AfterClass
    protected void afterClass() {
        ScreenRecord screenRecordAnnotation = this.getClass().getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation != null) {
            ExtentTest classReport = getReportManager().getCurrentClassReport();
            if (classReport != null && classReport.getStatus().getName().equals("Fail")) {
                this.stopRecordingScreen(classReport, this.getClass().getSimpleName());
            } else {
                ScreenUtil.stopRecordingScreen(getDriverManager().getDriver());
            }
        }
    }

    @BeforeMethod
    protected void beforeMethod(ITestResult result, Method method) {
        ITestNGMethod testMethod = result.getMethod();
        String className = testMethod.getRealClass().getSimpleName();
        String testName = result.getTestName();
        String methodName = testMethod.getMethodName();
        String description = testMethod.getDescription();
        this.getReportManager().createMethodReport(methodName, className, testName);
        if (!description.isBlank()) {
            this.getReportManager().info("Description: " + description);
            LoggerFactory.getLogger(className).info("Description: " + description);
        }
        
        Screenshot screenshotAnnotation = method.getDeclaredAnnotation(Screenshot.class);
        if (screenshotAnnotation != null && (screenshotAnnotation.when().equals(When.BOTH) || screenshotAnnotation.when().equals(When.BEFORE))) {
            String screenshotTitle = "before" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(screenshotTitle);
        }

        if (this.getClass().getAnnotation(ScreenRecord.class) == null) {
            ScreenRecord screenRecordAnnotation = method.getDeclaredAnnotation(ScreenRecord.class);
            if (screenRecordAnnotation != null) {
                this.startRecordingScreen(screenRecordAnnotation.timeLimit());
            }
        }
    }

    @AfterMethod
    protected void afterMethod(Method method) {
        Screenshot screenshotAnnotation = method.getDeclaredAnnotation(Screenshot.class);
        ExtentTest methodReport = getReportManager().findReport(method.getName());
        if (screenshotAnnotation != null && (screenshotAnnotation.when().equals(When.BOTH) || screenshotAnnotation.when().equals(When.AFTER))) {
            String methodName = method.getName();
            String screenshotTitle = "after" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(methodReport, screenshotTitle);
        }

        if (this.getClass().getAnnotation(ScreenRecord.class) == null) {
            ScreenRecord screenRecordAnnotation = method.getDeclaredAnnotation(ScreenRecord.class);
            if (screenRecordAnnotation != null) {
                if (methodReport != null && methodReport.getStatus().getName().equals("Fail")) {
                    this.stopRecordingScreen(methodReport, this.getClass().getSimpleName());
                } else {
                    ScreenUtil.stopRecordingScreen(getDriverManager().getDriver());
                }
            } 
        }
    }

    @Test(enabled = false)
    protected void takeScreenshot(String screenshotTitle) {
        this.takeScreenshot(null, screenshotTitle);
    }

    @Test(enabled = false)
    protected void takeScreenshot(ExtentTest report, String screenshotTitle) {
        File screenshotFile = ScreenUtil.saveScreenshot(getDriverManager().getDriver(), TestConst.SCREENSHOT_PATH, screenshotTitle);
        if (screenshotFile != null) {
            if (report != null) {
                Media media = MediaEntityBuilder.createScreenCaptureFromPath(screenshotFile.getAbsolutePath(), screenshotTitle).build();
                report.info(media);
            } else {
                getReportManager().addScreenshot(screenshotFile.getAbsolutePath(), screenshotTitle);
            }
        }
    }

    @Test(enabled = false)
    protected void startRecordingScreen(int timeLimitInSeconds) {
        ScreenUtil.startRecordingScreen(getDriverManager().getDriver(), timeLimitInSeconds);
    }

    @Test(enabled = false)
    protected void stopRecordingScreen(String recordingTitle) {
        this.stopRecordingScreen(null, recordingTitle);
    }

    @Test(enabled = false)
    protected void stopRecordingScreen(ExtentTest report, String recordingTitle) {
        AppiumDriver appiumDriver = getDriverManager().getDriver();
        Dimension dimension = DriverUtil.getWindowSize(appiumDriver);
        int width = dimension.getWidth();
        int height = dimension.getHeight();
        Path recordingPath = ScreenUtil.stopRecordingScreen(appiumDriver, TestConst.SCREEN_RECORDING_DIRECTORY, recordingTitle);
        if (recordingPath != null) {
            String source = "file:///".concat(recordingPath.toAbsolutePath().toString());
            String attachment = "<video width='" + width + "' height='" + height + "' controls> " +
                    "<source src='" + source + "' type='video/" + ScreenUtil.DEFAULT_VIDEO_FORMAT + "'> " +
                    "Your browser does not support the video tag.</video>";
            if (report != null) {
                report.info(attachment);
            } else {
                getReportManager().info(attachment);
            }
        }
    }
}
