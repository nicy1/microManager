package utils;

/**
 * Created by Princewill Okorie on 25-Jul-17.
 */
public class Logger implements ILogger {
    @Override
    public void logError(Exception exception) {
        System.out.println(String.format("Error: %s", exception.getMessage()));
    }

    @Override
    public void logError(String message) {
        System.out.println(String.format("Error: %s", message));
    }

    @Override
    public void logWarning(String message) {
        System.out.println(String.format("Warning: %s", message));
    }

    @Override
    public void logInformation(String message) {
        System.out.println(String.format("Info: %s", message));
    }
}
