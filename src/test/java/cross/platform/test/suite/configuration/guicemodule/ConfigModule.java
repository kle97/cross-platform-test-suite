package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import cross.platform.test.suite.annotation.MobileConfigMap;
import cross.platform.test.suite.annotation.UserInfoMap;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.pageobject.factory.POMFactory;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.properties.TestConfig;
import cross.platform.test.suite.properties.TestConfigMap;
import cross.platform.test.suite.properties.UserInfo;
import cross.platform.test.suite.utility.ConfigUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ConfigModule extends AbstractModule {
    
    private final String testConfigMapPath;

    @Provides
    @Singleton
    public TestConfigMap provideTestConfigMap() {
        return ConfigUtil.readJsonFileAs(TestConfigMap.class, testConfigMapPath);
    }

    @Provides
    @Singleton
    public MobileConfig provideMobileConfig(@MobileConfigMap Map<String, MobileConfig> mobileConfigMap, TestConfigMap testConfigMap) {
        return mobileConfigMap.get(testConfigMap.getMobileConfig());
    }

    @Provides
    @Singleton
    public UserInfo provideUserInfo(@UserInfoMap Map<String, UserInfo> userInfoMap, TestConfigMap testConfigMap) {
        return userInfoMap.get(testConfigMap.getUserInfo());
    }

    @Provides
    @Singleton
    public TestConfig provideTestConfig(MobileConfig mobileConfig, UserInfo userInfo) {
        return new TestConfig(mobileConfig, userInfo);
    }

    @Provides
    @Singleton
    public POMFactory providePOMFactory(DriverManager driverManager, TestConfig testConfig) {
        return new POMFactory(driverManager, testConfig);
    }
}
