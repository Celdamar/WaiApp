package nz.co.android.cowseye2.service;

import java.io.IOException;
import java.util.List;

import nz.co.android.cowseye2.gps.GPSManager;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

public class ReverseGeoCodeCoordinatesService extends AsyncTask<Void, Void, String> {

	private Context context;
	private GPSManager gpsManager;
	private Geocoder geocoder;
	private String currentAddress;
	private LatLng newLatLng;
	private boolean noAlert;

	public ReverseGeoCodeCoordinatesService(Context context, GPSManager gpsManager, Geocoder geocoder, LatLng newLatLng, String currentAddress){
		this.context = context;
		this.gpsManager = gpsManager;
		this.geocoder = geocoder;
		this.currentAddress = currentAddress;
		this.newLatLng = newLatLng;
		this.noAlert = false;
	}

	public void setNoAlert(boolean alert) {
		noAlert = alert;
	}


	@Override
	protected String doInBackground(Void... Void) {
		try {
			List<Address> addresses = geocoder.getFromLocation(newLatLng.latitude, newLatLng.longitude, 1);
			if (addresses.isEmpty()) return null;
			String num = addresses.get(0).getFeatureName(); // .trim();
			String street = addresses.get(0).getThoroughfare(); //.trim();
			String subArea = addresses.get(0).getSubLocality(); //.getSubAdminArea();//.trim();
			String area = addresses.get(0).getLocality();
			String addr ="";
			if(num != null && !num.equals("")) {
				num = num.trim();
				addr+= num +=" ";
			}
			if(street != null && !street.equals("")) {
				street = street.trim();
				addr+=street+", ";
			}
			if(subArea != null && !subArea.equals("")) {
				subArea = subArea.trim();
				addr+=subArea + ", ";
			}
			if(area != null && !area.equals("")) {
				area = area.trim();
				if (! area.equals(subArea))
					addr += area;
			}
			return addr;

		} catch (IOException e) {
			Log.e(toString(), "Reverse Geocoding error: "+e);
		}
		return null;
	}


	@Override
	protected void onPostExecute(String addr) {
		if(addr==null){
			Log.e(toString(), "Error in reverse geo coding");
			gpsManager.requestBuildAlertMessageUpdatePosition(newLatLng);
		}
		else if (currentAddress == null || currentAddress.equals("") || noAlert ) {
			//gpsManager.updatePosition(newLatLng);
		} else if (!addr.equals("")) {
			if(!(addr.trim()).equals(currentAddress)){
				gpsManager.requestBuildAlertMessageUpdatePosition(newLatLng);
			}
		}
	}
}