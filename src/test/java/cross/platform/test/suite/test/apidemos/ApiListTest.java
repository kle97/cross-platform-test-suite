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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Guice
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ScreenRecord
public class ApiListTest extends BaseTest {

    private final DriverManager driverManager;
    private final ReportManager reportManager = new ReportManager();
    private final LoggingAssertion assertion = new LoggingAssertion(reportManager, log);

    public AppiumDriver getDriver() {
        return this.driverManager.getDriver();
    }
    
    @DataProvider(name = "tabLabelProvider")
    public Iterator<Object[]> tabLabelProvider() {
        List<String> tabLabelList = List.of("Access'ibility", "Accessibility", "Animation", "App", "Content",
                                            "Graphics", "Media", "NFC", "OS", "Preference", "Text", "Views");
        return tabLabelList.stream().map(tab -> new Object[] {tab}).iterator();
    }

    @Screenshot
    @Test
    public void verifyPageTitle() {
        ApiListPage apiListPage = PageObjectFactory.getApiListPage(getDriver());
        assertion.assertEquals("Title", "Api Demos", apiListPage.getTitle());
    }
    
    @Screenshot
    @Test(dataProvider = "tabLabelProvider", dependsOnMethods = "verifyPageTitle")
    public void verifyApiList(String tabLabel) {
        ApiListPage apiListPage = PageObjectFactory.getApiListPage(getDriver());
        assertion.assertEquals(tabLabel + " tab label", tabLabel, apiListPage.getTabLabel(tabLabel));
    }
}
