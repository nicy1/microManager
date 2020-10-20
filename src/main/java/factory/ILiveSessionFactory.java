package factory;

import models.LiveSession;
import response.SampleResponse;
import rx.Observable;

import java.io.File;

/**
 * Created by Princewill Princewill Okoriee on 31-Aug-17.
 */
public interface ILiveSessionFactory {
    /**
     * Send an image
     */
    void sendNewImage();

    /**
     * Start a new live session
     *
     * @param data
     */
    void startNewSession(LiveSession data);


    /**
     * Send an image
     *
     * @param file - Image file
     */
    Observable<SampleResponse> sendImage(File file);
}
