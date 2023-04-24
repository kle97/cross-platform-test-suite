package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import cross.platform.test.suite.properties.*;
import cross.platform.test.suite.utility.ConfigUtil;

public class TestConfigModule extends AbstractModule {

    private final ConfigMap configMap;
    private final String configMappingPath;
    
    public TestConfigModule(ConfigMap configMap, String configMappingPath) {
        this.configMap = configMap;
        this.configMappingPath = configMappingPath;
    }

    @Override
    protected void configure() {
        ConfigMapping configMapping = ConfigUtil.readJsonFileAs(configMappingPath, ConfigMapping.class);
        MobileConfig mobileConfig = configMap.getMobileConfigMap().get(configMapping.getMobileConfig());
        UserInfo userInfo = configMap.getUserInfoMap().get(configMapping.getUserInfo());
        bind(MobileConfig.class).toInstance(mobileConfig);
        bind(UserInfo.class).toInstance(userInfo);
        bind(TestConfig.class).in(Scopes.SINGLETON);
    }
}
