package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import cross.platform.test.suite.pageobject.factory.POMFactory;
import cross.platform.test.suite.properties.*;
import cross.platform.test.suite.test.common.AppiumService;
import cross.platform.test.suite.utility.ConfigUtil;

public abstract class BaseModule extends AbstractModule {
    
    protected TestConfig bindConfigs(String testConfigMapPath, ConfigMap configMap) {
        ConfigMapping configMapping = ConfigUtil.readJsonFileAs(testConfigMapPath, ConfigMapping.class);
        MobileConfig mobileConfig = configMap.getMobileConfigMap().get(configMapping.getMobileConfig());
        UserInfo userInfo = configMap.getUserInfoMap().get(configMapping.getUserInfo());
        TestConfig testConfig = new TestConfig(mobileConfig, userInfo);

        bind(MobileConfig.class).toInstance(mobileConfig);
        bind(UserInfo.class).toInstance(userInfo);
        bind(TestConfig.class).toInstance(testConfig);
        return testConfig;
    }
    
    protected void bindEssentials() {
        bind(POMFactory.class).in(Scopes.SINGLETON);
        bind(AppiumService.class).in(Scopes.SINGLETON);
    }
}
