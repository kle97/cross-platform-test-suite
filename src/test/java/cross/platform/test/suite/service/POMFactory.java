package cross.platform.test.suite.service;

import cross.platform.test.suite.exception.TestSuiteException;
import cross.platform.test.suite.pageobject.common.Page;
import cross.platform.test.suite.properties.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Platform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class POMFactory {

    private final Platform currentPlatform;
    private final Map<Class<? extends Page>, Page> pageInstanceMap = new HashMap<>();
    
    private final Map<Class<? extends Page>, Provider<Page>> genericMap;
    private final Map<Class<? extends Page>, Provider<Page>> iOSMap;
    private final Map<Class<? extends Page>, Provider<Page>> androidMap;
    
    @Inject
    public POMFactory(TestConfig testConfig,
                      @Named("genericMap") Map<Class<? extends Page>, Provider<Page>> genericMap,
                      @Named("iOSMap") Map<Class<? extends Page>, Provider<Page>> androidMap,
                      @Named("androidMap") Map<Class<? extends Page>, Provider<Page>> iOSPageObjectMap) {
        this.genericMap = genericMap;
        this.androidMap = androidMap;
        this.iOSMap = iOSPageObjectMap;
        this.currentPlatform = testConfig.getMobileConfig().getDesiredCapabilities().getPlatformName();
    }

    @SuppressWarnings("unchecked")
    public <T extends Page> T get(Class<T> clazz) {
        if (this.pageInstanceMap.containsKey(clazz)) {
            return (T) this.pageInstanceMap.get(clazz);
        } else {
            return this.getImplementation(clazz);
        }
    }

    public <T extends Page> T getNew(Class<T> clazz) {
        return this.getImplementation(clazz);
    }

    @SuppressWarnings("unchecked")
    private <T extends Page> T getImplementation(Class<T> clazz) {
        Page page = null;
        if (this.currentPlatform.is(Platform.ANDROID) && this.androidMap.containsKey(clazz)) {
            page = this.androidMap.get(clazz).get();
        } else if (this.currentPlatform.is(Platform.IOS) && this.iOSMap.containsKey(clazz)) {
            page = this.iOSMap.get(clazz).get();
        } else if (this.genericMap.containsKey(clazz)) {
            page = this.genericMap.get(clazz).get();
        } 
        
        if (page != null) {
            if (clazz.isAssignableFrom(page.getClass())) {
                this.pageInstanceMap.put(clazz, page);
                page.init();
                return (T) page;
            } else {
                String message = String.format("Page object '%s' is mapped to wrong implementation '%s'!", clazz.getName(), page.getClass().getName());
                throw new TestSuiteException(message);
            }
        } else {
            String message = String.format("Page object implementation is not found for '%s'!", clazz.getName());
            throw new TestSuiteException(message);
        }
    }
}