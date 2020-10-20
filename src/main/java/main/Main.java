package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.commons.lang3.SystemUtils;

import api.ILiveMicroApi;
import dagg.DaggerSingletonComponent;
import mmcorej.CMMCore;
import utils.ILogger;

public class Main {
	private static final int port = 33333;
	private static final int version = 1;
	public static final String secretPath = "secret.txt";
	private static final boolean MICRO_EMULATOR = false;
	public static final String fileName = "test.png";
	public static final String folder = "images";

	public static void main(String[] args) {	
		System.setProperty("javax.net.ssl.keyStore", "clientkeystore");
		System.setProperty("javax.net.ssl.keyStorePassword", "Emmanuel1@");

		SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		ServerSocket ss;

		try {

			CMMCore core = config();
			ILogger logger = DaggerSingletonComponent.create().logger();
			ILiveMicroApi liveMicroApi = DaggerSingletonComponent.create().liveMicroApi();
			//getVideo(core);
			
			boolean ssl_connection = false;
			if (ssl_connection){
				ss = ssf.createServerSocket(port);
			}
			else {
				ss = new ServerSocket(port);
			}
			
			logger.logInformation("Server started");

			//infinite loop, waiting for connections
			while(true)
			{
			    Socket client = ss.accept();
				System.out.println("Client accepted");
				
				if (ssl_connection) {
				    //((SSLSocket) client).setNeedClientAuth(true);
				    SSLSession session = ((SSLSocket) client).getSession();
				    Certificate[] cchain2 = session.getLocalCertificates();
				    for (int i = 0; i < cchain2.length; i++) {
					     System.out.println(((X509Certificate) cchain2[i]).getSubjectDN());
				    }
				    System.out.println("Peer host is " + session.getPeerHost());
				    System.out.println("Cipher is " + session.getCipherSuite());
				    System.out.println("Protocol is " + session.getProtocol());
				    System.out.println("ID is " + new BigInteger(session.getId()));
				    System.out.println("Session created in " + session.getCreationTime());
				    System.out.println("Session accessed in " + session.getLastAccessedTime());
				}
				
				//the new request is handled by a new thread, then the loop is repeated
				Connection newConnection = new Connection(client, version, core, logger, liveMicroApi);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception: " +e.getMessage());
			System.exit(1);
		}

	}

	public static CMMCore config() throws Exception {
		if(MICRO_EMULATOR) {			
			//Use the file
			System.out.println("The program is using files");
			return null;			
		}

		if (SystemUtils.IS_OS_WINDOWS) {
			//Should point to MMCoreJ_wrap.dll file
			System.setProperty("mmcorej.library.path", "C:/Program Files/Micro-Manager-1.4");
		} else if (SystemUtils.IS_OS_MAC) {
			//
		} else if (SystemUtils.IS_OS_LINUX) {
			//Should point to libMMCoreJ_wrap.so file
			System.setProperty("mmcorej.library.path", "/usr/local/ImageJ");
		} else {
			System.out.println("This is a SPECIAL OS.");
		}

		CMMCore core = new CMMCore();
		core.loadDevice("Camera", "DemoCamera", "DCam");
		core.initializeDevice("Camera");
		core.setCameraDevice("Camera");
		return core;
	}

	public static void getVideo(CMMCore core) throws Exception {

		core.setExposure(50);
		core.initializeCircularBuffer();
		core.startContinuousSequenceAcquisition(25);

		while(core.isSequenceRunning()) {
			while(core.getRemainingImageCount() > 0) {
				if (core.getBytesPerPixel() == 1) {
					// 8-bit grayscale pixels
					byte[] img = (byte[])core.getImage();
					System.out.println("Image snapped, " + img.length + " pixels total, 8 bits each.");
					System.out.println("Pixel [0,0] value = " + img[0]);
					//writeFile(img);
				} else if (core.getBytesPerPixel() == 2){
					// 16-bit grayscale pixels
					short[] img = (short[])core.getImage();
					System.out.println("Image snapped, " + img.length + " pixels total, 8 bits each.");
					System.out.println("Pixel [0,0] value = " + img[0]);
					//writeFile(img);
				} else {
					System.out.println("Dont' know how to handle images with " +
							core.getBytesPerPixel() + " byte pixels.");             
				}
			}				
		}	
	}


}
