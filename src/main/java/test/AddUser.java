package test;

import java.io.FileOutputStream;
import java.io.IOException;

import api.ISessionApi;
import api.IUserApi;
import api.SampleApi;
import api.SessionApi;
import api.UserApi;
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

public class AddUser {
	private static ILogger logger;

	public static void main(String[] args) {
		logger = Singleton.logger();
		ISessionFactory sessionFactory = Singleton.getSessionFactory();
		IUserApi userApi = new UserApi(Singleton.logger(), sessionFactory);

		User user = new User();
		user.email = "test1";
		user.password = "test1";
		user.firstname = "Marco";
		user.lastname = "Rossi";
		user.middlename = "";

		user.email = MyUtil.encode(user.email);
		user.password = MyUtil.encode(user.password);
		userApi.addUser(user).subscribe((response) -> {
			System.out.println("Ricevuto qlcs");
			if (response.success) {
				logger.logInformation("Inserted user");
			} else {
				logger.logError(response.error.message);
			}             

		}, error -> {
			logger.logError(error.getMessage());
		});

	}
}