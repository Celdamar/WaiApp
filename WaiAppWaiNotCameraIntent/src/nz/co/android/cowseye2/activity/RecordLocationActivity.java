package nz.co.android.cowseye2.activity;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.event.SubmissionEventBuilder;
import nz.co.android.cowseye2.fragments.NavigationDrawerFragment;
import nz.co.android.cowseye2.fragments.RecordLocationFragment;
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
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
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
public class RecordLocationActivity extends AbstractSubmissionActivity
		implements MarkerMoveInterface {

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
		setContentView(R.layout.activity_main);
		setupDrawer();
		// Intent intent = getIntent();
		// backButton = (Button)findViewById(R.id.backButton);
		// nextButton = (Button)findViewById(R.id.doneButton);
		// backButton.setOnClickListener(new
		// Utils.BackEventOnClickListener(this));

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Check if GPS enabled
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps(savedInstanceState);
		} else
			setupManagers(savedInstanceState);
		submissionEventBuilder = SubmissionEventBuilder
				.getSubmissionEventBuilder(myApplication);

	}

	private void setupDrawer() {
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		mNavigationDrawerFragment.showDrawerToggle(false);

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

	
	@Override
	protected void nextActivety() {
		if (!hasAllDetails()) {
			Toast.makeText(RecordLocationActivity.this,
				getResources().getString(R.string.nocoordinates),
				Toast.LENGTH_SHORT).show();
		}
		//GeoCoordinates have been taken and now to move onto the preview Activity
		else {
			if (dialog != null)
				dialog.dismiss();
			if (addressCoordinates != null)
				submissionEventBuilder.setGeoCoordinates(addressCoordinates);
				Intent intent = new Intent(RecordLocationActivity.this,
						PreviewActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		
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

	private void buildAlertMessageNoGps(final Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getResources().getString(R.string.gps_message))
				.setCancelable(false)
				.setPositiveButton(
						this.getResources().getString(
								R.string.positive_button_title),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								setupManagers(savedInstanceState);
							}
						})
				.setNegativeButton(
						this.getResources().getString(
								R.string.negative_button_title),
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
		// setUpMapIfNeeded(savedInstanceState);
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
			SupportMapFragment mapFragment = f.getMap(this);
			Log.d("RecLocAc", "2");
			mMap = mapFragment.getMap();
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
		//gpsManager.setAutoUpdateLocation(false);
		//gpsManager.updateLocationActivity(latlng, true);
		gpsManager.updateLocationActivity(latlng);
	}
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private boolean justopened = true;
	private RecordLocationFragment f;

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments

		if (justopened) {
			justopened = false;

			FragmentManager fragmentManager = getSupportFragmentManager();
			f = RecordLocationFragment.newInstance(position);

			fragmentManager.beginTransaction()
					.replace(R.id.container, f, "tag_Select_Image_frag")
					.commit();

		} else {

			myApplication.deleteImage(submissionEventBuilder.getImagePath()
					.toString());

			Intent i = new Intent(RecordLocationActivity.this,
					MainScreenActivity.class);

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			i.putExtra("selectedItem", position);

			startActivity(i);
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.

			restoreActionBar();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.map_menu, menu);
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}
}