package cross.platform.test.suite.guicemodule;

import cross.platform.test.suite.guicemodule.common.BaseModule;
import cross.platform.test.suite.properties.ConfigMap;

import javax.inject.Inject;

public class AllTestsModule extends BaseModule {
    
    @Inject
    public AllTestsModule(ConfigMap configMap, String configMappingPath) {
        super(configMap, configMappingPath);
    }
}
