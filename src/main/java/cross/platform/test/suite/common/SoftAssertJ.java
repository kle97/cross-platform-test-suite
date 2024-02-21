package cross.platform.test.suite.common;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assert;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.presentation.Representation;

import java.lang.reflect.InvocationTargetException;

public class SoftAssertJ extends SoftAssertions {

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
}

