package cross.platform.test.suite.test.catalog;

import cross.platform.test.suite.annotation.AppendReport;
import cross.platform.test.suite.annotation.ScreenRecord;
import cross.platform.test.suite.annotation.Screenshot;
import cross.platform.test.suite.guicemodule.common.ModuleFactory;
import cross.platform.test.suite.model.Product;
import cross.platform.test.suite.pageobject.CatalogPage;
import cross.platform.test.suite.properties.UserInfo;
import cross.platform.test.suite.service.DriverManager;
import cross.platform.test.suite.service.LoggingAssertion;
import cross.platform.test.suite.service.POMFactory;
import cross.platform.test.suite.service.Reporter;
import cross.platform.test.suite.test.common.BaseTest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@ScreenRecord
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Guice(moduleFactory = ModuleFactory.class)
public class CatalogTest extends BaseTest {

    private final UserInfo userInfo;
    private final DriverManager driverManager;
    private final POMFactory pomFactory;
    private final Reporter reporter;
    private final LoggingAssertion assertion;

    private List<Product> productList;
    private int index;

    @DataProvider(name = "productProvider")
    public Iterator<Object[]> productProvider() {
        List<String> productNamelList = List.of("Sauce Labs Backpack", "Sauce Labs Bike Light",
                                                "Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket",
                                                "Sauce Labs Onesie", "Test.allTheThings() T-Shirt");
        List<String> productPricelList = List.of("$29.99", "$9.99", "$15.99", "$49.99", "$7.99", "$15.99");

        List<Object[]> productList = new ArrayList<>();
        for (int i = 0; i < productNamelList.size(); i++) {
            productList.add(new Object[] {productNamelList.get(i), productPricelList.get(i)});
        }
        return productList.iterator();
    }

    @Test 
    @Screenshot
    @AppendReport(description = "Verify catalog page title.")
    public void verifyCatalogTitle() {
        assertion.assertEquals("Username", "bod@example.com", userInfo.getUsername());
        assertion.assertEquals("Password", "10203040", userInfo.getPassword());
        assertion.assertEquals("Title", "Product", pomFactory.get(CatalogPage.class).getCatalogTitle());
    }

    @Test(dependsOnMethods = "verifyCatalogTitle")
    @AppendReport(description = "Verify catalog products.")
    public void verifyCatalog() {
        this.productList = pomFactory.get(CatalogPage.class).getProductList(reporter.getCurrentReport());
        this.index = 0;
    }

    @Test(dataProvider = "productProvider", dependsOnMethods = "verifyCatalog")
    public void verifyProducts(String productName, String productPrice) {
        reporter.appendChildReport("verifyCatalog", productName);
        Product product = this.productList.get(index++);
        assertion.assertEquals(productName, productName, product.getProductName());
        assertion.assertEquals(productPrice, productPrice, product.getProductPrice());
    }

    @Screenshot
    @AppendReport(description = "Verify scroll to top")
    @Test(dependsOnMethods = "verifyProducts")
    public void scrollBackToTop() {
        pomFactory.get(CatalogPage.class).scrollToTop();
    }
}