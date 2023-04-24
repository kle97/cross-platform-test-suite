package cross.platform.test.suite.test.common;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.TestConfig;
import cross.platform.test.suite.utility.ConfigUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
public abstract class AbstractTestSetup {

    @Inject
    private TestConfig testConfig;
    
    @Inject
    private AppiumService appiumService;
    
    protected boolean isHub() {
        return this.getTestConfig().getMobileConfig().getServerArguments().isHub();
    }
    
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
        ReportManager.flush();

        for (String reporterFilePath: ReportManager.getReporterFilePaths()) {
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
}