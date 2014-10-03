package nz.co.android.cowseye2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;






import nz.co.android.cowseye2.database.DatabaseAdapter;
import nz.co.android.cowseye2.database.DatabaseConstructor;
import nz.co.android.cowseye2.event.Event;
import nz.co.android.cowseye2.utility.JSONHelper;
import nz.co.android.cowseye2.utility.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

public class RiverWatchApplication extends Application  {

	/* Service paths */
//	public static String server_path = "http://api.riverwatch.co.nz:80/wainz";
//	public static String server_path = "http://homepages.ecs.vuw.ac.nz/wainz";
	//public static String server_path = "http://www.wainz.org.nz/api/image";
	//public static String server_path = "http://www.wainz.org.nz";
	public static String server_path = "http://www-test.wainz.org.nz";
	public static String submission_path = server_path + "/api/image";
	public static String get_incidents_path = server_path + "/approved";
	public static String get_incidents_path_start = "/start=";
	public static String get_incidents_path_number = "/number=";

	private static final long timerZeroDelay = 0;
	private static final long timerEventsProcessingPeriod = 300000; // 5 minutes
	private static final long timerEventsProcessingLargeDelay = 6000000; // 30 minutes
	private static final double MAX_TIMER_DELAYED_MULTIPLIER = 30; // 24 * 30 min = 720 min = 12 hours
	private double timerDelayedMultiplier = 1; // multiplier for the delay between event processing in the case of consecutive event fails or internet loss

	private static boolean eventProcessingSetup = false;

	//public EventHandler eventHandler;
	private Timer updateEventsTimer;
	private DatabaseConstructor databaseConstructor;
	private DatabaseAdapter databaseAdapter;

	//Start of application
	@Override
	public void onCreate() {
		super.onCreate();

		loadDatabase();
		System.out.println("database loaded Riverwatch App created");
		setupApplication();
	}

	private void setupApplication() {
		updateEventsTimer = new Timer();
		//eventHandler = new EventHandler(this);
	}

	/**
	 * Constructs and loads the database
	 */
	private void loadDatabase(){
		databaseConstructor = new DatabaseConstructor(this);
		try {
			databaseConstructor.createDataBase();
		} catch (IOException ioe) {
			Log.e(this.toString(),"Unable to create database");
		}
		try {
			databaseConstructor.openDataBase();
		}catch(SQLException sqle){
			Log.e(this.toString(),"Unable to open database");
		}
		databaseAdapter = new DatabaseAdapter(databaseConstructor);
	}


//	/** Adds this event to the database of events
//	 *
//	 * @param event - event to add
//	 * @param type - type of the event. one of (check_in, check_out, registration)
//	 * @param employeeId - employeedId of the employee if type is registration, otherwise null
//	 */
//	public void addNewEventToDatabase(Event event, String type, String employeeId) {
//		databaseAdapter.addNewEvent(event, type, employeeId);
//	}


	public DatabaseAdapter getDatabaseAdapter() {
		return databaseAdapter;
	}

/*
	public EventHandler getEventHandler(){
		return eventHandler;
	}

	/* Sets up and starts the timer to update events */
/*	private void startEventProcessingTimer(final long initialDelay){
		eventProcessingSetup = true;
		Log.i(toString(), "Starting Event Processing");
		updateEventsTimer = new Timer();
		/* Updates the event processing */
/*		TimerTask processEvents = new TimerTask() {
			public void run() {
				Log.i(toString(), "processing events");
				eventHandler.processEvents();
			}
		};
		updateEventsTimer.scheduleAtFixedRate(processEvents, initialDelay, timerEventsProcessingPeriod);
	}

	/** Requests the timer to update event processing if not already */
/*	public void requestStartEventHandling(){
		if(!eventProcessingSetup){
			eventProcessingSetup = true;
			//starts timer with the normal delay of 0 milliseconds
			startEventProcessingTimer(timerZeroDelay);
		}
	}

	/** Stops the current time and starts a delayed timer
	 * Called when the network is down, for a longer delay between trying again */
/*	public void requestDelayedEventsTimer(){
		stopTimerEventHandling();
		if(timerDelayedMultiplier<1)
			timerDelayedMultiplier=1;
		else if(timerDelayedMultiplier>MAX_TIMER_DELAYED_MULTIPLIER)
			timerDelayedMultiplier = MAX_TIMER_DELAYED_MULTIPLIER;
		//start event handling with the large delay
		startEventProcessingTimer((long)(timerEventsProcessingLargeDelay*timerDelayedMultiplier));
		//Next time an event has failed the delay will be twice as long
		if(timerDelayedMultiplier<MAX_TIMER_DELAYED_MULTIPLIER)
			timerDelayedMultiplier*=2;
	}

	/** Forces the event processing to start*/
/*	public void forceStartEventHandling(){
		//stops the current timer
		stopTimerEventHandling();
		//starts timer again with delay of zero so instant update
		requestStartEventHandling();
	}

	/** Stops the event processing timer if it is currently running */
/*	public void stopTimerEventHandling(){
		if(eventProcessingSetup){
			Log.i(toString(),"stopping Timer Event Handling" );
			eventProcessingSetup = false;
			updateEventsTimer.cancel();
		}
	}
*/	/** Returns whether this device is currently connected to a network */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm==null)
			return false;
		NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		Boolean is3g = networkInfo==null? false : networkInfo.isConnected();
		networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		Boolean isWifi= networkInfo==null? false : networkInfo.isConnected();
		return is3g || isWifi;
	}

	/** Returns whether this device has GPS enabled */
	public boolean isGPSEnabled(){
		LocationManager mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if(mLocationManager==null)
			return false;
		// Check if GPS enabled
		return mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
	}


	/** Downloads an employee image thumbnail
	 * @param url - url to download image thumbnail from
	 * @param e - thumbnail belongs to this employee
	 * @param lastImageThumbPath - path where the last thumbnail for the employee is if it exists
	 * @return the local path of the saved thumbnail
	 */
//	private String downloadEmployeeImageThumbnail(String url, String lastImageThumbPath) {
//
//		Bitmap image = RestClient.getBitmapThroughGETRequestURL(url);
//		if(image!=null){
//			try{
//				String localImageThumbnail = saveBitmapToDisk(image);
//				//Delete last image if it exists
//				if(lastImageThumbPath!=null && !lastImageThumbPath.equals("")){
//					deleteImage(lastImageThumbPath);
//				}
//				return localImageThumbnail;
//			}
//			catch(IOException f){
//				Log.e(toString(), "Could not save image to disk : "+f);
//			}
//		}
//		return null;
//	}

	/* Saves a bitmap to disk */
	public String saveBitmapToDisk(Bitmap bitmap, int incidentId, boolean isThumb) throws IOException {
		try{
//			final long num = System.currentTimeMillis();
			final String ID = getString(R.string.app_name).replace(" ", "_")+ (isThumb? "thumb" : "full") +"_"+incidentId;
			File dir = this.getDir("", Context.MODE_PRIVATE);
			String pathToDir = dir.getAbsolutePath();
			final String pathName = pathToDir + File.separator+ ID;
			FileOutputStream out = new FileOutputStream(pathName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			return pathName;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		throw new IOException("Could not create file or could not write to created file");
	}
	
	public Bitmap loadBitmapFromDisk(String pathName){
		Bitmap bitmap = BitmapFactory.decodeFile(pathName);
		return bitmap;
	}

	/** Deletes an image from local storage */
	public void deleteImage(String filePath) {
		File imageFile = new File(filePath);
//		Log.d(toString(), "deleteImage image exists before ? "+imageFile.exists());
		//delete image
		if(imageFile.exists())
			imageFile.delete();
//		Log.d(toString(), "deleteImage image exists after ? "+imageFile.exists());

	}

//	/* Deletes the image belonging to the current event */
//	public void deleteImage(Event currentEvent) {
//		Log.d(toString(), "path1 : "+currentEvent.getImagePath().getPath());
//		URI uri = null;
//		try {
//			uri = new URI(currentEvent.getImagePath().getPath());
//		} catch (URISyntaxException e) {
//			Log.e(toString(), "URISyntaxException: "+e);
//			return ;
//		}
//			Log.d(toString(), "path1 : "+uri);
//				File imageFile = new File(currentEvent.getImagePath().getPath());
//		Log.d(toString(), "deleteImage image exists before ? "+imageFile.exists());
//		//delete image
//		if(imageFile.exists())
//			imageFile.delete();
//		Log.d(toString(), "deleteImage image exists after ? "+imageFile.exists());
//
//	}

	/**
	 * Deals with the response from a submission event return
	 * @param response from a submission event
	 * @return true if succesfull submission, otherwise false
	 */
	public static boolean processEventResponse(HttpResponse response){
		if(response==null)
			return false;
		StatusLine statusLine = response.getStatusLine();
		if(statusLine == null)
			return false;
		int statusCode = statusLine.getStatusCode();
		try{
			switch(statusCode){
			case Utils.HTTP_OK:
				Log.i("app", "statusCode : "+statusCode+", Sucessful event response!");
				break;
			case Utils.HTTP_LOGIC_ERROR:
				Log.i("app", "statusCode : "+statusCode+", Logic error: Unsucessful event response!");
				return false;
			case Utils.HTTP_SERVER_ERROR:
				Log.i("app", "statusCode : "+statusCode+", Server error: Unsucessful event response!");
				return false;
			default:
				Log.i("app", "statusCode : "+statusCode+", ncaught error: Unsucessful event response!");
				return false;
			}
			//HttpEntity ans = response.getEntity();
			//ans.consumeContent();
			JSONObject jsonObject = JSONHelper.parseHttpResponseAsJSON(response);
			Log.d("app", "jsonObject : "+jsonObject);
			return true;
		}
		catch(Exception f){
			Log.e("app", "Exception in JsonParsing : "+f);
		}
		return false;

	}

	/* Deletes the image belonging to the current event */
	public void deleteImage(Event currentEvent) {
    File imageFile = new File(currentEvent.getImagePath().toString());
//		Log.d(toString(), "deleteImage image exists before ? "+imageFile.exists());
		//delete image
		if(imageFile.exists())
			imageFile.delete();
//		Log.d(toString(), "deleteImage image exists after ? "+imageFile.exists());

	}

	public String getRealPathFromURI(Uri contentURI) {
	    Cursor cursor = getContentResolver()
	               .query(contentURI, null, null, null, null);
	    cursor.moveToFirst();
	    int idx = cursor.getColumnIndex(MediaColumns.DATA);
	    return cursor.getString(idx);
	}
}
