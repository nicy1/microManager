package models;

import org.joda.time.DateTime;

public class Base {

    public String modifiedBy;

    public String modifiedDateTimeUTC;

    public String createdDateTimeUTC;

    public DateTime getCreated() {
        return DateTime.parse(createdDateTimeUTC);
    }

    public DateTime getModified() { return DateTime.parse(modifiedDateTimeUTC);}

}
