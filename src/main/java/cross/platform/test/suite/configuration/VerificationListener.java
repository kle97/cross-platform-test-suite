package cross.platform.test.suite.configuration;

import org.testng.IAnnotationTransformer;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.annotations.IDataProviderAnnotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class VerificationListener implements IMethodInterceptor, IAnnotationTransformer {

    @Override
    public void transform(IDataProviderAnnotation annotation, Method method) {
        boolean dataParallel = Boolean.parseBoolean(System.getProperty("verifications.dataParallel"));
        annotation.setParallel(dataParallel);
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        String filter = System.getProperty("verifications", "");
        if (!filter.isEmpty()) {
            List<IMethodInstance> filteredMethods = new ArrayList<>();
            String[] patterns = filter.split(",");
            for (IMethodInstance methodInstance : methods) {
                if (Arrays.stream(patterns).anyMatch(pattern -> matches(pattern, methodInstance))) {
                    filteredMethods.add(methodInstance);
                }
            }
            methods = filteredMethods;
        }

        methods.sort(Comparator.comparing(m -> m.getInstance().toString()));
        return methods;
    }
    
    private boolean matches(String pattern, IMethodInstance methodInstance) {
        String methodName = methodInstance.getMethod().getQualifiedName();
        String className = methodInstance.getMethod().getRealClass().getName();
        Pattern compiledPattern = Pattern.compile(pattern.replaceAll("\\*", ".*"));
        return compiledPattern.matcher(methodName).matches() || compiledPattern.matcher(className).matches();
    }
}
