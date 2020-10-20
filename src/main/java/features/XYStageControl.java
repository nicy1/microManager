package features;

import mmcorej.CMMCore;

public class XYStageControl {
	
	public static void changePosition(CMMCore core, double x, double y) throws Exception {
		core.setXYPosition(x, y);
		
		//core.setRelativeXYPosition(dx, dy);
	}
	
}
