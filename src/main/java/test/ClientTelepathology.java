package test;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.io.FileUtils;

import com.sun.net.ssl.internal.ssl.Provider;
import com.google.protobuf.ByteString;
import com.message.protobuf.Messages.ActionType;
import com.message.protobuf.Messages.Credentials;
import com.message.protobuf.Messages.MessageClient;
import com.message.protobuf.Messages.MessageServer;
import com.message.protobuf.Messages.Request;
import com.message.protobuf.Messages.Response;

import test.ConfReader;

public class ClientTelepathology {
	private Socket socket;
	private static final int port = 33333;
	private static final int version = 1;
	public static final int ERROR_TYPE = 2;
	public static final int OK_TYPE = 1;
	private OutputStream output;
	public InputStream input;
	
	private CreateDataset create_dataset;
	
	public ClientTelepathology() {
		System.setProperty("javax.net.ssl.trustStore", "trustStore");
	    System.setProperty("javax.net.ssl.trustStorePassword", "micromanager");
		// Registering the JSSE provider
		//Security.addProvider(new Provider());    
	    create_dataset = new CreateDataset();
	}
	
	// Singleton for this class
	private static ClientTelepathology single_instance = new ClientTelepathology();
	
	public static ClientTelepathology getInstance() {
		return single_instance;
	}
	
	public boolean connect(String host) {
		SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {

			socket = ssf.createSocket(host, port);
			SSLSession session = ((SSLSocket) socket).getSession();
			Certificate[] cchain = session.getPeerCertificates();
			System.out.println("The Certificates used by peer");
			for (int i = 0; i < cchain.length; i++) {
				System.out.println(((X509Certificate) cchain[i]).getSubjectDN());
			}
			System.out.println("Peer host is " + session.getPeerHost());
			System.out.println("Cipher is " + session.getCipherSuite());
			System.out.println("Protocol is " + session.getProtocol());
			System.out.println("ID is " + new BigInteger(session.getId()));
			System.out.println("Session created in " + session.getCreationTime());
			System.out.println("Session accessed in " + session.getLastAccessedTime());

			output = socket.getOutputStream();
			input = socket.getInputStream();
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	public byte[] getImage() throws Exception {
		byte[] image = null;
		long tReq = System.currentTimeMillis();
		int reqS = sendPhotoRequest(output);

		MessageServer response = MessageServer.parseDelimitedFrom(input);
		if(response == null)
			System.out.println("Is null");

		/*handle message */
		switch(response.getType()) {
		case OK_TYPE:
			Response rs = response.getResponse();
			long tResp = System.currentTimeMillis();
			int respS = response.getSerializedSize();
			create_dataset.FileWriteLine(false, reqS, reqS/respS, tResp-tReq, rs.getResponseTypeValue());
			//check if policies
			Map<String, String> policies = response.getPoliciesMap();
			if(policies != null && !policies.isEmpty()) {
				handlePolicies(policies);
			}

			//
			if(rs != null && rs.getResponseType()==ActionType.SNAP) {
				ByteString img = rs.getImage();
				image = img.toByteArray();
				System.out.println("Received size " + image.length);
				//FileUtils.writeByteArrayToFile(new File("images/real/rec.png"), img.toByteArray());	
				/*Path path = Paths.get("images/real/rec.png");
				Files.write(path, img.toByteArray());
				System.out.println("Scritto file ");*/
			}else {
				//error
			}
			break;
		case ERROR_TYPE:
			//server error
			System.out.println("Error at the plugin side");
			throw new Exception("Error at the plugin side");
		default:
			//error
			break;
		}

		return image;
	}
	
	public byte[] changePosition(int x, int y) throws Exception {
		byte[] image = null;
		sendPositionRequest(output, x, y);

		MessageServer response = MessageServer.parseDelimitedFrom(input);
		if(response == null)
			System.out.println("Is null");

		/*handle message */
		switch(response.getType()) {
			case OK_TYPE:
				Response rs = response.getResponse();
				//check if policies
				Map<String, String> policies = response.getPoliciesMap();
				if(policies != null && !policies.isEmpty()) {
					handlePolicies(policies);
				}

				//
				if(rs != null && rs.getResponseType()==ActionType.CHANGEPOSITION) {
					System.out.println("Received confirmation");
				}else {
					//error
				}
				break;
			case ERROR_TYPE:
				//server error
				System.out.println("Error at the plugin side");
				throw new Exception("Error at the plugin side");
			default:
				//error
				break;
		}

		return image;
	}
	
	public byte[] controlAutofocus(boolean enable, int y) throws Exception {
		byte[] image = null;
		long tReq = System.currentTimeMillis();
		int reqS = sendAutoFocusRequest(output, enable, y);

		MessageServer response = MessageServer.parseDelimitedFrom(input);
		if(response == null)
			System.out.println("Is null");

		/*handle message */
		switch(response.getType()) {
			case OK_TYPE:
				Response rs = response.getResponse();
				long tResp = System.currentTimeMillis();
				int respS = response.getSerializedSize();
				create_dataset.FileWriteLine(false, reqS, reqS/respS, tResp-tReq, rs.getResponseTypeValue());
				//check if policies
				Map<String, String> policies = response.getPoliciesMap();
				if(policies != null && !policies.isEmpty()) {
					handlePolicies(policies);
				}
				//
				if(rs != null && rs.getResponseType()==ActionType.AUTOFOCUS) {
					System.out.println("Received confirmation");
				}else {
					//error
				}
				break;
			case ERROR_TYPE:
				//server error
				System.out.println("Error at the plugin side");
				throw new Exception("Error at the plugin side");
			default:
				//error
				break;
		}

		return image;
	}
	
	public void getVideo() throws Exception {
		long tReq = System.currentTimeMillis();
		int reqS = sendVideoRequest(output);

		MessageServer response = MessageServer.parseDelimitedFrom(input);
		if(response == null)
			System.out.println("Is null");

		/*handle message */
		switch(response.getType()) {
			case OK_TYPE:
				Response rs = response.getResponse();
				long tResp = System.currentTimeMillis();
				int respS = response.getSerializedSize();
				create_dataset.FileWriteLine(false, reqS, reqS/respS, tResp-tReq, rs.getResponseTypeValue());
				//check if policies
				Map<String, String> policies = response.getPoliciesMap();
				if(policies != null && !policies.isEmpty()) {
					handlePolicies(policies);
				}
				//
				if(rs != null && rs.getResponseType()==ActionType.START_LIVEMODE) {
					System.out.println("Received confirmation");
				}else {
					//error
				}
				break;
			case ERROR_TYPE:
				//server error
				System.out.println("Error at the plugin side");
				throw new Exception("Error at the plugin side");
			default:
				//error
				break;
		}
	}
	
	public void stopVideo() throws Exception {
		long tReq = System.currentTimeMillis();
		int reqS = sendStopVideo(output);
        
		MessageServer response = MessageServer.parseDelimitedFrom(input);
		if(response == null)
			System.out.println("Is null");

		/*handle message */
		switch(response.getType()) {
			case OK_TYPE:
				Response rs = response.getResponse();
				long tResp = System.currentTimeMillis();
				int respS = response.getSerializedSize();
				create_dataset.FileWriteLine(false, reqS, reqS/respS, tResp-tReq, rs.getResponseTypeValue());
				//check if policies
				Map<String, String> policies = response.getPoliciesMap();
				if(policies != null && !policies.isEmpty()) {
					handlePolicies(policies);
				}
				//
				if(rs != null && rs.getResponseType()==ActionType.STOP_LIVEMODE) {
					System.out.println("Received confirmation");
				}else {
					//error
				}
				break;
			case ERROR_TYPE:
				//server error
				System.out.println("Error at the plugin side");
				throw new Exception("Error at the plugin side");
			default:
				//error
				break;
		}
	}
	
	public void controlFocus(int x) throws Exception {
		sendFocusRequest(output, x);

		MessageServer response = MessageServer.parseDelimitedFrom(input);
		if(response == null)
			System.out.println("Is null");

		/*handle message */
		switch(response.getType()) {
			case OK_TYPE:
				Response rs = response.getResponse();
				//check if policies
				Map<String, String> policies = response.getPoliciesMap();
				if(policies != null && !policies.isEmpty()) {
					handlePolicies(policies);
				}
				//
				if(rs != null && rs.getResponseType()==ActionType.FOCUS) {
					System.out.println("Received confirmation");
				}else {
					//error
				}
				break;
			case ERROR_TYPE:
				//server error
				System.out.println("Error at the plugin side");
				throw new Exception("Error at the plugin side");
			default:
				//error
				break;
		}
	}
	
	public void setExposure(int x) throws Exception {
		long tReq = System.currentTimeMillis();
		int reqS = sendExposureRequest(output, x);

		MessageServer response = MessageServer.parseDelimitedFrom(input);
		if(response == null)
			System.out.println("Is null");

		/*handle message */
		switch(response.getType()) {
			case OK_TYPE:
				Response rs = response.getResponse();
				long tResp = System.currentTimeMillis();
				int respS = response.getSerializedSize();
				create_dataset.FileWriteLine(false, reqS, reqS/respS, tResp-tReq, rs.getResponseTypeValue());
				//check if policies
				Map<String, String> policies = response.getPoliciesMap();
				if(policies != null && !policies.isEmpty()) {
					handlePolicies(policies);
				}
				//
				if(rs != null && rs.getResponseType()==ActionType.EXPOSURE) {
					System.out.println("Received confirmation");
				}else {
					//error
				}
				break;
			case ERROR_TYPE:
				//server error
				System.out.println("Error at the plugin side");
				throw new Exception("Error at the plugin side");
			default:
				//error
				break;
		}
	}


	public MessageServer sendConfiguration(Request request) throws Exception {
		ActionType requestedType = request.getRequestType();

		sendConfiguration(output, request);

		MessageServer response = MessageServer.parseDelimitedFrom(input);
		if(response == null)
			System.out.println("Is null");

		/*handle message */
		switch(response.getType()) {
		case OK_TYPE:
			Response rs = response.getResponse();
			//check if policies
			Map<String, String> policies = response.getPoliciesMap();
			if(policies != null && !policies.isEmpty()) {
				handlePolicies(policies);
			}

			if(rs == null || rs.getResponseType() != requestedType) {
				//error

			}
			break;
		case ERROR_TYPE:
			//server error
			System.out.println("Error " + response.getError());
			throw new Exception("Error at the plugin side");
		default:
			//error
			break;
		}

		return response;
	}

	public boolean close() {

		try {
			output.close();
			input.close();
			socket.close();

			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
	}

	private void handlePolicies(Map<String, String> policies) {
		// TODO Auto-generated method stub

	}

	private Credentials.Builder getCredentials() throws IOException{
		ConfReader cf = new ConfReader();
		return Credentials.newBuilder()
				.setSecret(cf.getPluginSecret());
	}

	private int sendPhotoRequest(OutputStream output) throws IOException, InterruptedException {
		Request request = Request.newBuilder()
				.setRequestType(ActionType.SNAP)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				//.setCredentials(getCredentials())
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
		//create_dataset.FileWriteLine(requestMessage, false);
		return requestMessage.getRequest().getSerializedSize();
	}
	
	private void sendPositionRequest(OutputStream output, int x, int y) throws IOException {
		Request request = Request.newBuilder()				
				.setRequestType(ActionType.CHANGEPOSITION)
				.setDoubleParam1(x)
				.setDoubleParam2(y)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setCredentials(getCredentials())
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
	}
	
	private int sendAutoFocusRequest(OutputStream output, boolean enable, int y) throws IOException, InterruptedException {
		Request request = Request.newBuilder()				
				.setRequestType(ActionType.AUTOFOCUS)
				.setBoolParam(enable)
				.setDoubleParam1(y)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setCredentials(getCredentials())
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
		Thread.holdsLock(input);
		//create_dataset.FileWriteLine(requestMessage, false);
		return requestMessage.getRequest().getSerializedSize();
	}


	private int sendVideoRequest(OutputStream output) throws IOException, InterruptedException {
		Request request = Request.newBuilder()
				.setRequestType(ActionType.START_LIVEMODE)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setCredentials(getCredentials())
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
		//create_dataset.FileWriteLine(requestMessage, false);
		return requestMessage.getRequest().getSerializedSize();
	}
	
	private int sendStopVideo(OutputStream output) throws IOException, InterruptedException {
		Request request = Request.newBuilder()
				.setRequestType(ActionType.STOP_LIVEMODE)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setCredentials(getCredentials())
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
		//create_dataset.FileWriteLine(requestMessage, false);
		return requestMessage.getRequest().getSerializedSize();
	}
	
	private int sendFocusRequest(OutputStream output, int x) throws IOException, InterruptedException {
		Request request = Request.newBuilder()
				.setRequestType(ActionType.FOCUS)
				.setDoubleParam1(x)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setCredentials(getCredentials())
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
		//create_dataset.FileWriteLine(requestMessage, false);
		return requestMessage.getRequest().getSerializedSize();
	}
	
	private int sendExposureRequest(OutputStream output, int x) throws IOException, InterruptedException {
		Request request = Request.newBuilder()
				.setRequestType(ActionType.EXPOSURE)
				.setDoubleParam1(x)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setCredentials(getCredentials())
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
		//create_dataset.FileWriteLine(requestMessage, false);
		return requestMessage.getRequest().getSerializedSize();
	}

	private void sendConfiguration(OutputStream output, Request request) throws IOException {
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setCredentials(getCredentials())
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
	}

}
