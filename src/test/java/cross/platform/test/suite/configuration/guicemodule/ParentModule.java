package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.ConfigMap;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.properties.UserInfo;
import cross.platform.test.suite.service.DriverManager;
import cross.platform.test.suite.service.LoggingAssertion;
import cross.platform.test.suite.service.Reporter;
import cross.platform.test.suite.utility.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ParentModule extends AbstractModule {
    
    @Override
    protected void configure() {
        Map<String, MobileConfig> mobileConfigMap = ConfigUtil.readConfigMapConfigFromFile(TestConst.MOBILE_CONFIG_MAP_PATH, MobileConfig.class);
        Map<String, UserInfo> userInfoMap = ConfigUtil.readConfigMapConfigFromFile(TestConst.USER_INFO_MAP_PATH, UserInfo.class);
        ConfigMap configMap = new ConfigMap(mobileConfigMap, userInfoMap);
        
        bind(ConfigMap.class).toInstance(configMap);
        bind(DriverManager.class).in(Scopes.SINGLETON);
        bind(LoggingAssertion.class).in(Scopes.SINGLETON);
        bind(Reporter.class).in(Scopes.SINGLETON);
        
        install(new PageObjectModule());
        
        if (!ConfigUtil.isParallel()) {
            install(new TestConfigModule(configMap, TestConst.DEFAULT_CONFIG_MAPPING_PATH));
        }
    }
}
