package cross.platform.test.suite.common;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import org.assertj.core.api.*;
import org.assertj.core.description.Description;
import org.assertj.core.presentation.Representation;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

@Slf4j
public class SoftAssertJ extends SoftAssertions {
    
    private static final SoftAssertJ softAssertions = new SoftAssertJ();
    private static final ThreadLocal<AssertionDescription> messages = new ThreadLocal<>();

    public static SoftAssertJ getInstance() {
        return softAssertions;
    }

    @Builder
    @Getter
    static class AssertionDescription {
        private String formatter;
        private Object[] args;
        private Description description;
        private Supplier<String> supplier;
    }
    
    public SoftAssertJ as(String description, Object... args) {
        messages.set(AssertionDescription.builder().formatter(description).args(args).build());
        return this;
    }

    public SoftAssertJ as(Description description) {
        messages.set(AssertionDescription.builder().description(description).build());
        return this;
    }

    public SoftAssertJ as(Supplier<String> description) {
        messages.set(AssertionDescription.builder().supplier(description).build());
        return this;
    }

    @Override
    public <SELF extends Assert<? extends SELF, ? extends ACTUAL>, ACTUAL> SELF proxy(Class<SELF> assertClass,
                                                                                      Class<ACTUAL> actualClass,
                                                                                      ACTUAL actual) {
        SELF proxy = super.proxy(assertClass, actualClass, actual);
        if (messages.get() != null) {
            AssertionDescription description = messages.get();
            if (description.getFormatter() != null) {
                proxy.as(description.getFormatter(), description.getArgs());
            } else if (description.getDescription() != null) {
                proxy.as(description.getDescription());
            } else if (description.getSupplier() != null) {
                proxy.as(description.getSupplier());
            }
            messages.remove();
        }
        return proxy;
    }

    public interface ByteBuddyInterceptor {
        @RuntimeType
        <T extends AbstractAssert<?, ?>> Object intercept(@This T assertion,
                                                          @SuperCall(nullIfImpossible = true) Callable<T> proxy,
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
                public <T extends AbstractAssert<?, ?>> Object intercept(T assertion, Callable<T> proxy,
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

        String message = !info.descriptionText().isEmpty() ? "[" + info.descriptionText() + "] " : "";
        String methodName = method.getName().replace("ForProxy", "");
        Reporter.pass(message + "expected: " + representation.toStringOf(actual)
                              + " " + methodName + (sb.length() > 0 ? ": " : "") + sb);
    }

    @Override
    public void onAssertionErrorCollected(AssertionError e) {
        Reporter.fail(e.getMessage()
                       .trim()
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

