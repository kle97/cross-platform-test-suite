package cross.platform.test.suite.guicemodule;

import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.guicemodule.common.BaseModule;
import cross.platform.test.suite.properties.ConfigMap;

import javax.inject.Inject;

public class CatalogModule extends BaseModule {
    @Inject
    public CatalogModule(ConfigMap configMap) {
        super(configMap, TestConst.CONFIG_MAPPING_1_PATH);
    }
}
