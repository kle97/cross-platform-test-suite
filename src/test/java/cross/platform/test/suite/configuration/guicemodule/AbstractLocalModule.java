package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
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
        MobileConfig mobileConfig = this.readMobileConfigFromFile(getMobileConfigPath());
        bind(MobileConfig.class).toInstance(mobileConfig);
        install(new PageFactoryModule(mobileConfig));
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
