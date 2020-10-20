package service;

import rx.Observable;

public interface ILocalStorageWriter {

    Observable<String> saveObjectAs(Object data, String path);

    Observable<Object> getFileAs(String file, Class<?> type);

    Observable<Boolean> removeFile(String path);
}
