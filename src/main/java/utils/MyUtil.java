package utils;

import org.joda.time.DateTime;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Created by Princewill Okorie on 25-Jul-17.
 */
public class MyUtil {
    public static String fillIconPath(String iconName) {
        return String.format("/icons/%s", iconName);

    }

    public static String encode(String text) {
        byte[] encoded = Base64.getEncoder().encode(text.getBytes());
        return new String(encoded, Charset.forName("UTF-8"));
    }

    public static String decode(String text) {
        byte[] decoded = Base64.getDecoder().decode(text.getBytes());
        return new String(decoded, Charset.forName("UTF-8"));
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    

    public static String formatImageFileName(String ext, long width, long height) {
        return String.format("sample_%s_%dx%d.%s",
                DateTime.now().toString(Constant.IMAGE_DATE_FORMAT), width, height, ext);
    }


    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int extensionPos = filename.lastIndexOf(46);
        int lastUnixPos = filename.lastIndexOf(47);
        int lastWindowsPos = filename.lastIndexOf(92);
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);

        int index = lastSeparator > extensionPos ? -1 : extensionPos;
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }
}
