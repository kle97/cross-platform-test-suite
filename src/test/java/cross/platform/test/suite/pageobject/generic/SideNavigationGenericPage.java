package cross.platform.test.suite.pageobject.generic;

import cross.platform.test.suite.constant.Direction;
import cross.platform.test.suite.pageobject.AbstractPage;
import cross.platform.test.suite.pageobject.CatalogPage;
import cross.platform.test.suite.pageobject.SideNavigationPage;
import cross.platform.test.suite.service.POMFactory;
import cross.platform.test.suite.utility.DriverUtil;
import cross.platform.test.suite.utility.SwiperUtil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidBy;
import io.appium.java_client.pagefactory.AndroidFindAll;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SideNavigationGenericPage extends AbstractPage implements SideNavigationPage {

    @AndroidFindBy(xpath = "//android.widget.ScrollView")
    private WebElement menuItemContainer;

    @AndroidFindAll(@AndroidBy(xpath = "//android.view.ViewGroup/android.widget.TextView"))
    private List<WebElement> menuItemList;

    public SideNavigationGenericPage(AppiumDriver appiumDriver, POMFactory pomFactory) {
        super(appiumDriver, pomFactory);
    }

    @Override
    public String getMenuItemLabel(String label) {
        WebElement element = SwiperUtil.findElementInScrollableContainerWithText(driver(), menuItemContainer, menuItemList, label, Direction.DOWN);
        return DriverUtil.getText(element);
    }

    @Override
    public void clickMenuItem(String label) {
        WebElement element = SwiperUtil.findElementInScrollableContainerWithText(driver(), menuItemContainer, menuItemList, label, Direction.DOWN);
        element.click();
    }

    @Override
    public void scrollToTop() {
        for (int i = 0; i < SwiperUtil.DEFAULT_MAX_SCROLL_SEARCH; i++) {
            String beforeScrollingPageSource = DriverUtil.getPageSource(driver());
            SwiperUtil.scrollByElementSizePercentage(driver(), menuItemContainer, Direction.UP, 0.93);
            if (DriverUtil.getPageSource(driver()).equals(beforeScrollingPageSource)) {
                break;
            }
        }
    }

    @Override
    public CatalogPage goToCatalog() {
        this.clickMenuItem("Catalog");
        return pomFactory().get(CatalogPage.class);
    }
}