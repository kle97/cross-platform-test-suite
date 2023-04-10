package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractLocalModule extends AbstractModule {

    public abstract String getMobileConfigPath();
    
    @Override
    protected void configure() {
        install(new ConfigModule(getMobileConfigPath()));
    }
}
