package models;

import mmcorej.CMMCore;
import mmcorej.StrVector;

import java.io.IOException;

import org.joda.time.DateTime;
import org.micromanager.utils.ImageUtils;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import utils.Constant;
import utils.ILogger;
import utils.MyUrl;

/**
 * Created by Princewill Princewill Okoriee on 25-Jul-17.
 */
public class Microscope implements IMicroscope {
	private final CMMCore hardware;
	private final ILogger logger;
	private PublishSubject<Sample> sampleSubject;
	private PublishSubject<Sample> videoSubject;
	private Subscription liveMode;
	private Process ffmpegProcess;

	private Microscope(CMMCore hardware, ILogger logger) {
		this.hardware = hardware;
		this.logger = logger;
		sampleSubject = PublishSubject.create();
		videoSubject = PublishSubject.create();

		init();
	}

	public static IMicroscope newInstance(CMMCore core, ILogger logger) {
		return new Microscope(core, logger);
	}

	private void init() {
		try {
			this.hardware.setExposure(50);
			this.hardware.initializeCircularBuffer();
		} catch (Exception e) {
			logger.logError(e);
		}
	}

	private void startUdpForStreaming() throws IOException {
		//start udp server for ffmpeg
		String final_cmd = "ffmpeg -i udp:/0.0.0.0:4445 -r 25 -s 320x240 -c:v mpeg1video -f mpegts "
				+ "-rtbufsize 500000k -bf 0 -b:v 800k http://" + MyUrl.API_IP + ":8081" + 
				"/supersecret/" + Constant.MICRO_ID + "/320/240";

		System.out.println("Running command" + final_cmd);

		this.ffmpegProcess = Runtime.getRuntime().exec(final_cmd);
		
	}

	private void stopUdpForStreaming() {
		//stop udp server for ffmpeg
		if(this.ffmpegProcess != null)
			this.ffmpegProcess.destroy();
	}

	@Override
	public void startLiveMode() {

		try {
			//this.startUdpForStreaming();

			this.hardware.startContinuousSequenceAcquisition(Constant.LIVE_VIDEO_INTERVAL_MS);
			logger.logInformation(String.format("Live image started @ %s", DateTime.now().toString()));
            int c = 0;
			while (this.hardware.isSequenceRunning()) {
				while (this.hardware.getRemainingImageCount() > 0) {
					// counter added for dataset purpose
					c++;
					if(c == 5)
					   return;
					if (this.hardware.getBytesPerPixel() == 1) {
						// 8-bit grayscale pixels
						Sample sample = new Sample(ImageUtils.makeProcessor(this.hardware, this.hardware.popNextImage()));
						videoSubject.onNext(sample);
					} else if (this.hardware.getBytesPerPixel() == 2){
						// 16-bit grayscale pixels
						short[] img = (short[])hardware.popNextImage();
						System.out.println("Image snapped, " + img.length + " pixels total, 16 bits each.");
						System.out.println("Pixel [0,0] value = " + img[0]);
					} else {
						System.out.println("Dont' know how to handle images with " +
								this.hardware.getBytesPerPixel() + " byte pixels.");             
					}
				}
			}
			System.out.println("No runnning sequence");
			videoSubject.onCompleted();

		} catch (Exception e) {
			videoSubject.onError(e);
			videoSubject.onCompleted();
		}

		/*try {
			this.hardware.startContinuousSequenceAcquisition(Constant.LIVE_VIDEO_INTERVAL_MS);

			while(this.hardware.isSequenceRunning()) {
				while(this.hardware.getRemainingImageCount() > 0) {
					if (this.hardware.getBytesPerPixel() == 1) {
						// 8-bit grayscale pixels
						Sample sample = new Sample(ImageUtils.makeProcessor(hardware, hardware.popNextImage()));
                        videoSubject.onNext(sample);
					} else if (this.hardware.getBytesPerPixel() == 2){
						// 16-bit grayscale pixels
						ImageAcquisition.getShortLive(this.hardware);
					} else {
						System.out.println("Dont' know how to handle images with " +
								this.hardware.getBytesPerPixel() + " byte pixels.");             
					}
				}				
			}	
		} catch (Exception e) {
			videoSubject.onError(e);
			videoSubject.onCompleted();
		}*/
	}

	@Override
	public Observable<Sample> sampleSubscribe() {
		return sampleSubject.asObservable().subscribeOn(Schedulers.newThread());
	}

	@Override
	public Observable<Sample> videoSubscribe() {
		return videoSubject.asObservable().subscribeOn(Schedulers.newThread());
	}

	@Override
	public void stopLiveMode() {
		try {
			this.stopUdpForStreaming();

			this.hardware.stopSequenceAcquisition();
			this.hardware.clearCircularBuffer();
			this.liveMode.unsubscribe();
			logger.logInformation(String.format("Live mode stopped @ %s", DateTime.now().toString()));
		} catch (Exception e) {
			logger.logError(e);
		}
	}

	@Override
	public void snapImage() {
		try {
			this.hardware.snapImage();
			Sample sample = new Sample(ImageUtils.makeProcessor(hardware, hardware.getImage()));
			logger.logInformation(String.format("Image captured @ %s", DateTime.now().toString()));
			sampleSubject.onNext(sample);
		} catch (Exception e) {
			logger.logError(e);
		}
	}

	//snapPhoto for the server version code
	public static byte[] getBytePhoto(CMMCore core) throws Exception {
		core.snapImage();
		byte[] img = (byte[])core.getImage();
		System.out.println("Image snapped, " + img.length + " pixels total, 8 bits each.");
		System.out.println("Pixel [0,0] value = " + img[0]);
		//writeFile(img);
		return img;
	}

	public static short[] getShortPhoto(CMMCore core) throws Exception {
		core.snapImage();
		short[] img = (short[])core.getImage();
		System.out.println("Image snapped, " + img.length + " pixels total, 8 bits each.");
		System.out.println("Pixel [0,0] value = " + img[0]);
		//writeFile(img);
		return img;
	}

	@Override
	public void listDeviceProperties(String deviceName) {
		try {
			StrVector properties = hardware.getDevicePropertyNames(deviceName);
			for (int i = 0; i < properties.size(); i++) {
				String prop = properties.get(i);
				String val = hardware.getProperty(deviceName, prop);
				System.out.println("Name: " + prop + ", value: " + val);
			}
		} catch (Exception e) {
			logger.logError(e);
		}
	}

	@Override
	public void toggleLiveMode() {
		if (liveMode.isUnsubscribed()) startLiveMode();
		else stopLiveMode();
	}

	@Override
	public boolean isLiveModeOn() {
		return !liveMode.isUnsubscribed();
	}

	@Override
	public void sampleUnSubscribe() {
		if (isLiveModeOn()) stopLiveMode();
		sampleSubject.onCompleted();
		liveMode.unsubscribe();
	}

	@Override
	public void videoUnSubscribe() {
		if (isLiveModeOn()) stopLiveMode();
		videoSubject.onCompleted();
		liveMode.unsubscribe();
	}
}
