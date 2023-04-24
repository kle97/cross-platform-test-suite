package cross.platform.test.suite.test.catalog;

import cross.platform.test.suite.test.common.AbstractTestSetup;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Guice;

@Slf4j
@Guice(modules = CatalogModule.class)
public class CatalogSetup extends AbstractTestSetup {

}
