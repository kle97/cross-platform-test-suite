package cross.platform.test.suite.guicemodule.common;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import cross.platform.test.suite.guicemodule.PageObjectModule;
import cross.platform.test.suite.properties.*;
import cross.platform.test.suite.service.LoggingAssertion;
import cross.platform.test.suite.service.Reporter;
import cross.platform.test.suite.utility.ConfigUtil;

public class BaseModule extends AbstractModule {
    
    private final ConfigMap configMap;
    private final String configMappingPath;

    public BaseModule(ConfigMap configMap, String configMappingPath) {
        this.configMap = configMap;
        this.configMappingPath = configMappingPath;
    }

    @Override
    protected void configure() {
        TestConfig testConfig = createAndBindTestConfig();
        
        bind(LoggingAssertion.class).in(Scopes.SINGLETON);
        bind(Reporter.class).in(Scopes.SINGLETON);
        
        install(new PageObjectModule());
    }
    
    protected TestConfig createAndBindTestConfig() {
        ConfigMapping configMapping = ConfigUtil.readJsonFileAs(this.configMappingPath, ConfigMapping.class);
        MobileConfig mobileConfig = configMap.getMobileConfigMap().get(configMapping.getMobileConfig());
        UserInfo userInfo = configMap.getUserInfoMap().get(configMapping.getUserInfo());
        TestConfig testConfig = new TestConfig(mobileConfig, userInfo);
        
        bind(MobileConfig.class).toInstance(mobileConfig);
        bind(UserInfo.class).toInstance(userInfo);
        bind(TestConfig.class).toInstance(testConfig);
        return testConfig;
    }
}
