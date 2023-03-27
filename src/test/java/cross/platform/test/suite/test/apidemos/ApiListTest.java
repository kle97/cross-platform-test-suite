package cross.platform.test.suite.test.apidemos;

import cross.platform.test.suite.annotation.ScreenRecord;
import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.pageobject.ApiListPage;
import cross.platform.test.suite.pageobject.PageObjectFactory;
import cross.platform.test.suite.test.common.BaseTest;
import io.appium.java_client.AppiumDriver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
@Guice
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ApiListTest extends BaseTest {

    private final DriverManager driverManager;
    private final ReportManager reportManager = new ReportManager();
    private final LoggingAssertion assertion = new LoggingAssertion(reportManager, log);

    public AppiumDriver getDriver() {
        return this.driverManager.getDriver();
    }
    
    @Screenshot
    @ScreenRecord
    @Test(description = "verify Api Demos list.")
    public void verifyApiList(Method method) {
        ApiListPage apiListPage = PageObjectFactory.getApiListPage(getDriver());
        
        assertion.assertEquals("Title", "Api Demos", apiListPage.getTitle());
        List<String> tabLabelList = List.of("Access'ibility", "Accessibility", "Animation", "App", "Content", 
                                       "Graphics", "Media", "NFC", "OS", "Preference", "Text", "Views");
        for (String tabLabel: tabLabelList) {
            reportManager.appendChildReport(method.getName(), tabLabel);
            assertion.assertEquals(tabLabel + " tab label", tabLabel, apiListPage.getTabLabel(tabLabel));
            takeScreenshot(tabLabel);
        }
    }
}
