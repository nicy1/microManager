package api;

import request.ImageUploadRequest;
import response.SampleResponse;
import rx.Observable;

/**
 * Created by Princewill Princewill Okoriee on 31-Aug-17.
 */
public interface ISampleApi {
    Observable<SampleResponse> uploadImage(ImageUploadRequest request);
}
