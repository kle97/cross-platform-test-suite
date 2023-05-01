package cross.platform.test.suite.pageobject;

import cross.platform.test.suite.pageobject.common.Page;

public interface TopBarNavigation extends Page {

    SideNavigationPage clickSideMenu();

    void clickSortButton();

    void clickCartBadge();
}