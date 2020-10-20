package utils;

/**
 * Created by Princewill Okorie on 25-Jul-17.
 */
public interface ILogger {
    void logError(Exception exception);

    void logError(String message);

    void logWarning(String message);

    void logInformation(String message);
}
