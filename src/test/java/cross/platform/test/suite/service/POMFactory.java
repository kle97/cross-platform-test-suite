package cross.platform.test.suite.service;

import cross.platform.test.suite.pageobject.AbstractPage;
import cross.platform.test.suite.pageobject.AndroidPage;
import cross.platform.test.suite.pageobject.IosPage;
import cross.platform.test.suite.properties.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Platform;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class POMFactory {

    private final TestConfig testConfig;
    private final Platform currentPlatform;
    private final Map<Class<?>, Object> pageInstanceMap = new HashMap<>();
    
    private final Map<Class<?>, Provider<AbstractPage>> genericMap;
    private final Map<Class<?>, Provider<AndroidPage>> androidMap;
    private final Map<Class<?>, Provider<IosPage>> iOSMap;
    
    @Inject
    public POMFactory(TestConfig testConfig, 
                      Map<Class<?>, Provider<AbstractPage>> genericMap,
                      Map<Class<?>, Provider<AndroidPage>> androidMap,
                      Map<Class<?>, Provider<IosPage>> iOSPageObjectMap) {
        this.testConfig = testConfig;
        this.genericMap = genericMap;
        this.androidMap = androidMap;
        this.iOSMap = iOSPageObjectMap;
        this.currentPlatform = testConfig.getMobileConfig().getDesiredCapabilities().getPlatformName();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        if (pageInstanceMap.containsKey(clazz)) {
            return (T) pageInstanceMap.get(clazz);
        } else {
            return this.getImplementation(clazz);
        }
    }

    public <T> T getNew(Class<T> clazz) {
        return this.getImplementation(clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> T getImplementation(Class<T> clazz) {
        T page = null;
        if (this.currentPlatform.is(Platform.ANDROID) && this.androidMap.containsKey(clazz)) {
            page = (T) this.androidMap.get(clazz).get();
        } else if (this.currentPlatform.is(Platform.IOS) && this.iOSMap.containsKey(clazz)) {
            page = (T) this.iOSMap.get(clazz).get();
        } else if (this.genericMap.containsKey(clazz)) {
            page = (T) this.genericMap.get(clazz).get();
        } 
        
        if (page != null) {
            this.pageInstanceMap.put(clazz, page);
            return page;
        } else {
            log.error("Instance page object is not found for '{}'", clazz.getName());
            return null;
        }
    }
}