package cross.platform.test.suite.test.catalog;

import cross.platform.test.suite.configuration.guicemodule.BaseModule;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.ConfigMap;
import cross.platform.test.suite.properties.TestConfig;
import cross.platform.test.suite.utility.ConfigUtil;

import javax.inject.Inject;

public class CatalogModule extends BaseModule {

    private final ConfigMap configMap;

    @Inject
    public CatalogModule(ConfigMap configMap) {
        this.configMap = configMap;
    }

    @Override
    protected void configure() {
        if (ConfigUtil.isParallel()) {
            TestConfig testConfig = this.bindConfigs(TestConst.CONFIG_MAPPING_1_PATH, configMap);
            bindEssentials();
        }
    }
}
