package nz.co.android.cowseye2.activity;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.event.SubmissionEventBuilder;
import nz.co.android.cowseye2.gps.GPSManager;
import nz.co.android.cowseye2.gps.MapManager;
import nz.co.android.cowseye2.gps.MarkerMoveInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * This is the activity for selecting the location of the pollution event
 * 
 * @author Mitchell Lane
 * 
 */
public class RecordLocationActivity extends ActionBarActivity implements
		MarkerMoveInterface {

	// private Button backButton;
	// private Button nextButton;

	private static LocationManager mLocationManager;
	private GPSManager gpsManager;
	private MapManager mapManager;
	private ProgressDialog dialog;
	private GoogleMap mMap;

	// address got from reverse geo coding
	private LatLng addressCoordinates;
	private SubmissionEventBuilder submissionEventBuilder;
	private RiverWatchApplication myApplication;
	private Bundle savedInstanceState;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myApplication = (RiverWatchApplication) getApplication();
		this.savedInstanceState = savedInstanceState;
		setContentView(R.layout.location_layout);
		makeActionBar();
		// Intent intent = getIntent();
		// backButton = (Button)findViewById(R.id.backButton);
		// nextButton = (Button)findViewById(R.id.doneButton);
		// backButton.setOnClickListener(new
		// Utils.BackEventOnClickListener(this));

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		// Check if GPS enabled
		isGPSenabled();
		if(!isNetworkAvailable()){
			//initial check
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("No internet connection found. GPS may not be accurate if indoors.")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//Nothing should happen
				}
			});
			builder.create();
			builder.show();
		}
		if (status != ConnectionResult.SUCCESS) { // Google Play Services
			// are not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
					this, requestCode);
			dialog.show();
		} else { // Google Play Services are available
			setUpMapIfNeeded(savedInstanceState);
		}
		submissionEventBuilder = SubmissionEventBuilder
				.getSubmissionEventBuilder(myApplication);

	}
	
	/**Online connectivity check*/
	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

		// test for connection
		if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void isGPSenabled() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// Ask the user to enable GPS
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Location Manager");
			builder.setMessage("Would you like to enable GPS?");
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Launch settings, allowing user to make a change
					Intent i = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(i);
				}
			});
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// No location service, no Activity
					// finish();
					cantContinueWithoutGPS();
				}
			});
			builder.create().show();
		}

	}
	
	public void cantContinueWithoutGPS() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setTitle("Error");
		builder1.setMessage("You need GPS on to continue");
		builder1.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO dont do what youre about to do go backto the home screen
			}

		});
		builder1.create().show();
	}

	private void makeActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		// inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.legal) {
			buildMapslegalMessage(this.savedInstanceState);
		} else if (itemId == R.id.mapview) {
			mapManager.toggleSatelliteView(item);
		} else if (itemId == R.id.nextpage) {
			nextActivety();
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void nextActivety() {
		if (hasAllDetails()) {
			// Intent intent = buildLocationDataIntent(RESULT_OK);
			// startActivity(intent);
			if (dialog != null)
				dialog.dismiss();
			if (addressCoordinates != null)
				submissionEventBuilder.setGeoCoordinates(addressCoordinates);
			startActivity(new Intent(RecordLocationActivity.this,
					PreviewActivity.class));

			// get coordinates from address location
			// dialog = ProgressDialog.show(LocationActivity.this,
			// "Acquiring coordinates from address", "Please wait...");
			// TODO DO NOT
			// new GeoCodeCoordinatesService(LocationActivity.this,
			// gpsManager.getGeoCoder(),
			// addressEditText.getText().toString().trim()).execute();
		} else
			Toast.makeText(RecordLocationActivity.this,
					getResources().getString(R.string.nocoordinates),
					Toast.LENGTH_SHORT).show();
	}

	private void buildMapslegalMessage(final Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this))
		// .setCancelable(false)
				.setPositiveButton(
						this.getResources().getString(
								R.string.positive_button_title),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.cancel();
								setupManagers(savedInstanceState);
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	protected void setupManagers(Bundle savedInstanceState) {
		int result = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		if (result != ConnectionResult.SUCCESS) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(result, this,
					69);
			dialog.setCancelable(false);
			dialog.show();
		}
		setUpMapIfNeeded(savedInstanceState);
	}

	@Override
	protected void onPause() {
		// remove the listener for gps updates
		if (gpsManager != null)
			gpsManager.removeUpdates();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// add the listener again for gps updates
		if (gpsManager != null)
			gpsManager.requestUpdateListeners();
		super.onResume();
		setUpMapIfNeeded(this.savedInstanceState);
	}

	private void setUpMapIfNeeded(Bundle savedInstanceState) {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			Log.d("RecLocAc", "1");
			// Try to obtain the map from the SupportMapFragment.
			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.mapview);
			Log.d("RecLocAc", "2");
			mMap = mapFragment.getMap();
			mMap.setMyLocationEnabled(true);
			Log.d("RecLocAc", "3");
			if (checkReady()) {
				Log.d("RecLocAc", "4");
				// Check if we were successful in obtaining the map.
				mapManager = MapManager.getInstance(mMap, false, this);
				
				gpsManager = GPSManager.getInstance(mapManager,
						mLocationManager, this, savedInstanceState);
				Log.d("RecLocAc", "5");
			}
		}

	}

	private boolean checkReady() {
		if (mMap == null) {
			Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	/** Save state of app if activity is destroyed */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		if (gpsManager != null)
			gpsManager.saveStateOnDestroy(savedInstanceState);
	}

	// @Override
	// protected boolean isRouteDisplayed() {
	// return false;
	// }

	public void setAddress(LatLng addressCoordinates) {
		this.addressCoordinates = addressCoordinates;
		Log.d(toString(), "setting geo : " + addressCoordinates);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	// public Intent buildLocationDataIntent(int RESULT_TYPE) {
	// if(dialog!=null)
	// dialog.dismiss();
	// Intent intent=new Intent(this, PreviewActivity.class);
	// intent.putExtra(Constants.LOCATION_KEY, getLocation());
	// //if reverse geo coded the address
	// //if user has not changed the address ( then we can make a link and
	// coordinates)
	// if(getLocation().equals(geoAddress)){
	// double lat = addressCoordinates.getLatitudeE6();
	// //addrCoord.getLatitude()*1E6;//gpsManager.getUserLocationGeoPoint().getLatitudeE6()
	// / 1E6;
	// double lon =
	// addressCoordinates.getLongitudeE6();//addrCoord.getLongitude()*1E6;//gpsManager.getUserLocationGeoPoint().getLongitudeE6()
	// / 1E6;
	// String link = Constants.GOOGLE_MAP_LINK + (lat/1E6)+","+(lon/1E6);
	// intent.putExtra(Constants.LOCATION_LATITUDE_KEY,(int)lat);//gpsManager.getUserLocationGeoPoint().getLatitudeE6());
	// intent.putExtra(Constants.LOCATION_LONGITUDE_KEY,(int)lon);//
	// gpsManager.getUserLocationGeoPoint().getLongitudeE6());
	// intent.putExtra(Constants.LOCATION_GOOGLE_LINK, link);
	// Log.e(toString(), "Putting in link!");
	// }
	// setResult(RESULT_TYPE, intent);
	// // Toast.makeText(RecordLocationActivity.this,
	// getResources().getString(R.string.savingAddress),
	// Toast.LENGTH_SHORT).show();
	// return intent;
	// }
	public void errorGeoCodeAddress() {
		dialog.dismiss();
		Toast.makeText(this,
				getResources().getString(R.string.errorInGeoCoding),
				Toast.LENGTH_SHORT).show();

	}

	protected boolean hasAllDetails() {
		Log.d(toString(), "coord : " + addressCoordinates);
		return addressCoordinates != null;
	}

	@Override
	public void newLatLng(LatLng latlng) {
		gpsManager.updateLocationActivity(latlng);
	}

}
