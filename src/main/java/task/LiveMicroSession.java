package task;

import models.IMicroscope;
import models.LiveSession;
import models.Sample;
import request.ImageUploadRequest;
import response.SampleResponse;
import api.ISampleApi;
import rx.Observable;
import utils.Constant;
import utils.ILogger;
import utils.MyUrl;
import utils.MyUtil;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Princewill Princewill Okoriee on 31-Aug-17.
 */
public class LiveMicroSession implements ILiveMicroSession {
	private final IMicroscope microscope;
	private final ILogger logger;
	private final ISampleApi sampleApi;
	private LiveSession liveSession;
	private final AtomicLong frameCounter = new AtomicLong();
	private static int frameRate;

	public LiveMicroSession(IMicroscope microscope, ILogger logger, ISampleApi sampleApi, LiveSession data) {
		this.microscope = microscope;
		this.logger = logger;
		this.sampleApi = sampleApi;

		frameRate = (int)(1000.0/Constant.LIVE_VIDEO_INTERVAL_MS);

		this.liveSession = data;
	}

	@Override
	public void stop() {

	}

	@Override
	public void start() {
		// start thread to upload images
		this.microscope
		.sampleSubscribe()
		.subscribe(
				this::sendToServer,
				error -> logger.logError(error.getMessage()));

		// start thread to stream video
		this.microscope
		.videoSubscribe()
		.subscribe(
				this::streamVideo,
				error -> logger.logError(error.getMessage()));
	}

	@Override
	public Observable<SampleResponse> uploadImage(File file) {
		try {
			BufferedImage image = ImageIO.read(file);
			ImageUploadRequest request = new ImageUploadRequest();
			request.userid = this.liveSession.user.id;
			request.fkLiveSession = this.liveSession.code;
			request.height = Math.toIntExact(image.getHeight());
			request.width = Math.toIntExact(image.getWidth());
			request.data = Sample.getImageBytes(image);
			request.filename = MyUtil.formatImageFileName(MyUtil.getExtension(file.getName()), request.width, request.height);
			return toCloud(request);
		} catch (IOException e) {
			logger.logError(e);
		}

		return null;
	}

	private Observable<SampleResponse> toCloud(ImageUploadRequest request) {
		return this.sampleApi.uploadImage(request);
	}

	private void sendToServer(Sample sample) {
		try {
			System.out.println("Send to server");
			ImageUploadRequest request = new ImageUploadRequest();
			request.userid = this.liveSession.user.id;
			request.fkLiveSession = this.liveSession.code;
			request.height = Math.toIntExact(sample.getHeight());
			request.width = Math.toIntExact(sample.getWidth());
			request.data = sample.getImageBytes();
			request.filename = sample.getFileName();
			toCloud(request)
			.subscribe(response -> {
				if (response.success) {
					logger.logInformation("Image sent OK!");
				} else {
					logger.logError("Image sent KO: " + response.error.message);
				}
			}, error -> logger.logError(error.getMessage()));
		} catch (IOException e) {
			logger.logError(e);
		}

	}

	private void streamVideo(Sample sample) {
		try {
			System.out.println("Collected frame " + this.frameCounter.get());
			//collect N frames, then send them to server as stream
			if(this.frameCounter.get() < Constant.FRAME_TO_COLLECT) {

				// save as file
				long n = this.frameCounter.incrementAndGet();
				String filename = String.format("frame%05d.png", n);
				File file = new File(Constant.FRAMES_PATH + filename);
				ImageIO.write(toBufferedImage(sample.getImage()), "png", file);

			} else {
				// send as stream to api
				System.out.println("Sending frames");
				 SimpleDateFormat ft = 
					      new SimpleDateFormat ("hh:mm:ss:SSS a zzz");
				java.util.Date date = new java.util.Date();
				System.out.println(ft.format(date));

				//run ffmpeg command for sending webcam stream to server
				String command = "ffmpeg -s 640x480 -f video4linux2 -i /dev/video0 -f mpeg1video"
						+ " -b 800k -r 30 http://example.com:8082/password/640/480/";

				//run ffmpeg command for sending webcam stream to server
				String windows = "ffmpeg -s 320x240 -r 24 -f dshow -rtbufsize 500000k -i video=\"HP HD Camera\" "
						+ "-f mpegts -codec:v mpeg1video "
						+ "-bf 0 -b 400k http://130.192.225.164:8081/supersecret/micro2/320/240";

				//run ffmpeg command for creating a video streaming from a set of frames
				String fromFrames = "ffmpeg -r 60 -f image2 -s 1920x1080 -i frame%05d.png -vcodec libx264 "
						+ "-crf 25  -pix_fmt yuv420p test.mp4";

				String c = "ffmpeg -s 320x240 -r 25 -f image2 -i frame%05d.png -b:v 800k -vcodec libx264 "
						+ "-f mpeg1video http://130.192.225.164:8081/supersecret/320/240";

				String send_video = "ffmpeg -i test.mp4 -r 25 -s 320x240 -c:v mpeg1video -f mpegts -rtbufsize 500000k -bf 0 "
						+ "-b:v 400k http://130.192.225.164:8081/supersecret/320/240";
				
				String send_loop = "ffmpeg -loop 1 -i images/frames/frame%05d.png -r 25 -s 320x240 -c:v mpeg1video -f mpegts "
						+ "-rtbufsize 500000k -bf 0 -b:v 400k http://130.192.225.164:8081/supersecret/320/240";

				String final_real= "ffmpeg -i images/frames/frame%05d.png -r 25 -s 320x240 -c:v mpeg1video -f mpegts -rtbufsize 500000k -bf 0 "
						+ "-b:v 400k http://130.192.225.164:8081/supersecret/320/240";

				/***
				 * -r 					: 	frame rate, fps
				 * -crf 				: 	the quality, lower means better quality, 15-25 is usually good
				 * -pix_fmt				:	specifies the pixel format, change this as needed
				 * -s					:	resolution
				 * -b:v					:	target (average) bit rate for the encoder to use
				 * -multiple_requests 	: 	(valid for http) Use persistent connections if set to 1, default is 0
				 */
				String final_cmd = "ffmpeg -i " + Constant.FRAMES_PATH +"frame%05d.png -r " + LiveMicroSession.frameRate + " -s 320x240 -c:v mpeg1video "
						+ "-f mpegts -rtbufsize 500000k -bf 0 -b:v 800k http://" + MyUrl.API_IP + ":8081"
						+ "/supersecret/" + Constant.MICRO_ID + "/320/240";
				
				String to_udp = "ffmpeg -i " + Constant.FRAMES_PATH +"frame%05d.png -r " + LiveMicroSession.frameRate + " -s 320x240 -c:v mpeg1video "
						+ "-f mpegts -rtbufsize 500000k -bf 0 -b:v 400k udp://127.0.0.1:4445";

				Process p = Runtime.getRuntime().exec(final_cmd);

				this.frameCounter.set(0);

				System.out.println("Command executed" + final_cmd);
			}

		} catch (IOException e) {
			System.out.println("Errore");
			logger.logError(e);
		}

	}

	private static BufferedImage toBufferedImage(Image src) {
		int type = BufferedImage.TYPE_INT_RGB;  // other options
		BufferedImage dest = new BufferedImage(src.getWidth(null), src.getHeight(null), type);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(src, 0, 0, null);
		return dest;
	}
}
