package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import cross.platform.test.suite.configuration.listener.GuiceInjectorListener;
import cross.platform.test.suite.pageobject.ApiListGenericPage;
import cross.platform.test.suite.pageobject.ApiListPage;
import cross.platform.test.suite.properties.MobileConfig;
import org.openqa.selenium.Platform;

public class PageFactoryModule extends AbstractModule {
    
    private final MobileConfig mobileConfig;

    public PageFactoryModule(MobileConfig mobileConfig) {
        this.mobileConfig = mobileConfig;
    }

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new GuiceInjectorListener());
        Platform platform = this.mobileConfig.getDesiredCapabilities().getPlatformName();
        if (platform.is(Platform.IOS)) {
            // TODO
        } else if(platform.is(Platform.ANDROID)) {
            // TODO
        }
        
        bind(ApiListPage.class).to(ApiListGenericPage.class).in(Scopes.SINGLETON);
    }
}
