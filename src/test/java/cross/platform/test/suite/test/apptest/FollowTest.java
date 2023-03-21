package cross.platform.test.suite.test.apptest;

import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.helper.ReportHelper;
import cross.platform.test.suite.helper.ScreenRecordingHelper;
import cross.platform.test.suite.helper.ScreenshotHelper;
import cross.platform.test.suite.pageobject.ApiListPage;
import cross.platform.test.suite.pageobject.PageObjectFactory;
import cross.platform.test.suite.properties.MobileConfig;
import io.appium.java_client.AppiumDriver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;

@Slf4j
@Guice
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FollowTest implements ReportHelper, ScreenshotHelper, ScreenRecordingHelper {

    public static final String GROUP = "FollowTest";

    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
    private final ReportManager reportManager = new ReportManager();
    private final LoggingAssertion assertion = new LoggingAssertion(reportManager, log);
    
    public AppiumDriver getAppiumDriver() {
        return this.getDriverManager().getDriver();
    }

    @Screenshot
    @Test(description = "followTest description...")
    public void followTest() {
        log.debug(this.mobileConfig.getServerArguments().getAddress());
        log.debug(this.driverManager.getDriver().getRemoteAddress().toString());

        ApiListPage apiListPage = PageObjectFactory.getApiListPage(getAppiumDriver());
        assertion.assertEquals("Page title", "API Demos", apiListPage.getTitle());
        assertion.assertEquals("Check page title", "My page", "my page");
        assertion.assertEquals("Check OK button label", "OK", "OK");
        assertion.assertEquals("Check Login button label", "Login", "login");
        assertion.assertEquals("Check Cancel button label", "Cancel", "Cancel");
    }

    @Screenshot
    @Test(description = "followTest2 description...", dependsOnMethods = "followTest")
    public void followTest2() {
        ApiListPage apiListPage = PageObjectFactory.getApiListPage(getAppiumDriver());
        
        assertion.assertEquals("Page title", "API Demos", apiListPage.getTitle());
        assertion.assertEquals("Accessibility tab label", "Accessibility", apiListPage.getAccessibilityTab());
        apiListPage.clickAccessibilityTab();
        apiListPage.back();
        assertion.assertEquals("Check page title", "Follow page", "Follow page");
        assertion.assertEquals("Check delete button label", "Delete", "Delete");
        assertion.assertEquals("Check copy button label", "Copy", "Copy");
        assertion.assertEquals("Check edit button label", "Edit", "Edit");
    }
}
