package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.pageobject.factory.POFactory;
import cross.platform.test.suite.properties.MobileConfig;
import cross.platform.test.suite.utility.ConfigUtil;
import cross.platform.test.suite.utility.JacksonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public abstract class AbstractLocalModule extends AbstractModule {

    public abstract String getMobileConfigPath();
    
    @Override
    protected void configure() {
        
    }

    @Provides
    @Singleton
    public MobileConfig mobileConfig() {
        return this.readMobileConfigFromFile(getMobileConfigPath());
    }
    
    @Provides
    @Singleton
    public POFactory providePOFactory(DriverManager driverManager, MobileConfig mobileConfig) {
        return new POFactory(driverManager, mobileConfig);
    }

    protected MobileConfig readMobileConfigFromFile(String filePath) {
        try {
            Properties configAsProperties = ConfigUtil.readJsonFileAsProperties(filePath, true);
            return JacksonUtil.getDefaultJavaPropsMapper().readPropertiesAs(configAsProperties, MobileConfig.class);
        } catch (IOException ex) {
            log.debug(ex.getMessage());
            return null;
        }
    }
}
