package cross.platform.test.suite.configuration;

import lombok.extern.slf4j.Slf4j;
import org.testng.*;
import org.testng.annotations.IDataProviderAnnotation;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.xml.XmlSuite;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class TestNGListener implements IAlterSuiteListener, IAnnotationTransformer, IMethodInterceptor {

    @Override
    public void transform(IDataProviderAnnotation annotation, Method method) {
        String parallelMode = System.getProperty("parallel", "none");
        if (parallelMode.equals("tests")) {
            annotation.setParallel(true);
        }
    }

    @Override
    public void transform(IFactoryAnnotation annotation, Method method) {
        String property = System.getProperty("factory");
        if (property == null) {
            annotation.setEnabled(false);
        } else {
            String[] tests = property.split(",");
            for (String test : tests) {
                if (method.getName().equals(test)) {
                    annotation.setEnabled(true);
                    return;
                } else {
                    annotation.setEnabled(false);
                }
            }
        }
    }   

    @Override
    public void alter(List<XmlSuite> suites) {
        for (XmlSuite suite : suites) {
            suite.setGroupByInstances(true);
        }
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        methods.sort(Comparator.comparing(m -> m.getInstance().toString()));
        return methods;
    }
}
