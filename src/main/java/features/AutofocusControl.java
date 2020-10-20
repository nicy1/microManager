package features;

import mmcorej.CMMCore;

public class AutofocusControl {
	
	public static void enableContinousFocus(CMMCore core) throws Exception {
		core.enableContinuousFocus(true);
	}
	
	public static void disableContinousFocus(CMMCore core) throws Exception {
		core.enableContinuousFocus(false);
	}
	
	public static void setAutoFocusOffset(CMMCore core, double offset) throws Exception {
		core.setAutoFocusOffset(offset);
	}

}
