package cross.platform.test.suite.configuration.manager;

import io.appium.java_client.AppiumDriver;

public final class DriverManager {
    
    private final ThreadLocal<AppiumDriver> appiumDriverThreadLocal = new ThreadLocal<>();

    public void setDriver(AppiumDriver appiumDriver) {
        this.appiumDriverThreadLocal.set(appiumDriver);
    }

    public AppiumDriver getDriver() {
        return this.appiumDriverThreadLocal.get();
    }

    public void removeDriver() {
        this.appiumDriverThreadLocal.remove();
    }

    public boolean hasDriver() {
        return this.getDriver() != null;
    }
}
