package dagg;

import factory.ISessionFactory;
import factory.SessionFactory;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class FactoryModule {

    @Binds
    @Singleton
    abstract ISessionFactory providesSessionFactory(SessionFactory sessionFactory);
}
