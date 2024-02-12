package cross.platform.test.suite.common;

public abstract class BaseTest {
    
    private final long nanosecond = System.nanoTime();
    private final String toString = nanosecond + "@" + getClass().getName();
    
    public String toString() {
        return toString;
    }
}
