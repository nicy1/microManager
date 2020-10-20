package repository;

import rx.Observable;

/**
 * Created by Princewill Okorie on 03-Jan-18.
 */
public interface ICookieRepository {

    Observable<Boolean> saveCookie(Object session, String cookieName);

    Observable<Object> getCookie(String cookieName, Class<?> type);

    Observable<Boolean> delete(String cookieName);
}
