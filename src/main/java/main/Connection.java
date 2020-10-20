package main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.micromanager.utils.ImageUtils;

import com.google.protobuf.ByteString;
import com.message.protobuf.Messages.ActionType;
import com.message.protobuf.Messages.MessageClient;
import com.message.protobuf.Messages.MessageServer;
import com.message.protobuf.Messages.Request;
import com.message.protobuf.Messages.Response;

import api.ILiveMicroApi;
import api.LiveMicroApi;
import features.AutofocusControl;
import features.ExposureControl;
import features.FocusStageControl;
import features.GalvoControl;
import features.MoreImageTesting;
import features.ROIControl;
import features.SLMControl;
import features.XYStageControl;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import mmcorej.CMMCore;
import models.IMicroscope;
import models.LiveSession;
import models.Microscope;
import task.ILiveMicroSession;
import task.LiveMicroSession;
import utils.ILogger;

public class Connection extends Thread {
	protected int protocolVersion;
	private Socket client;
	private InputStream input;
	private OutputStream output;
	private CMMCore core;
	private ILogger logger;
	private ILiveMicroApi liveMicroApi;
	private LiveSession liveSession;
	private IMicroscope microscope;
	private ILiveMicroSession liveMicroSession;
	private MoreImageTesting imageAcquisition;

	public Connection(Socket client, int protocolVersion, CMMCore core, ILogger logger, ILiveMicroApi liveMicroApi)
	{
		this.client 			= client;
		this.protocolVersion   	= protocolVersion;
		this.core				= core;
		this.logger				= logger;
		this.liveMicroApi		= liveMicroApi;
		this.liveSession 		= new LiveSession("Title", "Description");
		this.microscope			= Microscope.newInstance(core, logger);
		this.imageAcquisition	= new MoreImageTesting(core, logger);
		this.liveMicroSession 	= new LiveMicroSession(microscope, logger, liveMicroApi.getSampleApi(), liveSession);
		this.liveMicroSession.start();

		//this will invoke run() method
		this.start();
	}

	public void run()
	{
		try
		{
			//initialize output and input buffer
			this.input = client.getInputStream();
			this.output = client.getOutputStream();

			System.out.println("Sto servendo il client che ha indirizzo "+client.getInetAddress());

			while(true) { 
				MessageClient request = MessageClient.parseDelimitedFrom(input);
				if(request == null)
					break;

				/*verify version */
				if(request.getVersion() != protocolVersion ) {
					//error
					sendErrorVersion(output);
				} else {
					/*//authN the Client with the secret
					String secret = request.getCredentials().getSecret();
					if(secret == null || secret.isEmpty() || ! authenticated(secret)) {
						//error
						sendErrorAuth(output);
					} else {
						//same version, so continue communication
						Map<String, String> policies = request.getPoliciesMap();
						if(policies != null && !policies.isEmpty()) {
							handlePolicies(policies);
						}*/

						Request rq = request.getRequest();
						if(rq != null) {

							handleRequest(rq, output);
						}else {
							//error
						}
				 //  }
				}
			}
		}
		catch(Exception e)
		{
			this.logger.logError(e);
			try {
				sendErrorService(this.output);
			} catch (IOException e1) {
				this.logger.logError(e1);
			}
		}
		finally {
			//buffer and socket closing
			try {
				input.close();
				output.close();
				client.close();
			} catch (IOException e) {
				this.logger.logError(e);
			}			
		}
	}

	private void handlePolicies(Map<String, String> policies) {
		// TODO Auto-generated method stub
		//nothing for the time being
	}

	private void handleRequest(Request request, OutputStream output) throws IOException, Exception {
		switch(request.getRequestType()) {
		case START_LIVEMODE:
			this.microscope.startLiveMode();
			sendResult(output, ActionType.START_LIVEMODE);
			break;
		case STOP_LIVEMODE:
			this.microscope.stopLiveMode();
			sendResult(output, ActionType.STOP_LIVEMODE);
			break;
		case SNAP:
			/*send response*/
			sendPhoto(output, snapPhoto(core));
			break;
		case CHANGEPOSITION:
			XYStageControl.changePosition(core, request.getDoubleParam1(), request.getDoubleParam2());
			sendResult(output, ActionType.CHANGEPOSITION);
			break;			
		case AUTOFOCUS:
			controlAutofocus(core, request.getBoolParam(), request.getDoubleParam1());
			sendResult(output, ActionType.AUTOFOCUS);
			break;
		case EXPOSURE:
			ExposureControl.setExposure(core, request.getDoubleParam1());
			sendResult(output, ActionType.EXPOSURE);
			break;
		case FOCUS:
			FocusStageControl.changeStage(core, request.getDoubleParam1());
			sendResult(output, ActionType.FOCUS);
			break;
		case ROI:
			configRoi(core, request.getRoiParam().getX(), request.getRoiParam().getY(), 
					request.getRoiParam().getXSize(), request.getRoiParam().getYSize());
			sendResult(output, ActionType.ROI);
			break;
		case GALVO:
			configGalvo(core, request.getGalvoParam().getName(), request.getDoubleParam1(), request.getDoubleParam2(),
					request.getGalvoParam().getIllumination(), request.getGalvoParam().getTime());
			sendResult(output, ActionType.GALVO);
			break;
		case SLM:
			configSlm(core, request.getSlmParam().getName(), request.getSlmParam().getImage());
			sendResult(output, ActionType.SLM);
			break;
		case UNKNOWN:
			break;
		default:
			//error
			System.out.println("Default");
			break;
		}

	}

	private boolean authenticated(String username, String password) {
		// TODO Auto-generated method stub
		return true;
	}
/*
	private boolean authenticated(String secret) throws IOException {
		String localSecret = new String(Files.readAllBytes(Paths.get(Main.secretPath)));
		return secret.equals(localSecret);
	}
*/
	public byte[] snapPhoto(CMMCore core) throws Exception {
		Sample sample = null;
		if(core!=null) {
			//core.setExposure(50);
			if (core.getBytesPerPixel() == 1) {
				// 8-bit grayscale pixels
				//byte[] image = ImageAcquisition.getBytePhoto(core);
				byte[] image = imageAcquisition.getPhoto(core);
				System.out.println("Size " + image.length);
				//sample = new Sample(ImageUtils.makeProcessor(core, image), Main.folder, Main.fileName);
				return image;
			} else if (core.getBytesPerPixel() == 2){
				// 16-bit grayscale pixels
				System.out.println(" 16 bit");
				short[] image = Microscope.getShortPhoto(core);
				sample = new Sample(ImageUtils.makeProcessor(core, image), Main.folder, Main.fileName);

			} else {
				System.out.println("Dont' know how to handle images with " +
						core.getBytesPerPixel() + " byte pixels.");             
			}		
			//sample.saveToFile();
		}	

		/*ImagePlus im = IJ.openImage(Main.folder + "/" +Main.fileName);
        ImageProcessor processor = im.getProcessor();
        BufferedImage img = processor.getBufferedImage();
		//FileUtils.writeByteArrayToFile(new File("images/snapped.png"), image);
		return toByteArray(img);*/
		return sample.getImageBytes();
	}

	private void controlAutofocus(CMMCore core, boolean enable, double offset) throws Exception {
		if(offset != 0)
			AutofocusControl.setAutoFocusOffset(core, offset);
		else if(enable)
			AutofocusControl.enableContinousFocus(core);
		else
			AutofocusControl.disableContinousFocus(core);
	}

	private void configRoi(CMMCore core, int x, int y, int xSize, int ySize) throws Exception {
		if(x == 0 && y == 0 && xSize == 0 && ySize == 0)
			ROIControl.clearROI(core);
		else
			ROIControl.setROI(core, x, y, xSize, ySize);		
	}

	private void configGalvo(CMMCore core, String name, double x, double y, boolean illumination, double time) throws Exception {
		if(x == 0 && y == 0 && time == 0 )
			GalvoControl.addDevice(core, name);
		else if(time != 0)
			GalvoControl.setAndFire(core, name, time, x, y);
		else if(x != 0 && y != 0)
			GalvoControl.setPosition(core, name, x, y);
		else if(illumination)
			GalvoControl.turnOnIllumination(core, name);
		else
			GalvoControl.turnOffIllumination(core, name);
	}

	private void configSlm(CMMCore core, String name, ByteString image) throws Exception {
		if(image.size() == 0)
			SLMControl.addDevice(core, name);
		else
			SLMControl.writeImage(core, name, image.toByteArray());	
	}

	private void sendPhoto(OutputStream output, byte[] image) throws IOException {
		Response response = Response.newBuilder()
				.setResponseType(ActionType.SNAP)
				.setImage(ByteString.copyFrom(image))
				.build();
		MessageServer responseMessage = MessageServer.newBuilder()
				.setType(Client.OK_TYPE)
				.setResponse(response)
				.build();
		responseMessage.writeDelimitedTo(output);
	}

	private void sendResult(OutputStream output, ActionType type) throws IOException {
		Response response = Response.newBuilder()
				.setResponseType(type)
				.build();
		MessageServer responseMessage = MessageServer.newBuilder()
				.setType(Client.OK_TYPE)
				.setResponse(response)
				.build();
		responseMessage.writeDelimitedTo(output);	
	}

	private void sendErrorVersion(OutputStream output) throws IOException {
		MessageServer.newBuilder()
		.setType(Client.ERROR_TYPE)
		.setError(MessageServer.Error.VERSION)
		.build()
		.writeDelimitedTo(output);
	}

	private void sendErrorService(OutputStream output) throws IOException {
		MessageServer.newBuilder()
		.setType(Client.ERROR_TYPE)
		.setError(MessageServer.Error.SERVICE)
		.build()
		.writeDelimitedTo(output);
	}

	private void sendErrorAuth(OutputStream output) throws IOException {
		MessageServer.newBuilder()
		.setType(Client.ERROR_TYPE)
		.setError(MessageServer.Error.AUTH)
		.build()
		.writeDelimitedTo(output);
	}

	public static byte[] toByteArray(BufferedImage image) throws IOException {
		//return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		// convert BufferedImage to byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}
}