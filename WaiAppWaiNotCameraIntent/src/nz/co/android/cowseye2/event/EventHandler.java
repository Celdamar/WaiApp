package nz.co.android.cowseye2.event;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;




import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.utility.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import android.util.Log;

public class EventHandler {

	private Queue<Event> eventQueue;
	private RiverWatchApplication myApplication;

	public EventHandler(RiverWatchApplication app) {
		eventQueue = new LinkedList<Event>();
		myApplication = app;
	}

	public void addEvent(Event e) {
		eventQueue.offer(e);
	}

	/** Starts processing events sequentially */
	public void processEvents(){
		if(eventQueue.isEmpty()){
			Log.i(toString(), "No events to process");
			return;
		}
		//only process events if we have an internet connection
		if(myApplication.isOnline()){
			while(!eventQueue.isEmpty()){
				Event currentEvent = eventQueue.peek();
				Log.i(toString(), "processing : " + currentEvent.toString());
				boolean success = processSubmissionEventResponse(currentEvent.processRaw());
//				Toast.makeText(myApplication, "Success : "+success, Toast.LENGTH_LONG).show();
				Log.i(toString(), "successfully processed event? : "+ success);
				//only actually remove event if successful
				if(success){
					deleteImage(currentEvent);
					//update the success of event in the database
//					myApplication.getDatabaseAdapter().updateEventToProcessed(currentEvent.getTimeStamp());
					//remove event
					eventQueue.poll();
				}
				//if unsuccessful stop event handling and move the event to the end of the queue
				else{
					Event failedEvent = eventQueue.poll();
					//if we havn't reached a maximum amount of fails then keep on trying
					if(failedEvent.getFailCount()<Event.FAIL_COUNT_MAX){
						failedEvent.incrementFailCount();
						//update the fail count of the event in the database
//						myApplication.getDatabaseAdapter().updateEventFailCount(failedEvent.getTimeStamp(), failedEvent.getFailCount());
						addEvent(failedEvent);
					}
					else{
						//remove the failed event from the database
//						myApplication.getDatabaseAdapter().removeFailedEvent(failedEvent.getTimeStamp());
					}
					break;
				}
			}
		}
		else{
			//no internet connection - set delay time to 2 minutes
			Log.i(toString(), "no internet connection - delaying event timer by 2 minutes");
	       //myApplication.requestDelayedEventsTimer(); // uncomment if we use this
		}
	}

	/* Deletes the image belonging to the current event */
	private void deleteImage(Event currentEvent) {
		URI uri = null;
		try {
			uri = new URI(currentEvent.getImagePath().getPath());
		} catch (URISyntaxException e) {
			Log.e(toString(), "URISyntaxException: "+e);
			return ;
		}
			Log.d(toString(), "path1 : "+uri);
				File imageFile = new File(uri);
//		Log.d(toString(), "deleteImage image exists before ? "+imageFile.exists());
		//delete image
		if(imageFile.exists())
			imageFile.delete();
//		Log.d(toString(), "deleteImage image exists after ? "+imageFile.exists());

	}
	

	/**
	 * Deals with the response from a submission event return
	 * @param response from a submission event
	 * @return true if succesfull submission, otherwise false
	 */
	public static boolean processSubmissionEventResponse(HttpResponse response){
		if(response==null)
			return false;
		StatusLine statusLine = response.getStatusLine();
		if(statusLine == null)
			return false;
		int statusCode = statusLine.getStatusCode();
		Log.i("app", "statusCode : "+statusCode);
		try{
			switch(statusCode){
			case Utils.HTTP_OK:
				Log.i("app", "Sucessful submission!");
				return true;
			case Utils.HTTP_LOGIC_ERROR:
				Log.i("app", "Logic error: Unsucessful submission!");

				return false;
			case Utils.HTTP_SERVER_ERROR:
				Log.i("app", "Server error: Unsucessful submission!");
				return false;
			default:
				Log.i("app", "Uncaught error: Unsucessful submission!");
				return false;
			}
//			JSONObject jsonObject = JSONHelper.parseHttpResponseAsJSON(response);
//			Log.d("app", "jsonObject : "+jsonObject);
		}
		catch(Exception f){ 
			Log.e("app", "Exception in JsonParsing : "+f);
		}
		return false;

	}
	

	/** Returns the amount of events awaiting upload */
	public int getNumberEventsAwaitingUpload() {
		return eventQueue.size();
	}
}
