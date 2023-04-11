package cross.platform.test.suite.pageobject;

public interface SideNavigationPage extends Page {

    String getMenuItemLabel(String label);

    void clickMenuItem(String label);

    void scrollToTop();

    CatalogPage goToCatalog();
}