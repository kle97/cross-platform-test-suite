package cross.platform.test.suite.test.factory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import cross.platform.test.suite.guicemodule.CatalogModule;
import cross.platform.test.suite.test.catalog.CatalogTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.annotations.Factory;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

@Slf4j
@Guice(modules = CatalogModule.class)
@Test(testName = "Catalog")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CatalogFactory {
    
    private final Provider<CatalogTest> catalogTest;
    
    @Factory
    public Object[] catalogFactory(ITestContext testContext) {
        return new Object[] {
                catalogTest.get(),
                catalogTest.get(),
        };
    }
}
