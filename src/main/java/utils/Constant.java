package utils;

import javax.swing.*;

/**
 * Created by Princewill Okorie on 25-Jul-17.
 */
public class Constant {

	public static final String INFO = "";
    public static final String VERSION = "1.0";
    public static final String COPYRIGHT = "";
    public static final String DESCRIPTION = "A remote access plugin for micro-manager";
    public static final String TITLE = "LiveMicro";
    public static final int MIN_WIDTH = 800;
    public static final int MIN_HEIGHT = 600;

    public static final String MICRO_ID = "micro2";
    public static final String DEFAULT_PATH = String.format("%s\\%s",
            new JFileChooser().getFileSystemView().getDefaultDirectory().toString(), TITLE);

    public static final String IMAGE_PATH = String.format("%s\\%s", Constant.DEFAULT_PATH, "images");
    public static final String BIN_PATH = String.format("%s\\%s", Constant.DEFAULT_PATH, "bin");
    public static final String IMAGE_DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final double LIVE_VIDEO_INTERVAL_MS = 40;	//25fps
    public static final String FRAMES_PATH = String.format("images/frames/");
    public static final long FRAME_TO_COLLECT = 2;
    public static final String MESSAGE_END = "00000000";
    public static final int BUFFER_SIZE = 4096;
    public static final int MESSAGE_END_SIZE = 8;
    public static final int SESSION_DURATION = 2;
    public static final int FIRST_CHILD_INDEX = 0;

    public static final int TOAST_SHORT  = 3000;
    public static final int TOAST_LONG = 5000;
}
