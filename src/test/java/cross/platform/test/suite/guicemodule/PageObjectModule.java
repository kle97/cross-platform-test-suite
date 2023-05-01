package cross.platform.test.suite.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import cross.platform.test.suite.pageobject.CatalogPage;
import cross.platform.test.suite.pageobject.SideNavigationPage;
import cross.platform.test.suite.pageobject.common.Page;
import cross.platform.test.suite.pageobject.generic.CatalogGenericPage;
import cross.platform.test.suite.pageobject.generic.SideNavigationGenericPage;

public class PageObjectModule extends AbstractModule {
    
    @Override
    protected void configure() {
        final PageObjectBinder poBinder = new PageObjectBinder(binder());
        poBinder.genericBinding(SideNavigationPage.class, SideNavigationGenericPage.class);
        poBinder.genericBinding(CatalogPage.class, CatalogGenericPage.class);
        
    }
    
    static class PageObjectBinder {
        private final MapBinder<Class<? extends Page>, Page> genericMap;
        private final MapBinder<Class<? extends Page>, Page> iOSMap;
        private final MapBinder<Class<? extends Page>, Page> androidMap;
        
        
        public PageObjectBinder(Binder binder) {
            this.genericMap = MapBinder.newMapBinder(binder, new TypeLiteral<>() {}, new TypeLiteral<>() {}, Names.named("genericMap"));
            this.iOSMap = MapBinder.newMapBinder(binder, new TypeLiteral<>() {}, new TypeLiteral<>() {}, Names.named("iOSMap"));
            this.androidMap = MapBinder.newMapBinder(binder, new TypeLiteral<>() {}, new TypeLiteral<>() {}, Names.named("androidMap"));
        }

        protected <T extends Page, V extends T> void genericBinding(Class<T> clazz, Class<V> implementationClass) {
            genericMap.addBinding(clazz).to(implementationClass);
        }

        protected <T extends Page, V extends T> void iOSBinding(Class<T> clazz, Class<V> implementationClass) {
            iOSMap.addBinding(clazz).to(implementationClass);
        }

        protected <T extends Page, V extends T> void androidBinding(Class<T> clazz, Class<V> implementationClass) {
            androidMap.addBinding(clazz).to(implementationClass);
        }
    }
}
