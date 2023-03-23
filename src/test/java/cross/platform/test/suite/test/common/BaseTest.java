package cross.platform.test.suite.test.common;

import com.aventstack.extentreports.ExtentTest;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;

@Slf4j
public abstract class BaseTest {

    public abstract ReportManager getReportManager();
    public abstract DriverManager getDriverManager();

    @BeforeClass
    protected void reportHelperBeforeClass(ITestContext context) {
        String className = this.getClass().getSimpleName();
        String testName = context.getName();
        if (testName.isBlank()) {
            testName = TestConst.DEFAULT_TEST_NAME;
        }
        this.getReportManager().createClassReport(className, testName);
    }

    @BeforeMethod
    protected void reportHelperBeforeMethod(ITestResult result, Method method) {
        ITestNGMethod testMethod = result.getMethod();
        String className = testMethod.getRealClass().getSimpleName();
        String testName = result.getTestName();
        String methodName = testMethod.getMethodName();
        String description = testMethod.getDescription();
        this.getReportManager().createMethodReport(methodName, description, className, testName);
        this.getReportManager().info("Description: " + description);
        LoggerFactory.getLogger(className).info("Description: " + description);

        Screenshot screenshotAnnotation = method.getDeclaredAnnotation(Screenshot.class);
        if (screenshotAnnotation == null) {
            screenshotAnnotation = getClass().getAnnotation(Screenshot.class);
        }
        if (screenshotAnnotation != null && (screenshotAnnotation.when().equals(When.BOTH) || screenshotAnnotation.when().equals(When.BEFORE))) {
            String screenshotTitle = "before" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(screenshotTitle);
        }

        ScreenRecord screenRecordAnnotation = method.getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation == null) {
            screenRecordAnnotation = getClass().getAnnotation(ScreenRecord.class);
        }
        if (screenRecordAnnotation != null) {
            this.startRecordingScreen(screenRecordAnnotation.timeLimit());
        }
    }

    @AfterMethod
    protected void screenshotHelperAfterMethod(Method method) {
        Screenshot screenshotAnnotation = method.getDeclaredAnnotation(Screenshot.class);
        if (screenshotAnnotation == null) {
            screenshotAnnotation = getClass().getAnnotation(Screenshot.class);
        }
        if (screenshotAnnotation != null && (screenshotAnnotation.when().equals(When.BOTH) || screenshotAnnotation.when().equals(When.AFTER))) {
            String methodName = method.getName();
            String screenshotTitle = "after" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            this.takeScreenshot(screenshotTitle);
        }

        ScreenRecord screenRecordAnnotation = method.getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation == null) {
            screenRecordAnnotation = getClass().getAnnotation(ScreenRecord.class);
        }
        if (screenRecordAnnotation != null) {
            ExtentTest currentReport = getReportManager().getCurrentReport();
            if (currentReport != null && currentReport.getStatus().getName().equals("Fail")) {
                this.stopRecordingScreen(this.getClass().getSimpleName());
            } else {
                ScreenUtil.stopRecordingScreen(getDriverManager().getDriver());
            }
        }
    }

    @Test(enabled = false)
    protected void takeScreenshot(String screenshotTitle) {
        File screenshotFile = ScreenUtil.saveScreenshot(getDriverManager().getDriver(), screenshotTitle);
        if (screenshotFile != null) {
            getReportManager().addScreenshot(screenshotFile.getAbsolutePath(), screenshotTitle);
        }
    }

    @Test(enabled = false)
    protected void startRecordingScreen(int timeLimitInSeconds) {
        ScreenUtil.startRecordingScreen(getDriverManager().getDriver(), timeLimitInSeconds);
    }

    @Test(enabled = false)
    protected void stopRecordingScreen(String recordingTitle) {
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
