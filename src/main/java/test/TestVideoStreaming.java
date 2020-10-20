package test;

import dagg.DaggerSingletonComponent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import api.ILiveMicroApi;
import api.ISampleApi;
import main.Main;
import mmcorej.CMMCore;
import models.IMicroscope;
import models.LiveSession;
import models.Microscope;
import task.ILiveMicroSession;
import task.LiveMicroSession;
import utils.Constant;
import utils.ILogger;
import utils.MyUrl;

public class TestVideoStreaming {
	 
	public static void main(String[] args) throws Exception {
		
		ILiveMicroSession liveMicroSession;
	    LiveSession liveSession = new LiveSession("Title", "Description");
	    
	    CMMCore core = Main.config();
	    
	    ILogger logger = DaggerSingletonComponent.create().logger();
        ILiveMicroApi liveMicroApi = DaggerSingletonComponent.create().liveMicroApi();
        IMicroscope microscope = Microscope.newInstance(core, logger);
                
        String final_cmd = "ffmpeg -i " + Constant.FRAMES_PATH +"frame%05d.png -r 25 -s 320x240 -c:v mpeg1video "
				+ "-f mpegts -rtbufsize 500000k -bf 0 -b:v 400k udp://130.192.225.164:8081";
    	
    	final_cmd = "ffmpeg -s 320x240 -r 24 -f dshow -rtbufsize 500000k -i video=\"HP HD Camera\" "
				+ "-f mpegts -codec:v mpeg1video "
				+ "-bf 0 -b 400k udp://127.0.0.1:4445";
    	
    	String from_udp_to_server = "ffmpeg -i udp://127.0.0.1:4445 -r 25 -s 320x240 -c:v mpeg1video "
				+ "-f mpegts -rtbufsize 500000k -bf 0 -b:v 400k udp://127.0.0.1:4445";
    	
    	//Process p = Runtime.getRuntime().exec(from_udp_to_server);

    	liveMicroSession 	= new LiveMicroSession(microscope, logger, liveMicroApi.getSampleApi(), liveSession);
    	liveMicroSession.start();
    	microscope.startLiveMode();
	}

}
