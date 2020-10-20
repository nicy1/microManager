package factory;

import models.Session;
import models.User;
import repository.ICookieRepository;
import utils.ILogger;

import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by Princewill Princewill Okoriee on 30-Aug-17.
 */
public class SessionFactory implements ISessionFactory {
    private final ICookieRepository cookieRepository;
    private final ILogger logger;
    private Session session;
    private String cookieName = "session.json";

    @Inject
    public SessionFactory(ICookieRepository cookieRepository, ILogger logger) {
        this.cookieRepository = cookieRepository;
        this.logger = logger;

        cookieRepository.getCookie(this.cookieName, Session.class)
                .subscribe(res -> this.session = (Session) res, err -> this.session = null);
    }

    @Override
    public void updateSession(Session session) {
        this.session = session;

        if (this.isValid(session))
            this.cookieRepository.saveCookie(session, this.cookieName)
                    .subscribe((saved) -> {
                        if (saved) {
                            this.logger.logInformation("Session cookie saved");
                        } else {
                            this.logger.logError("Unable to save session cookie");
                        }
                    }, (err) -> this.logger.logError(err.getMessage()));
        else {
            this.cookieRepository.delete(this.cookieName)
                    .subscribe((deleted) -> {
                        if (deleted) {
                            this.logger.logInformation("Session cookie deleted");
                        } else {
                            this.logger.logError("Unable to delete session cookie");
                        }
                    }, (err) -> this.logger.logError(err.getMessage()));
        }
    }

    @Override
    public int getUserId() {
        return getUser().id;
    }

    @Override
    public String getSessionCode() {
        return this.session.code;
    }

    @Override
    public User getUser() {
        return this.session.user;
    }

    @Override
    public boolean isValid() {
        return this.isValid(this.session);
    }

    @Override
    public boolean isValid(Session session) {
        return this.session != null
                && this.session.code != null
                && !this.session.code.isEmpty()
                && ZonedDateTime.now(ZoneOffset.UTC).isBefore(ZonedDateTime.parse(this.session.expiresDateTimeUTC));
    }

    @Override
    public String getToken() {
        return isValid() ? this.session.token : "";
    }

    @Override
    public String getRefreshCode() {
        return isValid() ? this.session.refresh : "";
    }

    @Override
    public Session getSession() {
        return this.session;
    }
}
