package api;

import javax.inject.Inject;

/**
 * Created by Princewill Princewill Okoriee on 25-Aug-17.
 */
public class LiveMicroApi implements ILiveMicroApi {
    private final ISessionApi sessionApi;
    private final IUserApi userApi;
    private final ISampleApi sampleApi;
    private final ILiveMicroSessionApi liveMicroSessionApi;

    @Inject
    public LiveMicroApi(ISessionApi sessionApi,
                        IUserApi userApi,
                        ISampleApi sampleApi,
                        ILiveMicroSessionApi liveMicroSessionApi) {
        this.sessionApi = sessionApi;
        this.userApi = userApi;
        this.sampleApi = sampleApi;
        this.liveMicroSessionApi = liveMicroSessionApi;
    }

    @Override
    public ILiveMicroSessionApi getLiveSessionApi() {
        return this.liveMicroSessionApi;
    }

    @Override
    public IUserApi getUserApi() {
        return this.userApi;
    }

    @Override
    public ISessionApi getSessionApi() {
        return this.sessionApi;
    }

    @Override
    public ISampleApi getSampleApi() {
        return sampleApi;
    }
}
