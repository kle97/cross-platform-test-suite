package cross.platform.test.suite.testcase;

import org.testng.annotations.Factory;

public class FactoryClass {
    
    private static final ThreadLocal<Object[]> verificationStorage = new ThreadLocal<>();
    
    public static void setVerifications(Object[] verifications) {
        verificationStorage.set(verifications);
    }
    
    @Factory
    public Object[] factory() {
        return verificationStorage.get();
    }
}
