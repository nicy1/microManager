package api;

import models.User;
import response.IntegerResponse;
import rx.Observable;

public interface IUserApi {
    Observable<IntegerResponse> addUser(User user);
}
