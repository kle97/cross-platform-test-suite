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
@Test(groups = FollowTest.GROUP, dependsOnGroups = AdvancedTest.GROUP)
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FollowTest implements ReportHelper, ScreenshotHelper {

    public static final String GROUP = "FollowTest";

    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
    private final ReportManager reportManager = new ReportManager();
    private final LoggingAssertion assertion = new LoggingAssertion(reportManager, log);

    @Screenshot
    @Test(description = "followTest description...")
    public void followTest() {
        log.debug(this.mobileConfig.getServerArguments().getAddress());
        log.debug(this.driverManager.getDriver().getRemoteAddress().toString());

        assertion.assertEquals("Check page title", "My page", "my page");
        assertion.assertEquals("Check OK button label", "OK", "OK");
        assertion.assertEquals("Check Login button label", "Login", "login");
        assertion.assertEquals("Check Cancel button label", "Cancel", "Cancel");
    }

    @Screenshot
    @Test(description = "followTest2 description...", dependsOnMethods = "followTest")
    public void followTest2() {
        assertion.assertEquals("Check page title", "Follow page", "Follow page");
        assertion.assertEquals("Check delete button label", "Delete", "Delete");
        assertion.assertEquals("Check copy button label", "Copy", "Copy");
        assertion.assertEquals("Check edit button label", "Edit", "Edit");
    }
}
