package features;

import mmcorej.CMMCore;

public class FocusStageControl {
	
	//Sets the position of the stage in microns. Uses the current Z positioner (focus) device.
	public static void changeStage(CMMCore core, double position) throws Exception {
		core.setPosition(position);
	}

}
