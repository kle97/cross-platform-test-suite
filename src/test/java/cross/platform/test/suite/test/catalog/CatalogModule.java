package cross.platform.test.suite.test.catalog;

import com.google.inject.AbstractModule;
import cross.platform.test.suite.configuration.guicemodule.ConfigModule;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.utility.ConfigUtil;

public class CatalogModule extends AbstractModule {

    @Override
    protected void configure() {
        if (ConfigUtil.isParallel()) {
            install(new ConfigModule(TestConst.TEST_CONFIG_1_PATH));
        }
        
        
    }
}
