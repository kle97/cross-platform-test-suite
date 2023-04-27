package cross.platform.test.suite.configuration.guicemodule;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import cross.platform.test.suite.pageobject.CatalogPage;
import cross.platform.test.suite.pageobject.SideNavigationPage;
import cross.platform.test.suite.pageobject.common.AbstractPage;
import cross.platform.test.suite.pageobject.common.AndroidPage;
import cross.platform.test.suite.pageobject.common.IosPage;
import cross.platform.test.suite.pageobject.generic.CatalogGenericPage;
import cross.platform.test.suite.pageobject.generic.SideNavigationGenericPage;

public class PageObjectModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<Class<?>, AbstractPage> genericMap = MapBinder.newMapBinder(binder(), new TypeLiteral<>() {}, new TypeLiteral<>() {});
        MapBinder<Class<?>, IosPage> iOSMap = MapBinder.newMapBinder(binder(), new TypeLiteral<>() {}, new TypeLiteral<>() {});
        MapBinder<Class<?>, AndroidPage> androidMap = MapBinder.newMapBinder(binder(), new TypeLiteral<>() {}, new TypeLiteral<>() {});
        
        genericMap.addBinding(SideNavigationPage.class).to(SideNavigationGenericPage.class);
        genericMap.addBinding(CatalogPage.class).to(CatalogGenericPage.class);
        
    }
}
