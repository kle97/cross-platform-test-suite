package cross.platform.test.suite.test.factory;

import com.google.inject.Inject;
import cross.platform.test.suite.guicemodule.CatalogModule2;
import cross.platform.test.suite.test.catalog.CatalogTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.annotations.Factory;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

@Slf4j
@Guice(modules = CatalogModule2.class)
@Test(testName = "Catalog2")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CatalogFactory2 {

    private final CatalogTest catalogTest;

    @Factory
    public Object[] catalogFactory(ITestContext testContext) {
        return new Object[] {
                catalogTest,
        };
    }
}
