package cross.platform.test.suite.pageobject;

public interface SideNavigationPage {

    String getMenuItemLabel(String label);

    void clickMenuItem(String label);

    void scrollToTop();

    CatalogPage goToCatalog();
}