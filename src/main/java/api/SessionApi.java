package api;

import com.google.gson.Gson;
import factory.ISessionFactory;
import request.SessionRequest;
import response.SessionResponse;
import rx.Observable;
import rx.schedulers.Schedulers;
import utils.ILogger;
import utils.MyUrl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SessionApi extends BaseApi implements ISessionApi {
    private static final String TAG = "network.api.SessionApi";

    @Inject
    public SessionApi(ILogger logger , ISessionFactory sessionFactory) {
        super(logger, sessionFactory);
        System.setProperty("javax.net.ssl.trustStore", "cacerts");
	    System.setProperty("javax.net.ssl.trustStorePassword", "changeIt");
    }

    @Override
    public Observable<SessionResponse> logIn(SessionRequest request) {
        return Observable.create((Observable.OnSubscribe<SessionResponse>) subscriber -> {

            logger.logInformation(String.format("%s.login: Sending login request", TAG));
            SessionResponse response = new SessionResponse();

            try {
                Gson gson = new Gson();
                
                URL url = new URL(String.format("%s/desktop", MyUrl.SESSION_API));
                
                HttpsURLConnection connection = getConnection(url);
                String data = gson.toJson(request.data);
                
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(data);
                wr.flush();
                
                int result = connection.getResponseCode();
                
                if (result == HttpsURLConnection.HTTP_OK) {
                    logger.logInformation(String.format("%s.login - Login request returned HTTP OK", TAG));
                    StringBuilder sb = getResult(connection.getInputStream());
                    //wr.close();
                    connection.disconnect();

                    response = gson.fromJson(sb.toString(), SessionResponse.class);
                    subscriber.onNext(response);
                } else {
                    logger.logError(String.format("%s.login - Login request returned KO (%s) : %s", TAG, result, connection.getResponseMessage()));
                    response.markAsError(connection.getResponseMessage());
                    subscriber.onNext(response);
                }

                subscriber.onCompleted();
            } catch (IOException e) {
                logger.logError(e);
            }


        }).subscribeOn(Schedulers.io());
    }
    
    @Override
    public Observable<Void> logOut() {
        return Observable.create((Observable.OnSubscribe<Void>) subscriber -> {

            try {
                // Open connection
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpDelete httpDelete = new HttpDelete(String.format("%s", MyUrl.SESSION_API));
                httpDelete.setHeader(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(sessionFactory.getToken()));


                CloseableHttpResponse httpResponse = httpClient.execute(httpDelete);

                // check if request was successful
                if (httpResponse.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                    logger.logError(String.format("Unable to log out: %s", httpResponse.getStatusLine().getReasonPhrase()));
                }

                subscriber.onCompleted();
            } catch (IOException e) {
                logger.logError(e);
            }


        }).subscribeOn(Schedulers.io());

    }

    @Override
    public Observable<SessionResponse> extend(String refreshToken) {
        return Observable.create((Observable.OnSubscribe<SessionResponse>) subscriber -> {

            SessionResponse response = new SessionResponse();

            try {
                Gson gson = new Gson();

                // open connection
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet get = new HttpGet(String.format("%s/extend/%s", MyUrl.SESSION_API, refreshToken));
                get.setHeader(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(sessionFactory.getToken()));

                CloseableHttpResponse httpResponse = httpClient.execute(get);
                HttpEntity responseEntity = httpResponse.getEntity();


                // check if request was successful
                if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder sb = getResult(responseEntity.getContent());

                    response = gson.fromJson(sb.toString(), SessionResponse.class);
                    subscriber.onNext(response);
                } else {
                    response.markAsError(httpResponse.getStatusLine().getReasonPhrase());
                    subscriber.onNext(response);
                }

                subscriber.onCompleted();
            } catch (IOException e) {
                logger.logError(e);
            }


        }).subscribeOn(Schedulers.io());
    }
}