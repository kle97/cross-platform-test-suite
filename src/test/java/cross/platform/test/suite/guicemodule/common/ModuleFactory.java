package cross.platform.test.suite.guicemodule.common;

import com.google.inject.Module;
import cross.platform.test.suite.guicemodule.CatalogModule;
import cross.platform.test.suite.properties.ConfigMap;
import cross.platform.test.suite.utility.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.IModuleFactory;
import org.testng.ITestContext;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ModuleFactory implements IModuleFactory {
    
    private static final Map<String, Class<? extends Module>> moduleClassMap = new ConcurrentHashMap<>();
    static {
        moduleClassMap.put("Catalog", CatalogModule.class);
    }
    
    private static final Map<String, Module> cachedModuleMap = new ConcurrentHashMap<>();
    private static final Module noopModule = new NoopModule();
    private final ConfigMap configMap;

    @Inject
    public ModuleFactory(ConfigMap configMap) {
        this.configMap = configMap;
    }

    @Override
    public Module createModule(ITestContext iTestContext, Class<?> testClass) {
        if (!ConfigUtil.isParallel()) {
            return noopModule;
        }
        
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
                log.info("Mapping guice module '{}' to class '{}'", module.getClass().getSimpleName(), testClass.getSimpleName());
                return module;
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                log.error("Mapping failed for guice module '{}'", moduleClass);
                throw new RuntimeException(e);
            }
        } else {
            log.error("Couldn't map guice module for class '{}'", testClass.getSimpleName());
            return null;
        }
    }
}
