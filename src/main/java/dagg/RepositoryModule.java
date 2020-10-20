package dagg;

import repository.CookieRepository;
import repository.ICookieRepository;

import javax.inject.Singleton;

import api.ILiveMicroApi;
import api.ILiveMicroSessionApi;
import api.ISampleApi;
import api.ISessionApi;
import api.IUserApi;
import api.LiveMicroApi;
import api.LiveMicroSessionApi;
import api.SampleApi;
import api.SessionApi;
import api.UserApi;
import dagger.Binds;
import dagger.Module;


@Module
public abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract ICookieRepository providesCookieRepository(CookieRepository repository);

    @Binds
    @Singleton
    abstract ISessionApi providesSessionApi(SessionApi api);

    @Binds
    @Singleton
    abstract ILiveMicroSessionApi providesLiveMicroSessionApi(LiveMicroSessionApi api);

    @Binds
    @Singleton
    abstract ISampleApi providesSampleApi(SampleApi api);

    @Binds
    @Singleton
    abstract IUserApi providesUserApi(UserApi api);

    @Binds
    @Singleton
    abstract ILiveMicroApi providesLiveMicroApi(LiveMicroApi api);
}