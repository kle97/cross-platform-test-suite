package cross.platform.test.suite.configuration.listener;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuiceInjectorListener implements TypeListener {
    
    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        log.info(type.getRawType().getName());
        encounter.register((InjectionListener<I>) injectee -> {
            log.info(String.valueOf(injectee.getClass()));
        });
    }
}
