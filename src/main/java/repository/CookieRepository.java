package repository;

import javax.inject.Inject;

import rx.Observable;
import service.ILocalStorageWriter;
import utils.Constant;

public class CookieRepository implements ICookieRepository {

    private ILocalStorageWriter localStorage;

    @Inject
    public CookieRepository(ILocalStorageWriter localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    public Observable<Boolean> saveCookie(Object data, String cookieName) {
        String path = String.format("%s\\%s", Constant.BIN_PATH, cookieName);

        return this.localStorage.removeFile(path)
                .switchMap(rem -> this.localStorage.saveObjectAs(data, path))
                .map(res -> res != null && !res.isEmpty());
    }

    @Override
    public Observable<Object> getCookie(String cookieName, Class<?> type) {
        String path = String.format("%s\\%s", Constant.BIN_PATH, cookieName);

        return this.localStorage.getFileAs(path, type);
    }

    @Override
    public Observable<Boolean> delete(String cookieName) {
        String path = String.format("%s\\%s", Constant.BIN_PATH, cookieName);

        return this.localStorage.removeFile(path);
    }
}
