package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.Scopes;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.ConfigMap;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.properties.TestConfig;
import cross.platform.test.suite.properties.UserInfo;
import cross.platform.test.suite.utility.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ParentModule extends BaseModule {
    
    @Override
    protected void configure() {
        Map<String, MobileConfig> mobileConfigMap = ConfigUtil.readConfigMapConfigFromFile(TestConst.MOBILE_CONFIG_MAP_PATH, MobileConfig.class);
        Map<String, UserInfo> userInfoMap = ConfigUtil.readConfigMapConfigFromFile(TestConst.USER_INFO_MAP_PATH, UserInfo.class);
        ConfigMap configMap = new ConfigMap(mobileConfigMap, userInfoMap);
        
        bind(ConfigMap.class).toInstance(configMap);
        bind(DriverManager.class).in(Scopes.SINGLETON);
        
        if (!ConfigUtil.isParallel()) {
            TestConfig testConfig = this.bindConfigs(TestConst.DEFAULT_CONFIG_MAPPING_PATH, configMap);
            
        }
    }
}
