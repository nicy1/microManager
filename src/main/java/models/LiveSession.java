package models;

/**
 * Created by Princewill Princewill Okoriee on 10-Oct-17.
 */
public class LiveSession extends Base {

    public int id;

    public String code;

    public User user;

    public String title;

    public String description;

    public String startDateTimeUTC;

    public String fkUser;

    public LiveSessionRequest request;

    public LiveSession(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public LiveSession(){
        this("", "");
    }

}
