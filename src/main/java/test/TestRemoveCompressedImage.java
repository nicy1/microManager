package test;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import api.ISessionApi;
import api.SampleApi;
import api.SessionApi;
import factory.ISessionFactory;
import models.User;
import models.RemovedImage;
import request.LoginRequest;
import request.SessionRequest;
import response.SessionResponse;
import rx.Observable;
import utils.Constant;
import utils.ILogger;
import utils.MyUtil;
import utils.Singleton;

public class TestRemoveCompressedImage {
	private static ILogger logger;

	public static void main(String[] args) {
		logger = Singleton.logger();
		ISessionFactory sessionFactory = Singleton.getSessionFactory();
		ISessionApi sessionApi = new SessionApi(Singleton.logger(), sessionFactory);
		SessionRequest request = new SessionRequest();
		SampleApi sampleApi = new SampleApi(Singleton.logger(), sessionFactory);

		request.data = new LoginRequest();
		request.data.username = MyUtil.encode("test");
		request.data.password = MyUtil.encode("test");
		request.data.duration = Constant.SESSION_DURATION;

		Observable<SessionResponse> ses =  sessionApi.logIn(request).map(response -> {
			if (response.success) sessionFactory.updateSession(response.data);
			return response;
		});

		logger.logInformation("after login");

		ses.subscribe((sessionResponse) -> {
			// Run on main thread
			if (sessionResponse.success) {

				logger.logInformation("Login");
				logger.logInformation("User Id" + sessionResponse.data.user.id);
				
				long timeBefore = System.currentTimeMillis();
				Observable<RemovedImage> img = sampleApi.removeCmpressedImage(6, sessionResponse.data.user.code,
													0 , 0 , 0 )
						.map(response -> {return response;});

				logger.logInformation("after send request");
				
				img.subscribe((image) -> {
					logger.logInformation("received something");
					// Run on main thread
					if (image != null) {

						long timeAfter = System.currentTimeMillis();
					    long elapsed = timeAfter - timeBefore;
					    logger.logInformation("elapsed:" + elapsed);
					    byte[] imageArray = Base64.decodeBase64(image.image);
					    logger.logInformation("Size:" + imageArray.length);
					    logger.logInformation("Tot pixel:" + image.totPixel);
					    logger.logInformation("Colored pixel:" + image.coloredPixel);
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream("images/testImage.png");
							fos.write(imageArray);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						}
						finally {
							try {
								fos.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}


					} else {
						logger.logError("Error during download image");
					}

				}, error -> {
					logger.logInformation("error");
					logger.logError(error.getMessage());
				});
			} else {
				logger.logError(sessionResponse.error.message);
			}

		}, error -> {
			error.printStackTrace();
			logger.logError(error.getMessage());
		});


		/*CMMCore core = new CMMCore();
        try {
            core.loadDevice("Camera", "DemoCamera", "DCam");
            core.initializeDevice("Camera");
            core.setCameraDevice("Camera");

            Singleton.cmmCore = core;
            logger = Singleton.logger();

            new JFXPanel();

            Platform.runLater(() -> {
                try {
                    IMicroscope microscope = Microscope.newInstance(core, logger);
                    LiveMicroUI ui = new LiveMicroUI(new LiveSessionFactory(microscope), Singleton.getApi(),
                            Singleton.logger(), Singleton.getSessionFactory());
                    //start the server
                    Server server = new Server(new Network(microscope, logger), logger);
                    server.start();

                    ui.start(new Stage());
                } catch (Exception e) {
                    logger.logError(e);
                }
            });

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            System.exit(1);
        }*/

	}
}
