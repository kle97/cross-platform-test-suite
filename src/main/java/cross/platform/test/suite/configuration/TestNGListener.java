package cross.platform.test.suite.configuration;

import lombok.extern.slf4j.Slf4j;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.IDataProviderAnnotation;

import java.lang.reflect.Method;

@Slf4j
public class TestNGListener implements IAnnotationTransformer {

    @Override
    public void transform(IDataProviderAnnotation annotation, Method method) {
        boolean dataParallel = Boolean.parseBoolean(System.getProperty("dataParallel"));
        annotation.setParallel(dataParallel);
    }
}
