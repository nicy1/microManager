package test;

import test.ClientTelepathology;

public class TestRequest {

	public void requestType(ClientTelepathology ct, int type) throws Exception {
		if(type == 1)
			send_request1(ct);
		if(type == 2)
			send_request2(ct);
	}

	private void send_request1(ClientTelepathology ct) throws Exception {
		for(int i=0; i<400; i++){
			ct.getImage();
			ct.controlAutofocus(false, 0);
			ct.setExposure(5);
			ct.setExposure(15);
			ct.getImage();
			ct.setExposure(15);
			ct.controlAutofocus(false, 0);
			ct.controlAutofocus(false, 0);
			ct.getImage();
			ct.setExposure(15);
			ct.setExposure(15);
			ct.setExposure(15);
			ct.getImage();
			ct.controlAutofocus(false, 0);
			ct.getImage();
			ct.controlAutofocus(false, 0);
		}
	}

	private void send_request2(ClientTelepathology ct) throws Exception {
		for(int i=0; i<300; i++){
			ct.controlAutofocus(false, 0);
			ct.getImage();
			ct.setExposure(10);
			ct.getVideo();
			ct.controlAutofocus(false, 0);
			ct.stopVideo();
			ct.setExposure(10);
			ct.getVideo();
			ct.controlAutofocus(false, 0);
			ct.stopVideo();
			ct.setExposure(5);
			ct.getImage();
		}
	}

}