package cross.platform.test.suite.test.apptest;

import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.test.helper.ReportHelper;
import cross.platform.test.suite.test.helper.ScreenshotHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;

@Slf4j
@Guice
@Test(groups = AdvancedTest.GROUP)
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AdvancedTest implements ReportHelper, ScreenshotHelper {
    public static final String GROUP = "AdvancedTest";

    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
    private final ReportManager reportManager = new ReportManager();
    private final LoggingAssertion assertion = new LoggingAssertion(reportManager, log);

    @Screenshot
    @Test(description = "advancedTest description...")
    public void advancedTest() {
        log.info("Running advancedTest method...");
    }
}
