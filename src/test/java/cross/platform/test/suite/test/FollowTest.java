package cross.platform.test.suite.test;

import cross.platform.test.suite.assertion.SoftAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.properties.MobileConfig;
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
public class FollowTest implements ReportTest {
    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
    private final ReportManager reportManager;
    
    public void followTest() {
        log.debug(this.mobileConfig.getServerArguments().getAddress());
        log.debug(this.driverManager.getDriver().getRemoteAddress().toString());
        
        SoftAssertion softAssertion = new SoftAssertion(this.reportManager);
        softAssertion.assertEquals("Check page title", "My page", "my page");
        softAssertion.assertEquals("Check OK button label", "OK", "OK");
        softAssertion.assertEquals("Check Login button label", "Login", "login");
        softAssertion.assertEquals("Check Cancel button label", "Cancel", "Cancel");
        softAssertion.assertAll();
    }

    @Test(dependsOnMethods = "followTest")
    public void followTest2() {
        SoftAssertion softAssertion = new SoftAssertion(this.reportManager);
        softAssertion.assertEquals("Check page title", "Follow page", "Follow page");
        softAssertion.assertEquals("Check delete button label", "Delete", "delete");
        softAssertion.assertEquals("Check copy button label", "Copy", "copy");
        softAssertion.assertEquals("Check edit button label", "Edit", "Edit");
        softAssertion.assertAll();
    }
}
