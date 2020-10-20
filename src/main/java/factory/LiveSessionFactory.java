package factory;

import models.IMicroscope;
import models.LiveSession;
import response.SampleResponse;
import api.ILiveMicroApi;
import api.ISampleApi;
import rx.Observable;
import task.ILiveMicroSession;
import task.LiveMicroSession;
import utils.ILogger;

import java.io.File;

/**
 * Created by Princewill Princewill Okoriee on 31-Aug-17.
 */
public class LiveSessionFactory implements ILiveSessionFactory {
    private final IMicroscope microscope;
    private final ILogger logger;
    private final ISampleApi sampleApi;
    private ILiveMicroSession liveSession;

    public LiveSessionFactory(IMicroscope microscope, ILogger logger, ISampleApi sampleApi) {
        this.microscope = microscope;
        this.logger = logger;
        this.sampleApi = sampleApi;
        this.liveSession = null;
    }

    @Override
    public void sendNewImage() {
        this.microscope.snapImage();
    }

    @Override
    public void startNewSession(LiveSession data) {
        if (liveSession != null) liveSession.stop();

        this.liveSession = new LiveMicroSession(microscope, logger, sampleApi, data);
        this.liveSession.start();
    }

    @Override
    public Observable<SampleResponse> sendImage(File file) {
        if (liveSession != null){
          return this.liveSession.uploadImage(file);
        }

        return null;
    }
}
