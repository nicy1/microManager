package utils;

public class MyUrl {
	public static final String API_IP = "192.168.43.215";
    private static final String BASE_API = "https://" + API_IP + ":33333"; /*https://livemicroapi.herokuapp.com";*/
    public static final String SESSION_API = String.format("%s/session", MyUrl.BASE_API);
    public static final String USER_API = String.format("%s/user", MyUrl.BASE_API);
    public static final String SAMPLE_API = String.format("%s/sample", MyUrl.BASE_API);
    public static final String OPERATION_API = String.format("%s/operation", MyUrl.BASE_API);
    public static final String LIVESESSION_API = String.format("%s/livesession", MyUrl.BASE_API);
}
