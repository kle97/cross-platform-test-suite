package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import cross.platform.test.suite.configuration.manager.DriverManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
public class ParentModule extends AbstractModule {
    
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    protected DriverManager provideDriverManager() {
        return new DriverManager();
    }
}
