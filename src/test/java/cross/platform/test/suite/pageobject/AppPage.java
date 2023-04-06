package cross.platform.test.suite.pageobject;

public interface AppPage extends Page {

    String getTitle();

    String getAppLabel(String label);

    void clickTab(String label);

    void scrollToTop();
    
    ApiListPage back();
}
