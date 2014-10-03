package nz.co.android.cowseye2.event;

import java.io.IOException;




import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.common.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.util.Log;



public class GetIncidentsEvent{

	protected HttpGet httpGet;
	protected HttpClient client;

	private final RiverWatchApplication myApplication;
	private final int startOfIncident;
	private final int numberOfIncidents;

	public GetIncidentsEvent(RiverWatchApplication myApplication, int startOfIncident, int numberOfIncidents){
		this.myApplication = myApplication;
		this.startOfIncident = startOfIncident;
		this.numberOfIncidents = numberOfIncidents;
		client = constructHttpClient();
		httpGet = constructHttpGet();
	}

	/** Constructs a HttpClient */
	public HttpClient constructHttpClient(){
		HttpClient client = new DefaultHttpClient();
		//set timeout to 20 seconds
		HttpConnectionParams.setConnectionTimeout(client.getParams(), Constants.CONNECTION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(client.getParams(), 25000);
		return client;
	}

	/** Processes the event and returns the response of the event */
	public HttpResponse processRaw() {
		//add the created method body to the post request
		HttpResponse response = null;
		try {
			response = client.execute(httpGet);
		} 
		catch (HttpResponseException e) {
			Log.e(toString(), "HttpResponseException : "+e);
		} catch (ClientProtocolException e) {
			Log.e(toString(), "ClientProtocolException : "+e);
		} catch (IOException e) {
			Log.e(toString(), "IOException : "+e);
		}
		if(response ==null)
			Log.e(toString(), "response is null: ");
		return response;
	}

	/** construct path to web service */
	public HttpGet constructHttpGet(){
		String url = RiverWatchApplication.get_incidents_path + 
				RiverWatchApplication.get_incidents_path_start + startOfIncident  + 
				RiverWatchApplication.get_incidents_path_number + numberOfIncidents;
		Log.d(toString(), "url : "+url);
		return new HttpGet(url);
	}
}