package cross.platform.test.suite.test.apidemos;

import cross.platform.test.suite.annotation.ScreenRecord;
import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.assertion.LoggingAssertion;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.configuration.manager.ReportManager;
import cross.platform.test.suite.pageobject.ApiListPage;
import cross.platform.test.suite.pageobject.factory.POFactory;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.test.common.BaseTest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;

@Slf4j
@ScreenRecord
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Guice(modules = ApiDemosModule.class)
public class ApiListTest extends BaseTest {

    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
    private final ReportManager reportManager = new ReportManager();
    private final LoggingAssertion assertion = new LoggingAssertion(reportManager, log);
    private final POFactory factory;
    
    @DataProvider(name = "tabLabelProvider")
    public Iterator<Object[]> tabLabelProvider() {
        List<String> tabLabelList = List.of("Access'ibility", "Accessibility", "Animation", "App", "Content",
                                            "Graphics", "Media", "NFC", "OS", "Preference", "Text", "Views");
        return tabLabelList.stream().map(tab -> new Object[] {tab}).iterator();
    }

    @Screenshot
    @Test(description = "Verify page title.")
    public void verifyPageTitle() {
        assertion.assertEquals("Title", "Api Demos", factory.get(ApiListPage.class).getTitle());
    }
    
    @Screenshot
    @Test(dataProvider = "tabLabelProvider", dependsOnMethods = "verifyPageTitle")
    public void verifyApiList(String tabLabel) {
        reportManager.setCurrentReportName(tabLabel);
        assertion.assertEquals(tabLabel + " tab label", tabLabel, factory.get(ApiListPage.class).getTabLabel(tabLabel));
    }

    @Screenshot
    @Test(description = "Verify scroll to top", dependsOnMethods = "verifyApiList")
    public void scrollBackToTop() {
        factory.get(ApiListPage.class).scrollToTop();
    }
}
