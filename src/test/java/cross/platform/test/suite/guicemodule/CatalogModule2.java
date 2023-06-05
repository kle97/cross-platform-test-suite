package cross.platform.test.suite.guicemodule;

import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.guicemodule.common.BaseModule;
import cross.platform.test.suite.properties.ConfigMap;

import javax.inject.Inject;

public class CatalogModule2 extends BaseModule {
    
    @Inject
    public CatalogModule2(ConfigMap configMap) {
        super(configMap, TestConst.CONFIG_MAPPING_2_PATH);
    }
}
