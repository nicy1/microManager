package api;

import com.google.gson.Gson;
import factory.ISessionFactory;
import models.LiveSession;
import request.BaseRequest;
import response.LiveMicroSessionResponse;
import response.LiveSessionHistoryResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
 * Created by Princewill Princewill Okoriee on 10-Oct-17.
 */
public class LiveMicroSessionApi extends BaseApi implements ILiveMicroSessionApi {

    @Inject
    public LiveMicroSessionApi(ILogger logger, ISessionFactory sessionFactory) {
        super(logger, sessionFactory);
    }

    @Override
    public Observable<LiveMicroSessionResponse> createSession(LiveSession session) {
        return Observable.create((Observable.OnSubscribe<LiveMicroSessionResponse>) subscriber -> {

            LiveMicroSessionResponse response = new LiveMicroSessionResponse();

            try {
                Gson gson = new Gson();

                URL url = new URL(String.format("%s", MyUrl.LIVESESSION_API));
                HttpURLConnection connection = getConnection(url);
                connection.setRequestProperty(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(sessionFactory.getToken()));

                String data = gson.toJson(session);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(data);
                wr.flush();

                int result = connection.getResponseCode();

                if (result == HttpURLConnection.HTTP_OK) {
                    StringBuilder sb = getResult(connection.getInputStream());
                    wr.close();
                    connection.disconnect();

                    response = gson.fromJson(sb.toString(), LiveMicroSessionResponse.class);
                    subscriber.onNext(response);
                } else {
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
    public Observable<LiveSessionHistoryResponse> getHistory() {
        return Observable.create((Observable.OnSubscribe<LiveSessionHistoryResponse>) subscriber -> {

            LiveSessionHistoryResponse response = new LiveSessionHistoryResponse();

            try {
                Gson gson = new Gson();

                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet http = new HttpGet(String.format("%s/user", MyUrl.LIVESESSION_API));
                http.setHeader(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(sessionFactory.getToken()));

                CloseableHttpResponse httpResponse = httpClient.execute(http);
                HttpEntity responseEntity = httpResponse.getEntity();


                // check if request was successful
                if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder sb = getResult(responseEntity.getContent());

                    response = gson.fromJson(sb.toString(), LiveSessionHistoryResponse.class);
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
