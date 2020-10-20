package dagg;

import dagger.Component;
import factory.ISessionFactory;
import service.ILocalStorageWriter;
import utils.ILogger;

import javax.inject.Singleton;

import api.ILiveMicroApi;


@Singleton
@Component(modules = {UtilsModule.class, RepositoryModule.class, FactoryModule.class})
public interface SingletonComponent {

    ISessionFactory sessionFactory();

    ILocalStorageWriter localStorageWriter();

    ILogger logger();
    
    ILiveMicroApi liveMicroApi();
}
