package cross.platform.test.suite.pageobject;

public interface ApiListPage extends Page {
    String getTitle();

    String getTabLabel(String label);

    void clickTab(String label);

    void scrollToTop();
    
    
}
