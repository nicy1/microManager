package factory;

import models.Session;
import models.User;

public interface ISessionFactory {
    void updateSession(Session session);

    int getUserId();

    String getSessionCode();

    User getUser();

    boolean isValid();

    boolean isValid(Session session);

    String getToken();

    String getRefreshCode();

    Session getSession();
}
