package cross.platform.test.suite.common;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.presentation.Representation;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Slf4j
public class SoftAssertJ extends SoftAssertions {
    
    public interface ByteBuddyInterceptor {
        @RuntimeType
        <T extends AbstractAssert<?, ?>> Object intercept(@This T assertion,
                                                          @SuperCall Callable<?> proxy,
                                                          @SuperMethod(nullIfImpossible = true) Method superMethod,
                                                          @StubValue Object stub,
                                                          @Origin Method method,
                                                          @AllArguments Object[] arguments,
                                                          @FieldValue("actual") Object actual) throws Exception;
    }
    
    public SoftAssertJ() {
        try {
            Implementation implementation = MethodDelegation.to(new ByteBuddyInterceptor() {
                @Override
                public <T extends AbstractAssert<?, ?>> Object intercept(T assertion, Callable<?> proxy,
                                                                         Method superMethod, Object stub, Method method,
                                                                         Object[] arguments, Object actual) throws Exception {
                    WritableAssertionInfo info = assertion.getWritableAssertionInfo();
                    try {
                        onBeforeAssert(info, actual, method, arguments);
                        Object result = proxy.call();
                        succeeded();
                        onAssertSuccess(info, actual, method, arguments);
                        return result;
                    } catch (AssertionError assertionError) {
                        if (isNestedErrorCollectorProxyCall()) {
                            // let the most outer call handle the assertion error
                            throw assertionError;
                        }
                        collectAssertionError(assertionError);
                    } finally {
                        onAfterAssert(info, actual, method, arguments);
                    }
                    if (superMethod != null && !superMethod.getReturnType().isInstance(assertion)) {
                        // In case the object is not an instance of the return type, just default value for the return type:
                        // null for reference type and 0 for the corresponding primitive types.
                        return stub;
                    }
                    return assertion;
                }

                private boolean isNestedErrorCollectorProxyCall() {
                    return Arrays.stream(Thread.currentThread().getStackTrace())
                                 .filter(stackTraceElement -> ByteBuddyInterceptor.class.getName().equals(stackTraceElement.getClassName())
                                         && stackTraceElement.getMethodName().startsWith("intercept"))
                                 .count() > 1;
                }
            }, ByteBuddyInterceptor.class);
            
            Field softProxies = AbstractSoftAssertions.class.getDeclaredField("proxies");
            softProxies.setAccessible(true);
            Field ERROR_COLLECTOR = softProxies.getType().getDeclaredField("ERROR_COLLECTOR");
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            unsafe.putObject(unsafe.staticFieldBase(ERROR_COLLECTOR), unsafe.staticFieldOffset(ERROR_COLLECTOR), implementation);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void onAssertSuccess(WritableAssertionInfo info, Object actual, Method method, Object[] arguments) {
        Representation representation = info.representation();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            sb.append(representation.toStringOf(arguments[i]));
            if (i < arguments.length - 1) {
                sb.append(", ");
            }
        }

        Reporter.pass("[" + info.descriptionText() + "] expected: " + sb + " " 
                              + method.getName() + ": " + representation.toStringOf(actual));
    }

    @Override
    public void onAssertionErrorCollected(AssertionError e) {
        Reporter.fail(e.getMessage()
                       .replaceAll("[\\s]{1,}", " ")
                       .replace("Expecting", "expected"));
    }

    public void onBeforeAssert(WritableAssertionInfo info, Object actual, Method method, Object[] arguments) {
        // not implemented
    }

    public void onAfterAssert(WritableAssertionInfo info, Object actual, Method method, Object[] arguments) {
        // not implemented
    }
}

