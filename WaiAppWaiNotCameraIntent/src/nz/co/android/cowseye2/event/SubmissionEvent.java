package nz.co.android.cowseye2.event;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;





import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.common.Constants;
import nz.co.android.cowseye2.utility.JSONHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.net.Uri;
import android.util.Log;



/**
 * Models a standard event to send to the web server
 */
public class SubmissionEvent implements Event{

	protected HttpPost httpPost;
	protected HttpClient client;

	protected Uri imageToPath; /* Stores the path to the image on local storage */
	protected boolean fromGallery;
	protected String imageDescription;
	protected List<String> imageTag;
	protected String address;
	protected LatLng geoCoordinates;


	//    protected final String password;
	//    protected final String loginCode;
	private int failCount = 0;
	protected String timeStamp;
	private final RiverWatchApplication myApplication;

	//    public StandardEvent(String loginCode, String password, boolean authorize){
	//        this.loginCode = loginCode;
	//        this.password = password;
	//        //create new HttpClient
	//        client = constructHttpClient();
	//        //Create and authorize HttpPost
	//        if(authorize)
	//            httpPost = setAuthorization(constructHttpPost());
	//        else
	//            httpPost = constructHttpPost();
	//    }

	public SubmissionEvent(RiverWatchApplication myApplication){
		this.myApplication = myApplication;
		client = constructHttpClient();
		httpPost = constructHttpPost();
	}

	/** Constructs a HttpClient */
	@Override
	public HttpClient constructHttpClient(){
		HttpClient client = new DefaultHttpClient();
		//set timeout to 20 seconds
		HttpConnectionParams.setConnectionTimeout(client.getParams(), Constants.CONNECTION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(client.getParams(), Constants.SOCKET_TIMEOUT_MS);
		return client;
	}

	//    /**  Authorizes the HTTP Post method */
	//    public HttpPost setAuthorization(HttpPost httpPost) {
	//        String auth =  loginCode+":"+password;
	//        httpPost.addHeader("Authorization", "Basic " + Base64Coder.encodeString(auth).toString());
	//        return httpPost;
	//    }


	/** Processes the event and returns the response of the event */
	@Override
	public HttpResponse processRaw() {
		//add the created method body to the post request
		httpPost.setEntity(makeEntity());
		HttpResponse response = null;
		try {
			response = client.execute(httpPost);
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

	/** Processes the event and returns true if successfull, otherwise false */
	@Override
	public boolean processForSuccess() {
		//add the created method body to the post request
		httpPost.setEntity(makeEntity());
		HttpResponse response = null;
		try {
			response = client.execute(httpPost);
		}
		catch (HttpResponseException e) {
			Log.e(toString(), "HttpResponseException : "+e);
		} catch (ClientProtocolException e) {
			Log.e(toString(), "ClientProtocolException : "+e);
		} catch (IOException e) {
			Log.e(toString(), "IOException : "+e);
		}
		if(response ==null){
			Log.e(toString(), "response is null: ");
			return false;
		}
		try{
			JSONObject jsonObject = JSONHelper.parseHttpResponseAsJSON(response);
			//if (jsonObject.has(Utils.RESPONSE_CODE))
			//               return ResponseCodeState.stringToResponseCode((String)jsonObject.getString(Utils.RESPONSE_CODE))==ResponseCodeState.SUCCESS;

			return true;
		}
		catch(Exception e){
			Log.e(toString(), "Exception in JsonParsing : "+e);
		}
		return false;
	}

	@Override
	public void incrementFailCount(){
		failCount++;
	}

	@Override
	public int getFailCount(){
		return failCount;
	}
	@Override
	public Uri getImagePath(){
		return imageToPath;
	}

	@Override
	public String getImageDescription () {
		return imageDescription;
	}

	@Override
	public List<String> getImageTag () {
		return imageTag;
	}

	@Override
	public String getTimeStamp(){
		return timeStamp;
	}


	@Override
	public HttpPost setAuthorization(HttpPost httpPost) {
		// TODO Auto-generated method stub
		return null;
	}

	/** construct path to web service */
	@Override
	public HttpPost constructHttpPost(){
		return new HttpPost(RiverWatchApplication.submission_path);
	}


	@Override
	public MultipartEntity makeEntity() {
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		JSONObject jsonObject;
		try {
			//convert data to JSON
			jsonObject = makeJSONFromSubmissionData();
			Log.i(toString(), "Incident as JSON: "+jsonObject.toString());
		}
		catch (JSONException e) {
			Log.e(toString(), "JSONException: "+e);
			return null;
		}
		try{
			//add image data
			String imagePath = imageToPath.toString();
			if(fromGallery)
				imagePath = myApplication.getRealPathFromURI(imageToPath);

			reqEntity.addPart(Constants.FORM_POST_IMAGE, new FileBody(new File(imagePath)));
			//add Data in JSON format
			reqEntity.addPart(Constants.FORM_POST_DATA, new StringBody(jsonObject.toString()));
		}catch (UnsupportedEncodingException e1) {
			Log.e(toString(), "UnsupportedEncodingException : "+e1);
		}
		return reqEntity;
	}

	/* Returns a JSON object representing the submission data */
	private JSONObject makeJSONFromSubmissionData() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObjectGeoCoordinates = null;
		JSONArray jsonObjectTags = new JSONArray();
		//try and put geo coordinates in
		if(geoCoordinates!=null){
			jsonObjectGeoCoordinates = new JSONObject();
			jsonObjectGeoCoordinates.put(Constants.SUBMISSION_JSON_GEO_LAT, geoCoordinates.latitude);
			jsonObjectGeoCoordinates.put(Constants.SUBMISSION_JSON_GEO_LON, geoCoordinates.longitude);
			jsonObject.put(Constants.SUBMISSION_JSON_GEO_LOCATION, jsonObjectGeoCoordinates);
		}
		//otherwise put in address
		else{
			jsonObject.put(Constants.SUBMISSION_JSON_ADDRESS, address);
		}

		jsonObject.put(Constants.SUBMISSION_JSON_DESCRIPTION, imageDescription);
		jsonObjectTags.put(imageTag);
		jsonObject.put(Constants.SUBMISSION_JSON_TAGS, jsonObjectTags);
		return jsonObject;
	}

	public void setImagePath(Uri uriToImage) {
		imageToPath = uriToImage;
	}

	public void setImageDescription (String description) {
		imageDescription = description;
	}

	public void setImageTag (List<String> tag) {
		imageTag=tag;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LatLng getGeoCoordinates() {
		return geoCoordinates;
	}

	public void setGeoCoordinates(LatLng geoCoordinates) {
		this.geoCoordinates = geoCoordinates;
	}

	public boolean isFromGallery() {
		return fromGallery;
	}

	public void setFromGallery(boolean fromGallery) {
		this.fromGallery = fromGallery;
	}



}