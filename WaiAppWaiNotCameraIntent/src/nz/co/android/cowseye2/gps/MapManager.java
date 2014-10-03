package nz.co.android.cowseye2.gps;

import nz.co.android.cowseye2.R;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
/** A class that provides setup and helping functions for controlling the mapview, its controller,
 * and markers for yourself and your friend's positions
 * @author Mitchell Lane
 *
 */
public class MapManager implements OnMarkerDragListener {
	public static final String USER_SATELLITE_KEY = "SAT_KEY";


	private static final int POSITION_ZOOM_LEVEL = 18;
	private GoogleMap googleMap;
	private boolean satelliteOn;
	private boolean autoZoom = true;

	/* Singleton*/
	private static MapManager mapManager;

	public static MapManager getInstance(){
		return mapManager;
	}
	public static MapManager getInstance(GoogleMap googleMap, boolean satelliteOn, Context mainActivityContext) {
		Log.d("MapMan", "getInstance");
		mapManager = new MapManager(googleMap, satelliteOn, mainActivityContext);
		return mapManager;
	}

	private MapManager(GoogleMap googleMap, boolean satelliteOn, Context mainActivityContext) {
		Log.d("MapMan", "Contr");
		this.googleMap = googleMap;

		this.satelliteOn = satelliteOn;
		setup(googleMap, satelliteOn, mainActivityContext);
	}
	
	public Location getMyLocation(){
		return googleMap.getMyLocation();
	}

	private void setup(GoogleMap googleMap, boolean satelliteOn, Context mainActivityContext) {
		Log.d("MapMan", "Setup");
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.setOnMarkerDragListener(this);
		setSatelliteView(satelliteOn);
		Log.d("MapMan", "Setup end");
		//myPositionOverlay = new MapItemizedOverlay(myPositionMarker, mainActivityContext, MARKER_TEXT_SIZE, new UserOnTap(mainActivityContext));
	}


	


	/** Sets the map view to center itself around the given user location geoPoint */
	public void setMapViewToLocation(LatLng latLng){
		if (autoZoom) {
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, POSITION_ZOOM_LEVEL));
		} else {
			googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		}
	}


	public void setAutoZoom(boolean zoom) {
		autoZoom = zoom;
	}

	public void toggleSatelliteView(MenuItem item) {
		int mapType = googleMap.getMapType();
		if (mapType == GoogleMap.MAP_TYPE_NORMAL) {
			 setSatelliteView(true);
			 item.setTitle("Map view");
			 item.setIcon(R.drawable.location_map);
		} else {
			setSatelliteView(false);
			item.setTitle("Satellite view");
			item.setIcon(R.drawable.da_layer_satellite);
		}
	}

	/** Sets the satellite view on or off */
	public void setSatelliteView(boolean b){
		if (b) {
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else {
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
		satelliteOn = b;
	}

	public boolean isSatelliteOn() {
		return satelliteOn;
	}
	@Override
	public void onMarkerDrag(Marker marker) {
	}
	@Override
	public void onMarkerDragEnd(Marker marker) {

	}

	@Override
	public void onMarkerDragStart(Marker marker) {
	}
	public void moveCamera(LatLng latlng) {
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(POSITION_ZOOM_LEVEL));
	}
}
