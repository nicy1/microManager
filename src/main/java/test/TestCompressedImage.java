package test;

import java.io.FileOutputStream;
import java.io.IOException;

import api.ISessionApi;
import api.SampleApi;
import api.SessionApi;
import factory.ISessionFactory;
import models.User;
import request.LoginRequest;
import request.SessionRequest;
import response.SessionResponse;
import rx.Observable;
import utils.Constant;
import utils.ILogger;
import utils.MyUtil;
import utils.Singleton;

public class TestCompressedImage {
	private static ILogger logger;

	public static void main(String[] args) {
		logger = Singleton.logger();
		ISessionFactory sessionFactory = Singleton.getSessionFactory();
		ISessionApi sessionApi = new SessionApi(Singleton.logger(), sessionFactory);
		SessionRequest request = new SessionRequest();
		SampleApi sampleApi = new SampleApi(Singleton.logger(), sessionFactory);

		request.data = new LoginRequest();
		request.data.username = "test";
		request.data.password = "test";
		request.data.duration = Constant.SESSION_DURATION;
		
		long timeBeforeLog = System.currentTimeMillis();

		Observable<SessionResponse> ses =  sessionApi.logIn(request).map(response -> {
			if (response.success) sessionFactory.updateSession(response.data);
			return response;
		});

		ses.subscribe((sessionResponse) -> {
			// Run on main thread
			if (sessionResponse.success) {
				long timeAfterLog = System.currentTimeMillis();
				logger.logInformation("elapsed Login:" + (timeAfterLog - timeBeforeLog));


				logger.logInformation("Login");
				logger.logInformation("User Id" + sessionResponse.data.user.code);

				long timeBefore = System.currentTimeMillis();
				Observable<byte[]> img = sampleApi.getCompressedImage(3, sessionResponse.data.user.code)
						.map(response -> {return response;});

				logger.logInformation("after send request");

				img.subscribe((image) -> {
					logger.logInformation("received something");
					// Run on main thread
					if (image != null) {

						long timeAfter = System.currentTimeMillis();
						long elapsed = timeAfter - timeBefore;
						logger.logInformation("elapsed:" + elapsed);

						logger.logInformation("received image");
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream("images/testImage.png");
							fos.write(image);
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
