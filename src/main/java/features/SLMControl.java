package features;

import mmcorej.CMMCore;

public class SLMControl {
	
	public static void addDevice(CMMCore core, String slmLabel) throws Exception {
		core.setSLMDevice(slmLabel);
	}
	
	public static void writeImage(CMMCore core, String slmLabel, byte[] pixels) throws Exception {
		core.setSLMImage(slmLabel, pixels);
	}

}
