package cross.platform.test.suite.pageobject.factory;

import cross.platform.test.suite.configuration.manager.DriverManager;
import cross.platform.test.suite.pageobject.AbstractPage;
import cross.platform.test.suite.pageobject.ApiListPage;
import cross.platform.test.suite.pageobject.Page;
import cross.platform.test.suite.pageobject.generic.ApiListGenericPage;
import cross.platform.test.suite.properties.MobileConfig;
import io.appium.java_client.AppiumDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Platform;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class POFactory {
    
    private static final Map<Class<? extends Page>, Class<? extends AbstractPage>> iOSPageMap = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Page>, Class<? extends AbstractPage>> androidPageMap = new ConcurrentHashMap<>();
    static {
        androidPageMap.put(ApiListPage.class, ApiListGenericPage.class);
    }
    
    private final DriverManager driverManager;
    private final MobileConfig mobileConfig;
    private final Map<Class<? extends Page>, ? super Page> pageInstanceMap = new HashMap<>();
    private final Platform currentPlatform;
    
    public POFactory (DriverManager driverManager, MobileConfig mobileConfig) {
        this.driverManager = driverManager;
        this.mobileConfig = mobileConfig;
        this.currentPlatform = mobileConfig.getDesiredCapabilities().getPlatformName();
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Page> T get(Class<T> clazz) {
        if (pageInstanceMap.containsKey(clazz)) {
            return (T) pageInstanceMap.get(clazz);
        }
        
        T page = null;
        try {
            if (this.currentPlatform.is(Platform.ANDROID) && androidPageMap.containsKey(clazz)) {
                page = (T) androidPageMap.get(clazz).getConstructor(AppiumDriver.class).newInstance(driverManager.getDriver());
            } else if (this.currentPlatform.is(Platform.IOS) && iOSPageMap.containsKey(clazz)) {
                page = (T) iOSPageMap.get(clazz).getConstructor(AppiumDriver.class).newInstance(driverManager.getDriver());
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.debug(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        
        if (page != null) {
            pageInstanceMap.put(clazz, page);
        }
        return page;
    }
}
