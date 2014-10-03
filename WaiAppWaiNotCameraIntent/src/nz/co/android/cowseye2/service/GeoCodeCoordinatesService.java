package nz.co.android.cowseye2.service;

import java.io.IOException;
import java.util.List;

import nz.co.android.cowseye2.activity.RecordLocationActivity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

public class GeoCodeCoordinatesService extends AsyncTask<Void, Void, Address> {

	private RecordLocationActivity locationActivity;
	private Geocoder geocoder;
	private String address;

	public GeoCodeCoordinatesService(RecordLocationActivity locationActivity, Geocoder geocoder, String address){
		this.locationActivity = locationActivity;
		this.geocoder = geocoder;
		this.address = address;
	}
	

	@Override
	protected Address doInBackground(Void... Void) {
		try{
			List<Address> addresses = geocoder.getFromLocationName(address,1);
			if (addresses == null) {
				Log.e(toString(), "No lat,long found from addr :"+address);
				return null;
			}
			Address location = addresses.get(0);
			location.getLatitude();
			location.getLongitude();
			return location;

		} catch (IOException e) {
			Log.e(toString(), "Geocoding error: "+e);
		}
		return null;
	}

	/** Does not do anything as nothing needs to be done upon ending*/
	@Override
	protected void onPostExecute(Address location) {
		if(location==null)
			locationActivity.errorGeoCodeAddress();
//		else
//			locationActivity.buildLocationDataIntent(locationActivity.RESULT_OK);//, location);
		
	}
}