package features;

import mmcorej.CMMCore;

public class GalvoControl {

	public static void setAndFire(CMMCore core, String galvoLabel, double pulseTime_us, double x, double y) throws Exception {
		core.pointGalvoAndFire(galvoLabel, x, y, pulseTime_us);
	}
	
	public static void setPosition(CMMCore core, String galvoLabel, double x,  double y) throws Exception {
		core.setGalvoPosition(galvoLabel, x, y);
	}
	
	public static void turnOnIllumination(CMMCore core, String galvoLabel) throws Exception {
		core.setGalvoIlluminationState(galvoLabel, true);
	}
	
	public static void turnOffIllumination(CMMCore core, String galvoLabel) throws Exception {
		core.setGalvoIlluminationState(galvoLabel, false);
	}
	
	public static void addDevice(CMMCore core, String galvoLabel) throws Exception {
		core.setGalvoDevice(galvoLabel);
	}
	
}
