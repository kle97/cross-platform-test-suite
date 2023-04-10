package cross.platform.test.suite.configuration.guicemodule;

import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.constant.TestConst;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.properties.UserConfig;
import cross.platform.test.suite.utility.ConfigUtil;
import cross.platform.test.suite.utility.JacksonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class ParentModule extends AbstractModule {
    
    @Override
    protected void configure() {
        if (!ConfigUtil.isParallel()) {
            install(new ConfigModule(TestConst.DEFAULT_TEST_CONFIG_PATH));
        }
    }
    
    @Provides
    @Singleton
    protected DriverManager provideDriverManager() {
        return new DriverManager();
    }

    @Provides
    @Singleton
    @Named("mobileConfig")
    protected Map<String, MobileConfig> provideMobileConfigMap() {
        return this.readConfigMapConfigFromFile(MobileConfig.class, TestConst.MOBILE_CONFIG_MAP_PATH);
    }

    @Provides
    @Singleton
    @Named("userConfig")
    protected Map<String, UserConfig> provideUserConfigMap() {
        return this.readConfigMapConfigFromFile(UserConfig.class, TestConst.USER_CONFIG_MAP_PATH);
    }

    protected <T> Map<String, T> readConfigMapConfigFromFile(Class<T> configClass, String filePath) {
        try {
            Properties configAsProperties = ConfigUtil.readJsonFileAsProperties(filePath, true);
            MapType mapType = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, configClass);
            return JacksonUtil.getDefaultJavaPropsMapper().readPropertiesAs(configAsProperties, mapType);
        } catch (IOException ex) {
            log.debug(ex.getMessage());
            return null;
        }
    }
}
