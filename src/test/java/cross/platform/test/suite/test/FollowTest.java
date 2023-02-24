package cross.platform.test.suite.test;

import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.test.helper.AssertionHelper;
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
@Test
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FollowTest implements ReportHelper, AssertionHelper, ScreenshotHelper {

    public static final String GROUP = "FollowTest";

    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
    private final ReportManager reportManager;
    private final LoggingAssertion assertion;

    @Test(description = "followTest description...")
    @Screenshot
    public void followTest() {
        log.debug(this.mobileConfig.getServerArguments().getAddress());
        log.debug(this.driverManager.getDriver().getRemoteAddress().toString());

        assertion.assertEquals("Check page title", "My page", "my page");
        assertion.assertEquals("Check OK button label", "OK", "OK");
        assertion.assertEquals("Check Login button label", "Login", "login");
        assertion.assertEquals("Check Cancel button label", "Cancel", "Cancel");
    }

    @Test(dependsOnMethods = "followTest", description = "followTest2 description...")
    @Screenshot
    public void followTest2() {
        assertion.assertEquals("Check page title", "Follow page", "Follow page");
        assertion.assertEquals("Check delete button label", "Delete", "delete");
        assertion.assertEquals("Check copy button label", "Copy", "copy");
        assertion.assertEquals("Check edit button label", "Edit", "Edit");
    }
}
