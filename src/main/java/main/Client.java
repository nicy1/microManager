package main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.micromanager.utils.ImageUtils;

import com.google.protobuf.ByteString;
import com.message.protobuf.Messages.ActionType;
import com.message.protobuf.Messages.MessageClient;
import com.message.protobuf.Messages.MessageServer;
import com.message.protobuf.Messages.Request;
import com.message.protobuf.Messages.Response;

import ij.process.ImageProcessor;
import mmcorej.CMMCore;


public class Client {
	private static Socket socket;
	private static final int port = 33333;
	private static final int version = 1;
	public static final int ERROR_TYPE = 2;
	public static final int OK_TYPE = 1;
	public Client() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		OutputStream output = null;
		InputStream input = null;
		try {
			socket = new Socket("localhost", port);
			output = socket.getOutputStream();
			input = socket.getInputStream();
			
			//for the time being I omit the credentials message
			sendPhotoRequest(output); 
			
			MessageServer response = MessageServer.parseDelimitedFrom(input);
			
			/*handle message */
			switch(response.getType()) {
			case 1:
				Response rs = response.getResponse();
				if(rs != null) {
					/*verify version */
					handleResponse(rs);			
				}else {
					//error
				}
				break;
			case 2:
				//server error
				break;
			default:
				//error
				break;
			}
			output.close();
			input.close();
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendPhotoRequest(OutputStream output) throws IOException {
		Request request = Request.newBuilder()				
				.setRequestType(ActionType.SNAP)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setRequest(request)
				.build();
		
		requestMessage.writeDelimitedTo(output);
	}
	
	public static void sendVideoRequest(OutputStream output) throws IOException {
		Request request = Request.newBuilder()				
				.setRequestType(ActionType.START_LIVEMODE)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
	}
	
	public static void stopVideoRequest(OutputStream output) throws IOException {
		Request request = Request.newBuilder()				
				.setRequestType(ActionType.STOP_LIVEMODE)
				.build();
		MessageClient requestMessage = MessageClient.newBuilder()
				.setVersion(version)
				.setRequest(request)
				.build();
		requestMessage.writeDelimitedTo(output);
	}

	public static void handleResponse(Response rs) throws IOException {
		switch(rs.getResponseType()) {
		case START_LIVEMODE:
			break;
		case SNAP:
			/*save Image*/
			ByteString image = rs.getImage();
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(image.toByteArray()));
			FileUtils.writeByteArrayToFile(new File("images/rec.png"), image.toByteArray());
			
			/*f = new File(String.format("%s/%s", "images", fileName));
                ImageIO.write(imageProcessor.getBufferedImage(), "png", f);*/
			break;
		case UNKNOWN:
			break;
		default:
			//error
			break;
		}
	}


}
