package dagg;

import service.ILocalStorageWriter;
import service.LocalStorageWriter;
import utils.ILogger;
import utils.Logger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class UtilsModule {

    @Provides
    @Singleton
	public
    static ILocalStorageWriter provideLocalStorageWriter() {
        return new LocalStorageWriter();
    }

    @Provides
    @Singleton
	public
    static ILogger providesLogger() {
        return new Logger();
    }
}
