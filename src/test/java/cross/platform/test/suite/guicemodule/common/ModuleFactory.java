package cross.platform.test.suite.guicemodule.common;

import com.google.inject.Module;
import cross.platform.test.suite.guicemodule.CatalogModule;
import cross.platform.test.suite.guicemodule.DefaultTestModule;
import cross.platform.test.suite.properties.ConfigMap;
import lombok.extern.slf4j.Slf4j;
import org.testng.IModuleFactory;
import org.testng.ITestContext;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ModuleFactory implements IModuleFactory {
    
    private static final Map<String, Class<? extends Module>> moduleClassMap = new HashMap<>();
    static {
        moduleClassMap.put("Catalog", CatalogModule.class);
        moduleClassMap.put("AllTests", DefaultTestModule.class);
    }
    
    private static final Class<? extends Module> defaultModuleClass = DefaultTestModule.class;
    private static final String DEFAULT_TEST_NAME = "Surefire test";
    private static final Map<String, Module> cachedModuleMap = new ConcurrentHashMap<>();
    private final ConfigMap configMap;

    @Inject
    public ModuleFactory(ConfigMap configMap) {
        this.configMap = configMap;
    }

    @Override
    public Module createModule(ITestContext iTestContext, Class<?> testClass) {
        String testName = iTestContext.getName();
        if (cachedModuleMap.containsKey(testName)) {
            Module module = cachedModuleMap.get(testName);
            log.info("Mapping guice module '{}' to class '{}'", module.getClass().getSimpleName(), testClass.getSimpleName());
            return cachedModuleMap.get(testName);
        } else if (moduleClassMap.containsKey(testName)) {
            Class<? extends Module> moduleClass = moduleClassMap.get(testName);
            try {
                Module module = moduleClass.getConstructor(ConfigMap.class).newInstance(this.configMap);
                cachedModuleMap.put(testName, module);
                log.info("Mapping guice module '{}' to class '{}'!", module.getClass().getSimpleName(), testClass.getSimpleName());
                return module;
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                log.error("Mapping guice module '{}' to class '{}' failed!", moduleClass.getSimpleName(), testClass.getSimpleName());
                throw new RuntimeException(e);
            }
        } else if (testName.equals(DEFAULT_TEST_NAME)) {
            try {
                Module module = defaultModuleClass.getConstructor(ConfigMap.class).newInstance(this.configMap);
                log.info("Mapping guice module '{}' to class '{}'!", defaultModuleClass.getSimpleName(), testClass.getSimpleName());
                return module;
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                log.error("Mapping guice module '{}' to class '{}' failed!", defaultModuleClass.getSimpleName(), testClass.getSimpleName());
                throw new RuntimeException(e);
            }
        } else {
            log.error("Couldn't map guice module for class '{}'!", testClass.getSimpleName());
            return null;
        }
    }
}
