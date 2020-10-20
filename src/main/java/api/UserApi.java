package api;

import com.google.gson.Gson;
import factory.ISessionFactory;
import models.User;
import response.IntegerResponse;
import rx.Observable;
import rx.schedulers.Schedulers;
import utils.ILogger;
import utils.MyUrl;

import javax.inject.Inject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Princewill Princewill Okoriee on 30-Aug-17.
 */
public class UserApi extends BaseApi implements IUserApi {

    private static final String TAG = "network.api.UserApi";

    @Inject
    public UserApi(ILogger logger, ISessionFactory sessionFactory) {
        super(logger, sessionFactory);
        System.setProperty("javax.net.ssl.trustStore", "cacerts");
	    System.setProperty("javax.net.ssl.trustStorePassword", "changeIt");
    }

    @Override
    public Observable<IntegerResponse> addUser(User user) {
        return Observable.create((Observable.OnSubscribe<IntegerResponse>) subscriber -> {

            logger.logInformation(String.format("%s.addUser: Inserting new user", TAG));
            IntegerResponse response = new IntegerResponse();

            try {
                Gson gson = new Gson();

                URL url = new URL(String.format("%s/desktop", MyUrl.USER_API));
                HttpURLConnection connection = getConnection(url);

                String data = gson.toJson(user);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(data);
                wr.flush();

                int result = connection.getResponseCode();

                if (result == HttpURLConnection.HTTP_OK) {
                    logger.logInformation(String.format("%s.addUser - Insert new user HTTP OK", TAG));
                    GetIntegerResponse(subscriber, gson, connection, wr);
                } else {
                    logger.logError(String.format("%s.addUser - Insert new user returned KO (%s) : %s", TAG, result, connection.getResponseMessage()));
                    response.markAsError(connection.getResponseMessage());
                    subscriber.onNext(response);
                }

                subscriber.onCompleted();
            } catch (IOException e) {
            	e.printStackTrace();
                logger.logError(e);
            }


        }).subscribeOn(Schedulers.io());
    }
}
