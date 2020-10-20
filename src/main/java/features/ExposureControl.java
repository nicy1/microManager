package features;

import mmcorej.CMMCore;

public class ExposureControl {

	public static void setExposure(CMMCore core, double exp) throws Exception{
		core.setExposure(exp);
	}

}
