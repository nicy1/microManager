package api;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;

import factory.ISessionFactory;
import models.CountedImage;
import models.RemovedImage;
import request.ImageUploadRequest;
import response.CountedResponse;
import response.RemovedResponse;
import response.SampleResponse;
import rx.Observable;
import rx.schedulers.Schedulers;
import utils.ILogger;
import utils.MyUrl;

public class SampleApi extends BaseApi implements ISampleApi {	
	private static final String TAG = "network.api.SampleApi";

	@Inject
	public SampleApi(ILogger logger, ISessionFactory sessionFactory) {
		super(logger, sessionFactory);
	}

	public Observable<byte[]> getOriginalImage(int imageId, String userCode) {

		logger.logInformation(String.format("%s Starting method", TAG));
		URL url;
		try {
			url = new URL(String.format("%s/originalImage/%s/%s", MyUrl.SAMPLE_API, imageId, userCode));
			return this.getImage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public Observable<byte[]> getRealTimeImage(String microId, String userId, boolean elaboration) {

		logger.logInformation(String.format("%s Starting method", TAG));
		URL url;
		try {
			if(elaboration)
				url = new URL(String.format("%s/realTimeImage/%s/%s?elab=1", MyUrl.SAMPLE_API, microId, userId));
			else
				url = new URL(String.format("%s/realTimeImage/%s/%s?elab=0", MyUrl.SAMPLE_API, microId, userId));
			return this.getImage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Observable<byte[]> getCompressedImage(int imageId, String userCode) {

		logger.logInformation(String.format("%s Starting method", TAG));
		URL url;
		try {
			url = new URL(String.format("%s/compressedImage/%s/%s", MyUrl.SAMPLE_API, imageId, userCode));
			return this.getImage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public Observable<byte[]> zoomInImage(String userCode, double factor) {

		logger.logInformation(String.format("%s Starting method", TAG));
		URL url;
		try {
			url = new URL(String.format("%s/in/%s/%s", MyUrl.OPERATION_API, userCode, factor));
			return this.getImage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public Observable<byte[]> moveLeftImage(String userCode, int diff) {

		logger.logInformation(String.format("%s Starting method", TAG));
		URL url;
		try {
			url = new URL(String.format("%s/left/%s/%s", MyUrl.OPERATION_API, userCode, diff));
			return this.getImage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public Observable<RemovedImage> removeColorImage(int imageId, String userCode, int red, int green, int blue) {

		logger.logInformation(String.format("%s Starting method", TAG));
		URL url;
		try {
			url = new URL(String.format("%s/%s/remove/%s/%s/%s/%s", 
					MyUrl.OPERATION_API, imageId, userCode, red, green, blue));
			return this.getRemovedImage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Observable<RemovedImage> removeCmpressedImage(int imageId, String userCode, int red, int green, int blue) {

		logger.logInformation(String.format("%s Starting method", TAG));
		URL url;
		try {
			url = new URL(String.format("%s/%s/remove/compressed/%s/%s/%s/%s", 
					MyUrl.OPERATION_API, imageId, userCode, red, green, blue));
			return this.getRemovedImage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	public Observable<CountedImage> countNuclei(int imageId, String userCode) {

		logger.logInformation(String.format("%s Starting method", TAG));
		URL url;
		try {
			url = new URL(String.format("%s/%s/nuclei/%s", 
					MyUrl.OPERATION_API, imageId, userCode));
			return this.getCountedImage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	//if some errors it returns null
	private Observable<byte[]> getImage(URL url){
		return Observable.create((Observable.OnSubscribe<byte[]>) subscriber -> {
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			HttpsURLConnection connection = null;
			long timeBefore = 0;
			try {
				// open connection
				connection = getConnection(url);
				connection.setRequestProperty(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(sessionFactory.getToken()));


				// set the connection timeout to 5000 milliseconds and the read timeout to 110000 milliseconds
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(110000);
				timeBefore = System.currentTimeMillis();		

				int result = connection.getResponseCode();

				if (result == HttpsURLConnection.HTTP_OK) {
					logger.logInformation(String.format("%s.sample - Sample request returned HTTP OK", TAG));

					InputStream in = new BufferedInputStream(connection.getInputStream());
					int i=0;
					while ((i = in.read()) != -1) out.write(i); 

					subscriber.onNext(out.toByteArray());
				} else {
					logger.logError(String.format("%s.sample - Sample request returned KO (%s) : %s", TAG, result, connection.getResponseMessage()));
					subscriber.onNext(null);
				}

				connection.disconnect();
				out.close();
				subscriber.onCompleted();

			} catch (java.net.SocketTimeoutException e) {
				long timeAfter = System.currentTimeMillis();		
				System.out.println("Timeout : " + (timeAfter-timeBefore));
				subscriber.onNext(null);
				connection.disconnect();
				try {
					out.close();
				} catch (IOException e1) {
					e.printStackTrace();
					logger.logError(e);
				}
				subscriber.onCompleted();
				e.printStackTrace();
				logger.logError(e);
			} catch (IOException e) {
				e.printStackTrace();
				logger.logError(e);
			}


		}).subscribeOn(Schedulers.io());
	}

	private Observable<RemovedImage> getRemovedImage(URL url){
		return Observable.create((Observable.OnSubscribe<RemovedImage>) subscriber -> {

			RemovedResponse response = new RemovedResponse();
			try {
				Gson gson = new Gson();

				// open connection
				HttpsURLConnection connection = getConnection(url);
				connection.setRequestProperty(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(sessionFactory.getToken()));
				ByteArrayOutputStream out=new ByteArrayOutputStream();

				int result = connection.getResponseCode();

				if (result == HttpsURLConnection.HTTP_OK) {					
					logger.logInformation(String.format("%s.login - Login request returned HTTP OK", TAG));
					StringBuilder sb = getResult(connection.getInputStream());

					response = gson.fromJson(sb.toString(), RemovedResponse.class);
					logger.logInformation("Tot pixel in sample API :" + response.data.totPixel);
					if(response.success)
						subscriber.onNext(response.data);
					else
						subscriber.onNext(null);
				} else {
					logger.logError(String.format("%s.sample - Sample request returned KO (%s) : %s", TAG, result, connection.getResponseMessage()));
					subscriber.onNext(null);
				}

				connection.disconnect();
				out.close();
				subscriber.onCompleted();


			} catch (IOException e) {
				e.printStackTrace();
				logger.logError(e);
			}


		}).subscribeOn(Schedulers.io());

	}
	
	private Observable<CountedImage> getCountedImage(URL url){
		return Observable.create((Observable.OnSubscribe<CountedImage>) subscriber -> {

			CountedResponse response = new CountedResponse();
			try {
				Gson gson = new Gson();

				// open connection
				HttpsURLConnection connection = getConnection(url);
				connection.setRequestProperty(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(sessionFactory.getToken()));
				ByteArrayOutputStream out=new ByteArrayOutputStream();

				int result = connection.getResponseCode();

				if (result == HttpsURLConnection.HTTP_OK) {					
					logger.logInformation(String.format("%s.login - Login request returned HTTP OK", TAG));
					StringBuilder sb = getResult(connection.getInputStream());

					response = gson.fromJson(sb.toString(), CountedResponse.class);
					logger.logInformation("Tot pixel in sample API :" + response.data.getTotNuclei());
					if(response.success)
						subscriber.onNext(response.data);
					else
						subscriber.onNext(null);
				} else {
					logger.logError(String.format("%s.sample - Sample request returned KO (%s) : %s", TAG, result, connection.getResponseMessage()));
					subscriber.onNext(null);
				}

				connection.disconnect();
				out.close();
				subscriber.onCompleted();


			} catch (IOException e) {
				e.printStackTrace();
				logger.logError(e);
			}


		}).subscribeOn(Schedulers.io());

	}

	@Override
	public Observable<SampleResponse> uploadImage(ImageUploadRequest request) {
		return Observable.create((Observable.OnSubscribe<SampleResponse>) subscriber -> {

			SampleResponse response = new SampleResponse();

			try {
				Gson gson = new Gson();

				// open connection
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpPost uploadFile = new HttpPost(String.format("%s/%s", MyUrl.SAMPLE_API, request.fkLiveSession));
				uploadFile.setHeader(HttpHeaders.AUTHORIZATION, getAuthenticationHeader(sessionFactory.getToken()));
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();

				// add image to post body
				builder.addBinaryBody(
						"sample",
						new ByteArrayInputStream(request.data),
						ContentType.APPLICATION_OCTET_STREAM,
						String.format("%d_%s", request.userid, request.filename)
						);

				HttpEntity multipart = builder.build();
				uploadFile.setEntity(multipart);
				CloseableHttpResponse httpResponse = httpClient.execute(uploadFile);
				HttpEntity responseEntity = httpResponse.getEntity();


				// check if request was successful
				if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
					StringBuilder sb = getResult(responseEntity.getContent());

					response = gson.fromJson(sb.toString(), SampleResponse.class);
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
