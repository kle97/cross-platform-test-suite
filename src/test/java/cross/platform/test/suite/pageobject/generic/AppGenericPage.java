package cross.platform.test.suite.pageobject.generic;

import cross.platform.test.suite.constant.Direction;
import cross.platform.test.suite.pageobject.AbstractPage;
import cross.platform.test.suite.pageobject.ApiListPage;
import cross.platform.test.suite.pageobject.AppPage;
import cross.platform.test.suite.pageobject.factory.POMFactory;
import cross.platform.test.suite.utility.DriverUtil;
import cross.platform.test.suite.utility.SwiperUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidBy;
import io.appium.java_client.pagefactory.AndroidFindAll;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

public class AppGenericPage extends AbstractPage implements AppPage {

    @AndroidFindBy(xpath = "//*[@resource-id='android:id/action_bar']/android.widget.TextView")
    private WebElement title;

    @AndroidFindBy(id = "android:id/list")
    private WebElement apiListContainer;

    @AndroidFindBy(id = "android:id/list")
    @AndroidFindAll(@AndroidBy(id = "android:id/text1"))
    private List<WebElement> apiList;

    public AppGenericPage(AppiumDriver appiumDriver, POMFactory pomFactory) {
        super(appiumDriver, pomFactory);
    }

    @Override
    public String getTitle() {
        return DriverUtil.getText(this.title);
    }

    @Override
    public String getAppLabel(String label) {
        WebElement element = SwiperUtil.findElementInScrollableContainerWithText(driver(), apiListContainer, apiList, label, Direction.DOWN);
        return DriverUtil.getText(element);
    }

    @Override
    public void clickTab(String label) {
        WebElement element = SwiperUtil.findElementInScrollableContainerWithText(driver(), apiListContainer, apiList, label, Direction.DOWN);
        element.click();
    }

    @Override
    public void scrollToTop() {
        for (int i = 0; i < SwiperUtil.DEFAULT_MAX_SCROLL_SEARCH; i++) {
            String beforeScrollingPageSource = DriverUtil.getPageSource(driver());
            SwiperUtil.scrollByElementSizePercentage(driver(), apiListContainer, Direction.UP, 0.93);
            if (DriverUtil.getPageSource(driver()).equals(beforeScrollingPageSource)) {
                break;
            }
        }
    }

    @Override
    public ApiListPage back() {
        driver().navigate().back();
        return pomFactory().get(ApiListPage.class);
    }
}
