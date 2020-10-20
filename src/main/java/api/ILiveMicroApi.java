package api;

/**
 * Created by Princewill Princewill Okoriee on 25-Aug-17.
 */
public interface ILiveMicroApi {

    ILiveMicroSessionApi getLiveSessionApi();

    IUserApi getUserApi();

    ISessionApi getSessionApi();

    ISampleApi getSampleApi();
}
