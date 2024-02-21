package cross.platform.test.suite.testplan;

import cross.platform.test.suite.common.Reporter;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.bind.annotation.*;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assert;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.presentation.Representation;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public class AssertJTest {
    
    @Test
    public void assertJTest() {
        Softly softAssertions = new Softly();
        softAssertions.assertThat("1").as("AAA").isEqualTo("1");
        softAssertions.assertThat(new int[] {1,2,3}).as("BBB").contains(5);
        softAssertions.assertThat(4).as("CCC").isEqualTo(5);
        softAssertions.assertThat(10).as("DDD").isEqualTo(10);
        
        try {
            softAssertions.assertAll();
        } catch (Error e) {
            
        }
    }

    static class Softly extends SoftAssertions {
        
        

        @Override
        public void onAssertionErrorCollected(AssertionError e) {
            Reporter.fail(e.getMessage()
                           .replaceAll("[\\s]{1,}", " ")
                           .replace("Expecting", "expected"));
        }

        @Override
        public <SELF extends Assert<? extends SELF, ? extends ACTUAL>, ACTUAL> SELF proxy(Class<SELF> assertClass,
                                                                                          Class<ACTUAL> actualClass,
                                                                                          ACTUAL actual) {
            SELF self = super.proxy(assertClass, actualClass, actual);
            try {
                return new ByteBuddy().subclass(assertClass)
                                      .method(ModifierReviewable.OfByteCodeElement::isPublic)
                                      .intercept(InvocationHandlerAdapter.of((proxy, method, args) -> {
                                          try {
                                              if (!Assert.class.isAssignableFrom(method.getDeclaringClass())) {
                                                  method.invoke(self, args);
                                                  return proxy;
                                              }
                                              
                                              Object result = method.invoke(self, args);
                                              if (wasSuccess()) {
                                                  String message = "";
                                                  if (AbstractAssert.class.isAssignableFrom(result.getClass())) {
                                                      AbstractAssert<?, ?> assertion = (AbstractAssert<?, ?>) result;
                                                      Representation representation = assertion.info.representation();
                                                      StringBuilder sb = new StringBuilder();
                                                      for (int i = 0; i < args.length; i++) {
                                                          sb.append(representation.toStringOf(args[i]));
                                                          if (i < args.length - 1) {
                                                              sb.append(", ");
                                                          }
                                                      }
                                                      message = "[" + assertion.descriptionText() + "] expected: " 
                                                              + representation.toStringOf(actual) + " " + method.getName() 
                                                              + ": " + sb;
                                                  }

                                                  Reporter.pass(message);
                                              }
                                              return proxy;
                                          } catch (IllegalAccessException | InvocationTargetException e) {
                                              throw new RuntimeException(e);
                                          }
                                      }))
                                      .make()
                                      .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                                      .getLoaded().getConstructor(actualClass).newInstance(actual);
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private Class<?>[] getInterfaces(Class<?> type) {
            List<Class<?>> interfaces = new ArrayList<>(Arrays.asList(type.getInterfaces()));
            Class<?> superClass = type.getSuperclass();
            while (superClass != Object.class) {
                interfaces.addAll(Arrays.asList(superClass.getInterfaces()));
                type = superClass;
                superClass = type.getSuperclass();
            }
            return interfaces.toArray(new Class[0]);
        }

        private <T> String objectToString(T object) {
            if (object == null) {
                return "null";
            }

            String result;
            Class<?> objectClass = object.getClass();
            if (objectClass.isArray()) {
                try {
                    result = Arrays.deepToString((Object[]) object);
                } catch (ClassCastException e) {
                    result = Arrays.deepToString(new Object[]{object});
                }
                result = result.replaceAll("[\\[\\]]", "");
            } else {
                result = String.valueOf(object);
            }

            return result;
        }
    }

    static class Interceptor {

        private final Object actual;

        public Interceptor(Object actual) {
            this.actual = actual;
        }

        @SuppressWarnings("unused")
        @RuntimeType
        public Object intercept(@This Object self, @Origin Method method,
                                @AllArguments Object[] args, @SuperCall Callable<?> callable) throws Throwable {
            try {
                log.info("Before Assertion!");
                Object result = callable.call();
                log.info("    PASS    expected [" + actual + "]");
                log.info("After Assertion!");
                return result;
            } catch (AssertionError e) {
                log.info("    PASS    expected [" + actual + " but found [" + Arrays.toString(args) + "]");
                throw e;
            }
        }
    }
}
