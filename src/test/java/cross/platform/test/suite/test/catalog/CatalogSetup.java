package cross.platform.test.suite.test.catalog;

import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.test.common.AbstractTestSetup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;

import javax.inject.Inject;

@Slf4j
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Guice(modules = CatalogModule.class)
public class CatalogSetup extends AbstractTestSetup {
    
    private final MobileConfig mobileConfig;
    private final DriverManager driverManager;
}
