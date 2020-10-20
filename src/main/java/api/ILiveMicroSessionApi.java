package api;

import models.LiveSession;
import response.LiveMicroSessionResponse;
import response.LiveSessionHistoryResponse;
import rx.Observable;

/**
 * Created by Princewill Princewill Okoriee on 10-Oct-17.
 */
public interface ILiveMicroSessionApi {
    Observable<LiveMicroSessionResponse> createSession(LiveSession session);
    
    Observable<LiveSessionHistoryResponse> getHistory();
}
