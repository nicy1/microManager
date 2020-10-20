package test;

import test.ClientTelepathology;

public class TestRequest {

	public static void main(String[] args) {
		// Singleton
		ClientTelepathology ct = ClientTelepathology.getInstance();
		TestRequest req = new TestRequest();
		try {
			ct.connect_noSSL("localhost");
			req.requestType(ct, 3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ct.close();
	}

	public void requestType(ClientTelepathology ct, int type) throws Exception {
		if(type == 1)
			send_request1(ct);
		if(type == 2)
			send_request2(ct);
		if(type == 3)
			send_request3(ct);
	}

	private void send_request1(ClientTelepathology ct) throws Exception {
		for(int i=0; i<700; i++){
			ct.getImage();
			ct.setExposure(10);
			ct.setExposure(5);
			ct.setExposure(20);
			ct.setExposure(15);
			ct.setExposure(30);
			ct.setExposure(25);
			ct.controlAutofocus(false, 0);
		}
	}

	private void send_request2(ClientTelepathology ct) throws Exception {
		for(int i=0; i<700; i++){
			ct.setExposure(10);
			ct.getImage();
			ct.controlAutofocus(false, 0);
			ct.getImage();		
		}
	}

	private void send_request3(ClientTelepathology ct) throws Exception {
		for(int i=0; i<700; i++){
			ct.getImage();
			ct.getImage();
			ct.getImage();
			ct.getVideo();
			ct.stopVideo();
			ct.controlAutofocus(false, 0);
		}
	}

}