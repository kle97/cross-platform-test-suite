package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.pageobject.factory.POMFactory;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.properties.TestConfig;
import cross.platform.test.suite.properties.UserConfig;
import cross.platform.test.suite.utility.ConfigUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ConfigModule extends AbstractModule {
    
    private final String testConfigPath;

    @Provides
    @Singleton
    public TestConfig provideTestConfig() {
        return ConfigUtil.readJsonFileAs(TestConfig.class, testConfigPath);
    }

    @Provides
    @Singleton
    public MobileConfig provideMobileConfig(@Named("mobileConfig") Map<String, MobileConfig> mobileConfigMap, TestConfig testConfig) {
        return mobileConfigMap.get(testConfig.getMobileConfig());
    }

    @Provides
    @Singleton
    public UserConfig provideUserConfig(@Named("userConfig") Map<String, UserConfig> mobileUserConfig, TestConfig testConfig) {
        return mobileUserConfig.get(testConfig.getUserConfig());
    }

    @Provides
    @Singleton
    public POMFactory providePOFactory(DriverManager driverManager, MobileConfig mobileConfig) {
        return new POMFactory(driverManager, mobileConfig);
    }
}
