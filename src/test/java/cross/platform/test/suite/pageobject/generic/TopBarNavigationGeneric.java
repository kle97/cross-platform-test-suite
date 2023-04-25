package cross.platform.test.suite.pageobject.generic;

import cross.platform.test.suite.pageobject.AbstractPage;
import cross.platform.test.suite.pageobject.SideNavigationPage;
import cross.platform.test.suite.pageobject.TopBarNavigation;
import cross.platform.test.suite.service.POMFactory;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public abstract class TopBarNavigationGeneric extends AbstractPage implements TopBarNavigation {

    @AndroidFindBy(accessibility = "open menu")
    private WebElement sideMenuButton;

    @AndroidFindBy(accessibility = "sort button")
    private WebElement sortButton;

    @AndroidFindBy(accessibility = "cart badge")
    private WebElement cartBadge;

    public TopBarNavigationGeneric(AppiumDriver appiumDriver, POMFactory pomFactory) {
        super(appiumDriver, pomFactory);
    }

    @Override
    public SideNavigationPage clickSideMenu() {
        this.sideMenuButton.click();
        return pomFactory().get(SideNavigationPage.class);
    }

    @Override
    public void clickSortButton() {
        this.sortButton.click();
    }

    @Override
    public void clickCartBadge() {
        this.cartBadge.click();
    }
}