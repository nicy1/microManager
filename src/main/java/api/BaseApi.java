package api;

import com.google.gson.Gson;
import factory.ISessionFactory;
import response.IntegerResponse;
import rx.Subscriber;
import utils.ILogger;
import utils.MyUrl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by Princewill Princewill Okoriee on 25-Aug-17.
 */
public class BaseApi {
    protected final ILogger logger;
    protected final ISessionFactory sessionFactory;

    public BaseApi(ILogger logger, ISessionFactory sessionFactory) {
        this.logger = logger;
        this.sessionFactory = sessionFactory;
    }


    protected HttpsURLConnection getConnection(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setRequestProperty("charset", "utf-8");
        connection.setHostnameVerifier(new HostnameVerifier()
        {      
            public boolean verify(String hostname, SSLSession session)
            {
            	 if(hostname.equals(MyUrl.API_IP))
            	 	return true;
            	 else
            	 	return false;
            }
        });
        //connection.connect();
        return connection;
    }

    protected StringBuilder getResult(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        reader.close();
        return sb;
    }

    protected void GetIntegerResponse(Subscriber<? super IntegerResponse> subscriber,
                                      Gson gson, HttpURLConnection connection,
                                      DataOutputStream wr) throws IOException {
        IntegerResponse response;
        StringBuilder sb = getResult(connection.getInputStream());
        wr.close();
        connection.disconnect();

        response = gson.fromJson(sb.toString(), IntegerResponse.class);
        subscriber.onNext(response);
    }

    protected String getAuthenticationHeader(String token){
        return String.format("Bearer %s", token);
    }
}
