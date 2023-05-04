package cross.platform.test.suite.guicemodule;

import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.guicemodule.common.BaseModule;
import cross.platform.test.suite.properties.ConfigMap;

import javax.inject.Inject;

public class DefaultTestModule extends BaseModule {
    
    @Inject
    public DefaultTestModule(ConfigMap configMap) {
        super(configMap, TestConst.DEFAULT_CONFIG_MAPPING_PATH);
    }
}
