package cross.platform.test.suite.test.common;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public abstract class AbstractTestSetup {

    protected AppiumService appiumService;
    
    protected abstract MobileConfig getMobileConfig();

    protected abstract DriverManager getDriverManager();

    protected AppiumService getAppiumService() {
        if (this.appiumService == null) {
            this.appiumService = new AppiumService(getMobileConfig(), getDriverManager());
        }
        return this.appiumService;
    }
    
    private boolean isParallel() {
        return Boolean.parseBoolean(System.getProperty("parallel"));
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

        if (!isParallel()) {
            if (!this.getMobileConfig().getServerArguments().isHub()) {
                Runtime.getRuntime().addShutdownHook(new Thread(getAppiumService()::stopServer));
                getAppiumService().startServer();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(getAppiumService()::cleanUpSessionHook));
            getAppiumService().startSession();
        }
    }

    @AfterSuite(alwaysRun = true)
    protected void afterSuite() {
        if (!isParallel()) {
            getAppiumService().stopSession();
            if (!this.getMobileConfig().getServerArguments().isHub()) {
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
        if (isParallel()) {
            if (!this.getMobileConfig().getServerArguments().isHub()) {
                getAppiumService().startServer();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(getAppiumService()::cleanUpSessionHook));
            getAppiumService().startSession();
        }
    }

    @AfterTest(alwaysRun = true)
    protected void afterTest() {
        if (isParallel()) {
            getAppiumService().stopSession();
            if (!this.getMobileConfig().getServerArguments().isHub()) {
                getAppiumService().stopServer();
            }
        }
    }
}