package cross.platform.test.suite.guicemodule.common;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import cross.platform.test.suite.pageobject.common.AbstractPage;
import cross.platform.test.suite.properties.*;
import cross.platform.test.suite.service.LoggingAssertion;
import cross.platform.test.suite.service.POMFactory;
import cross.platform.test.suite.service.Reporter;
import cross.platform.test.suite.utility.ConfigUtil;

import java.util.Map;

public class BaseModule extends AbstractModule {
    
    private final ConfigMap configMap;
    private final String configMappingPath;

    public BaseModule(ConfigMap configMap, String configMappingPath) {
        this.configMap = configMap;
        this.configMappingPath = configMappingPath;
    }

    @Override
    protected void configure() {
        TestConfig testConfig = createAndBindTestConfig();
        bind(LoggingAssertion.class).in(Scopes.SINGLETON);
        bind(Reporter.class).in(Scopes.SINGLETON);
        bindPageObject(binder());
    }
    
    protected TestConfig createAndBindTestConfig() {
        ConfigMapping configMapping = ConfigUtil.readJsonFileAs(this.configMappingPath, ConfigMapping.class);
        MobileConfig mobileConfig = configMap.getMobileConfigMap().get(configMapping.getMobileConfig());
        UserInfo userInfo = configMap.getUserInfoMap().get(configMapping.getUserInfo());
        TestConfig testConfig = new TestConfig(mobileConfig, userInfo);
        
        bind(MobileConfig.class).toInstance(mobileConfig);
        bind(UserInfo.class).toInstance(userInfo);
        bind(TestConfig.class).toInstance(testConfig);
        return testConfig;
    }
    
    protected void bindPageObject(Binder binder) {
        MapBinder<Class<?>, AbstractPage> genericMapBinder = MapBinder.newMapBinder(binder, new TypeLiteral<>() {}, new TypeLiteral<>() {}, Names.named("genericMap"));
        MapBinder<Class<?>, AbstractPage> iOSMapBinder = MapBinder.newMapBinder(binder, new TypeLiteral<>() {}, new TypeLiteral<>() {}, Names.named("iOSMap"));
        MapBinder<Class<?>, AbstractPage> androidMapBinder = MapBinder.newMapBinder(binder, new TypeLiteral<>() {}, new TypeLiteral<>() {}, Names.named("androidMap"));

        for (Map.Entry<Class<?>, Class<? extends AbstractPage>> entry : POMFactory.getGenericMap().entrySet()) {
            genericMapBinder.addBinding(entry.getKey()).to(entry.getValue());
        }

        for (Map.Entry<Class<?>, Class<? extends AbstractPage>> entry : POMFactory.getIOSMap().entrySet()) {
            iOSMapBinder.addBinding(entry.getKey()).to(entry.getValue());
        }

        for (Map.Entry<Class<?>, Class<? extends AbstractPage>> entry : POMFactory.getAndroidMap().entrySet()) {
            androidMapBinder.addBinding(entry.getKey()).to(entry.getValue());
        }
    }
}
