package nz.co.android.cowseye2.service;

import java.util.ArrayList;
import java.util.List;








import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.activity.MainScreenActivity;
import nz.co.android.cowseye2.common.Constants;
import nz.co.android.cowseye2.database.Incident;
import nz.co.android.cowseye2.database.IncidentBuilder;
import nz.co.android.cowseye2.event.GetIncidentsEvent;
import nz.co.android.cowseye2.utility.JSONHelper;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetIncidentsAsyncTask extends AsyncTask<Void, Void, Boolean> {

	private MainScreenActivity mainScreen;
	private final GetIncidentsEvent getIncidentEvent;
	private final RiverWatchApplication myApplication;

	public GetIncidentsAsyncTask(MainScreenActivity mainScreen, GetIncidentsEvent getIncidentEvent, RiverWatchApplication myApplication){
		this.mainScreen = mainScreen;
		this.getIncidentEvent = getIncidentEvent;
		this.myApplication = myApplication;

	}

	public GetIncidentsAsyncTask(GetIncidentsEvent getIncidentsEvent, RiverWatchApplication myApplication) {
		this.getIncidentEvent = getIncidentsEvent;
		this.myApplication = myApplication;
	}

	@Override
	protected Boolean doInBackground(Void... Void) {
		HttpResponse response = getIncidentEvent.processRaw();
		JSONArray jsonArray = null;
		if(RiverWatchApplication.processEventResponse(response)){		
			try{
				JSONObject jsonObject = JSONHelper.parseHttpResponseAsJSON(response);
				if(jsonObject.has(Constants.JSON_INCIDENTS_KEY))
					jsonArray = jsonObject.getJSONArray(Constants.JSON_INCIDENTS_KEY);
				// convert json to incident array
				List<Incident> incidents = getIncidentsFromJSON(jsonArray);
				// insert incident into database
				for(Incident incident : incidents)
					myApplication.getDatabaseAdapter().insertIncident(incident);
			}
			catch(Exception f){ 
				Log.e(toString(), "Exception in JsonParsing : "+f);
			}
			return true;
		}
		return false;
	}

	private List<Incident> getIncidentsFromJSON(JSONArray data) {
		List<Incident> incidents = new ArrayList<Incident>();
		for(int i = 0; i < data.length(); i++ ){
			try {
				JSONObject incident = data.getJSONObject(i);
				if(incident!=null){
					try{
						incidents.add(getIncidentFromJSONObject(incident));
					}catch(JSONException e){
						Log.e(toString(), "Failed to convert JSON object to an incident : "+incident);
					}
				}
			} catch (JSONException e) {
				Log.e(toString(), "No incident found in JSONObject");
			}
		}
		return incidents;
	}

	private Incident getIncidentFromJSONObject(JSONObject incident) throws JSONException {
		IncidentBuilder builder = new IncidentBuilder();
		builder.setId(incident.getInt(Constants.JSON_INCIDENT_ID_KEY))
		.setDescription(incident.getString(Constants.JSON_INCIDENT_IMAGE_DESCRIPTION_KEY))
		.setImageUrl(incident.getString(Constants.JSON_INCIDENT_IMAGE_URL_KEY))
		.setThumbnailUrl(incident.getString(Constants.JSON_INCIDENT_THUMBNAIL_URL_KEY))
		.setPhysicalLocation(incident.getString(Constants.JSON_INCIDENT_PHYSICAL_LOCATION_KEY));
		if(incident.has(Constants.JSON_INCIDENT_GEOLOCATION_KEY)){
			JSONObject geoJSON = incident.getJSONObject(Constants.JSON_INCIDENT_GEOLOCATION_KEY);
			if(!geoJSON.isNull(Constants.JSON_INCIDENT_LATITUDE_KEY))
				builder.setLatitude(geoJSON.getInt(Constants.JSON_INCIDENT_LATITUDE_KEY));
			if(!geoJSON.isNull(Constants.JSON_INCIDENT_LONGITUDE_KEY))
				builder.setLongitude(geoJSON.getInt(Constants.JSON_INCIDENT_LONGITUDE_KEY));
		}
		return builder.build();
	}


	/** Does not do anything as nothing needs to be done upon ending*/
	@Override
	protected void onPostExecute(Boolean value) {
		if(mainScreen!=null)
			mainScreen.endGetIncidentsServiceCall(value);

	}
}