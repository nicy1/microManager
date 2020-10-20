package task;

import response.SampleResponse;
import rx.Observable;

import java.io.File; /**
 * Created by Princewill Princewill Okoriee on 31-Aug-17.
 */
public interface ILiveMicroSession {
    void stop();

    void start();

    Observable<SampleResponse> uploadImage(File file);
}
