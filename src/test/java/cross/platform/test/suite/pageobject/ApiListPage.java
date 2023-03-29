package cross.platform.test.suite.pageobject;

import cross.platform.test.suite.constant.Direction;
import cross.platform.test.suite.utility.DriverUtil;
import cross.platform.test.suite.utility.SwiperUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidBy;
import io.appium.java_client.pagefactory.AndroidFindAll;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ApiListPage extends AbstractPage {
    
    @AndroidFindBy(xpath = "//*[@resource-id='android:id/action_bar']/android.widget.TextView")
    private WebElement title;
    
    @AndroidFindBy(id = "android:id/list")
    private WebElement apiListContainer;

    @AndroidFindBy(id = "android:id/list")
    @AndroidFindAll(@AndroidBy(id = "android:id/text1"))
    private List<WebElement> apiList;
    
    public ApiListPage(AppiumDriver appiumDriver) {
        super(appiumDriver);
    }
    
    public String getTitle() {
        return DriverUtil.getText(this.title);
    }
    
    public String getTabLabel(String label) {
        WebElement element = SwiperUtil.findElementInScrollableContainerWithText(getDriver(), apiListContainer, apiList, label, Direction.DOWN);
        return DriverUtil.getText(element);
    }

    public void clickTab(String label) {
        WebElement element = SwiperUtil.findElementInScrollableContainerWithText(getDriver(), apiListContainer, apiList, label, Direction.DOWN);
        element.click();
    }
    
    public void scrollToTop() {
        for (int i = 0; i < SwiperUtil.DEFAULT_MAX_SCROLL_SEARCH; i++) {
            String beforeScrollingPageSource = DriverUtil.getPageSource(getDriver());
            SwiperUtil.scrollByElementSizePercentage(getDriver(), apiListContainer, Direction.UP, 0.93);
            if (DriverUtil.getPageSource(getDriver()).equals(beforeScrollingPageSource)) {
                break;
            }
        }
    }
}
