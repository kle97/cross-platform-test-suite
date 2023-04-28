package cross.platform.test.suite.test.common;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import cross.platform.test.suite.annotation.DisableAutoReport;
import cross.platform.test.suite.annotation.ScreenRecord;
import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.constant.When;
import cross.platform.test.suite.properties.TestConfig;
import cross.platform.test.suite.service.AppiumService;
import cross.platform.test.suite.service.DriverManager;
import cross.platform.test.suite.service.LoggingAssertion;
import cross.platform.test.suite.service.Reporter;
import cross.platform.test.suite.utility.ConfigUtil;
import cross.platform.test.suite.utility.DriverUtil;
import cross.platform.test.suite.utility.ScreenUtil;
import io.appium.java_client.AppiumDriver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.*;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
public abstract class BaseTest {

    @Inject
    private Reporter reporter;
    
    @Inject
    private LoggingAssertion assertion;
    
    @Inject
    private DriverManager driverManager;
    
    @Inject
    private TestConfig testConfig;

    @Inject
    private AppiumService appiumService;

    @BeforeSuite(alwaysRun = true)
    protected void beforeSuite() {
        String timeStamp = DateTimeFormatter.ofPattern("MM-dd-yyyy-HH-mm-ss")
                                            .withZone(ZoneId.systemDefault())
                                            .format(Instant.now());
        String filePath = "cross-platform-test-suite" + "-" + timeStamp + ".html";
        String reportFilePath = TestConst.REPORT_PATH + filePath;
        ExtentSparkReporter spark = new ExtentSparkReporter(reportFilePath);
        spark.config().setCss(".col-md-3 > img { max-width: 180px; max-height: 260px; } .col-md-3 > .title { max-width: 180px; }");
        Reporter.attachReporter(spark);

        if (!ConfigUtil.isParallel()) {
            if (!this.isHub()) {
                Runtime.getRuntime().addShutdownHook(new Thread(getAppiumService()::stopServer));
                getAppiumService().startServer();
            }
            getAppiumService().startSession();
        }
    }

    @AfterSuite(alwaysRun = true)
    protected void afterSuite() {
        if (!ConfigUtil.isParallel()) {
            getAppiumService().stopSession();
            if (!this.isHub()) {
                getAppiumService().stopServer();
            }
        }

        log.info("Writing extent report output to reporters...");
        Reporter.flush();

        for (String reporterFilePath: Reporter.getReporterFilePaths()) {
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
        if (ConfigUtil.isParallel()) {
            if (!this.isHub()) {
                getAppiumService().startServer();
            }
            getAppiumService().startSession();
        }
    }

    @AfterTest(alwaysRun = true)
    protected void afterTest() {
        if (ConfigUtil.isParallel()) {
            getAppiumService().stopSession();
            if (!this.isHub()) {
                getAppiumService().stopServer();
            }
        }
    }

    @BeforeClass
    protected void beforeClass(ITestContext context) {
        String className = this.getClass().getSimpleName();
        String testName = context.getName();
        if (testName.isBlank()) {
            testName = TestConst.DEFAULT_TEST_NAME;
        }
        this.getReporter().createClassReport(className, testName);
        this.getAssertion().setLogger(LoggerFactory.getLogger(this.getClass()));

        ScreenRecord screenRecordAnnotation = this.getClass().getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation != null) {
            this.startRecordingScreen(screenRecordAnnotation.timeLimit());
        }
    }

    @AfterClass
    protected void afterClass() {
        ScreenRecord screenRecordAnnotation = this.getClass().getDeclaredAnnotation(ScreenRecord.class);
        if (screenRecordAnnotation != null) {
            ExtentTest classReport = getReporter().getCurrentClassReport();
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
        DisableAutoReport disableAutoReportAnnotation = method.getDeclaredAnnotation(DisableAutoReport.class);
        if (disableAutoReportAnnotation == null) {
            this.getReporter().createMethodReport(methodName, className, testName);
        }

        if (!description.isBlank()) {
            this.getReporter().info("Description: " + description);
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
        ExtentTest methodReport = getReporter().findReport(method.getName());
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
    protected boolean isHub() {
        return this.getTestConfig().getMobileConfig().getServerArguments().isHub();
    }

    @Test(enabled = false)
    protected AppiumDriver getDriver() {
        return this.getDriverManager().getDriver();
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
                getReporter().addScreenshot(screenshotFile.getAbsolutePath(), screenshotTitle);
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
                getReporter().info(attachment);
            }
        }
    }
}