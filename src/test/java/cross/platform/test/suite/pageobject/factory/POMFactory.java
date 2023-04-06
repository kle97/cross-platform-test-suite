package cross.platform.test.suite.pageobject.factory;

import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.pageobject.AbstractPage;
import cross.platform.test.suite.pageobject.ApiListPage;
import cross.platform.test.suite.pageobject.AppPage;
import cross.platform.test.suite.pageobject.Page;
import cross.platform.test.suite.pageobject.generic.ApiListGenericPage;
import cross.platform.test.suite.pageobject.generic.AppGenericPage;
import cross.platform.test.suite.properties.MobileConfig;
import io.appium.java_client.AppiumDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Platform;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class POMFactory {
    
    private static final Map<Class<? extends Page>, Class<? extends AbstractPage>> iOSPageMap = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Page>, Class<? extends AbstractPage>> androidPageMap = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Page>, Class<? extends AbstractPage>> genericPageMap = new ConcurrentHashMap<>();
    static {
        genericPageMap.put(ApiListPage.class, ApiListGenericPage.class);
        genericPageMap.put(AppPage.class, AppGenericPage.class);
    }
    
    private final DriverManager driverManager;
    private final MobileConfig mobileConfig;
    private final Map<Class<? extends Page>, ? super Page> pageInstanceMap = new HashMap<>();
    private final Platform currentPlatform;
    
    public POMFactory(DriverManager driverManager, MobileConfig mobileConfig) {
        this.driverManager = driverManager;
        this.mobileConfig = mobileConfig;
        this.currentPlatform = mobileConfig.getDesiredCapabilities().getPlatformName();
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Page> T get(Class<T> clazz) {
        if (pageInstanceMap.containsKey(clazz)) {
            return (T) pageInstanceMap.get(clazz);
        } else {
            return this.getImplementation(clazz);
        }
    }

    public <T extends Page> T getNew(Class<T> clazz) {
        return this.getImplementation(clazz);
    }

    @SuppressWarnings("unchecked")
    private <T extends Page> T getImplementation(Class<T> clazz) {
        Class<? extends Page> implementationClass = null;
        if (this.currentPlatform.is(Platform.ANDROID)) {
            implementationClass = androidPageMap.get(clazz);
        } else if (this.currentPlatform.is(Platform.IOS)) {
            implementationClass = iOSPageMap.get(clazz);
        }
        
        if (implementationClass == null) {
            implementationClass = genericPageMap.get(clazz);
        }

        if (implementationClass != null) {
            try {
                T page = (T) implementationClass.getConstructor(AppiumDriver.class, POMFactory.class).newInstance(driverManager.getDriver(), this);
                pageInstanceMap.put(clazz, page);
                return page;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.debug(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        return null;
    }
}
