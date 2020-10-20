package service;

import com.google.gson.Gson;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LocalStorageWriter implements ILocalStorageWriter {

    @Override
    public Observable<String> saveObjectAs(Object data, String path) {
        return Observable.create((Observable.OnSubscribe<String>) subscriber -> {

            Gson gson = new Gson();
            String txt = gson.toJson(data);

            try {
                FileWriter fr = new FileWriter(path);
                fr.write(txt);
                fr.close();

                subscriber.onNext(path);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
                subscriber.onCompleted();
            }

        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Object> getFileAs(String path, Class<?> type) {
        return Observable.create(subscriber -> {

            Gson gson = new Gson();

            try {
                FileReader fr = new FileReader(path);
                Object res = gson.fromJson(fr, type);

                subscriber.onNext(res);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
                subscriber.onCompleted();
            }

        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> removeFile(String path) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {

            File file = new File(path);

            if (file.exists()) {
                subscriber.onNext(file.delete());
            } else {
                subscriber.onNext(true);
            }

            subscriber.onCompleted();

        }).subscribeOn(Schedulers.io());
    }
}
