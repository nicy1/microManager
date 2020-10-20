package utils;

import factory.ISessionFactory;
import factory.SessionFactory;
import mmcorej.CMMCore;
import api.*;
import repository.CookieRepository;
import repository.ICookieRepository;
import service.ILocalStorageWriter;
import service.LocalStorageWriter;

public class Singleton {
    private static ILogger log;
    private static ISessionFactory sessionFactory;
    private static ICookieRepository cookieRepository;
    private static ILocalStorageWriter localStorageWriter;

    public static CMMCore cmmCore;

    public static ILogger logger() {
        if (log == null) {
            log = new Logger();
        }
        return log;
    }

//    public static ILiveMicroApi getApi() {
//        if (api == null) {
//            api = new LiveMicroApi(new SessionApi(Singleton.logger()),
//                    new UserApi(Singleton.logger()),
//                    Singleton.getSessionFactory(),
//                    new SampleApi(Singleton.logger()), new LiveMicroSessionApi(Singleton.logger()));
//        }
//
//        return api;
//    }

    public static ISessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new SessionFactory(Singleton.getCookieRepository(), Singleton.logger());
        }

        return sessionFactory;
    }

    public static void launchUI(){

    }

    public static ICookieRepository getCookieRepository() {
        if (cookieRepository == null) {
            cookieRepository = new CookieRepository(new LocalStorageWriter());
        }

        return cookieRepository;
    }
}
