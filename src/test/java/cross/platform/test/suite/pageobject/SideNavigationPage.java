package cross.platform.test.suite.pageobject;

import cross.platform.test.suite.pageobject.common.Page;

public interface SideNavigationPage extends Page {

    String getMenuItemLabel(String label);

    void clickMenuItem(String label);

    void scrollToTop();

    CatalogPage goToCatalog();
}