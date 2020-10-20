package models;

import rx.Observable;


/**
 * Created by Princewill Princewill Okoriee on 25-Jul-17.
 */
public interface IMicroscope {
    void startLiveMode();

    Observable<Sample> sampleSubscribe();
    
    Observable<Sample> videoSubscribe();

    void stopLiveMode();

    void snapImage();

    void listDeviceProperties(String deviceName);

    void toggleLiveMode();

    boolean isLiveModeOn();

    void sampleUnSubscribe();
    
    void videoUnSubscribe();

}
