package api;

import request.SessionRequest;
import response.SessionResponse;
import rx.Observable;

public interface ISessionApi {
	Observable<SessionResponse> logIn(SessionRequest request);

    Observable<Void> logOut();

    Observable<SessionResponse> extend(String refreshToken);
}