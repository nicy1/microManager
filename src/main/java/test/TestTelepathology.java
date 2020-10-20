package test;

import test.ClientTelepathology;
import test.TestRequest;

public class TestTelepathology {

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

}
