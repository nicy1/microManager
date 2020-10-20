package request;

public class LoginRequest {

    /**
     * Login username
     */
    public String username;

    /**
     * Login password
     */
    public String password;

    /**
     * Login token
     */
    public String token;

    /**
     * Duration in hours of the session
     */
    public int duration;
}
