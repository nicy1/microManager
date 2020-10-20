package features;

import mmcorej.CMMCore;

public class ROIControl {

	public static void setROI(CMMCore core, int x, int y, int xSize, int ySize) throws Exception {
		core.setROI(x, y, xSize, ySize);
	}

	public static void clearROI(CMMCore core) throws Exception {
		core.clearROI();
	}
}
