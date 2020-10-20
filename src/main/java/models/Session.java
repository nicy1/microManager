package models;

import org.joda.time.DateTime;

public class Session extends Base {

    public String code;

    public String expiresDateTimeUTC;

    public String refresh;

    public String fkUser;

    public User user;

    public String token;

    public DateTime getExpires() {
        return DateTime.parse(expiresDateTimeUTC);
    }
}
