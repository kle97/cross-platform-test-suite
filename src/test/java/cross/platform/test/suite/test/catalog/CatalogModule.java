package cross.platform.test.suite.test.catalog;

import com.google.inject.AbstractModule;
import cross.platform.test.suite.configuration.guicemodule.TestConfigModule;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.ConfigMap;
import cross.platform.test.suite.utility.ConfigUtil;

import javax.inject.Inject;

public class CatalogModule extends AbstractModule {

    private final ConfigMap configMap;

    @Inject
    public CatalogModule(ConfigMap configMap) {
        this.configMap = configMap;
    }

    @Override
    protected void configure() {
        if (ConfigUtil.isParallel()) {
            install(new TestConfigModule(configMap, TestConst.CONFIG_MAPPING_1_PATH));
        }
    }
}
