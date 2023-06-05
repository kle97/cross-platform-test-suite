package cross.platform.test.suite.service;

import cross.platform.test.suite.exception.TestSuiteException;
import cross.platform.test.suite.pageobject.CatalogPage;
import cross.platform.test.suite.pageobject.SideNavigationPage;
import cross.platform.test.suite.pageobject.common.AbstractPage;
import cross.platform.test.suite.pageobject.common.Page;
import cross.platform.test.suite.pageobject.generic.CatalogGenericPage;
import cross.platform.test.suite.pageobject.generic.SideNavigationGenericPage;
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

    private final static Map<Class<?>, Class<? extends AbstractPage>> genericMap = Map.ofEntries(
            entry(SideNavigationPage.class, SideNavigationGenericPage.class),
            entry(CatalogPage.class, CatalogGenericPage.class)
    );

    private final static Map<Class<?>, Class<? extends AbstractPage>> iosMap = Map.ofEntries(
    );

    private final static Map<Class<?>, Class<? extends AbstractPage>> androidMap = Map.ofEntries(
    );

    private final Platform currentPlatform;
    private final Map<Class<? extends Page>, Page> pageInstanceMap = new HashMap<>();

    private final Map<Class<?>, Provider<AbstractPage>> genericMapBinding;
    private final Map<Class<?>, Provider<AbstractPage>> iOSMapBinding;
    private final Map<Class<?>, Provider<AbstractPage>> androidMapBinding;

    @Inject
    public POMFactory(TestConfig testConfig,
                      @Named("genericMap") Map<Class<?>, Provider<AbstractPage>> genericMapBinding,
                      @Named("iOSMap") Map<Class<?>, Provider<AbstractPage>> androidMapBinding,
                      @Named("androidMap") Map<Class<?>, Provider<AbstractPage>> iOSMapBinding) {
        this.genericMapBinding = genericMapBinding;
        this.androidMapBinding = androidMapBinding;
        this.iOSMapBinding = iOSMapBinding;
        this.currentPlatform = testConfig.getMobileConfig().getDesiredCapabilities().getPlatformName();
    }

    private static <T extends Page, V extends T> Map.Entry<Class<T>, Class<V>> entry(Class<T> clazz, Class<V> implementationClass) {
        return Map.entry(clazz, implementationClass);
    }
    
    public static Map<Class<?>, Class<? extends AbstractPage>> getGenericMap() {
        return genericMap;
    }

    public static Map<Class<?>, Class<? extends AbstractPage>> getIOSMap() {
        return iosMap;
    }

    public static Map<Class<?>, Class<? extends AbstractPage>> getAndroidMap() {
        return androidMap;
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
        if (this.currentPlatform.is(Platform.ANDROID) && this.androidMapBinding.containsKey(clazz)) {
            page = this.androidMapBinding.get(clazz).get();
        } else if (this.currentPlatform.is(Platform.IOS) && this.iOSMapBinding.containsKey(clazz)) {
            page = this.iOSMapBinding.get(clazz).get();
        } else if (this.genericMapBinding.containsKey(clazz)) {
            page = this.genericMapBinding.get(clazz).get();
        }

        if (page != null) {
            this.pageInstanceMap.put(clazz, page);
            page.init();
            return (T) page;
        } else {
            String message = String.format("Page object implementation is not found for '%s'!", clazz.getName());
            throw new TestSuiteException(message);
        }
    }
}