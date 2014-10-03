package nz.co.android.cowseye2.event;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;

import android.net.Uri;

/**
 * This is an event that needs to be processed by interacting with the web server at Protext
 * @author Mitchell Lane
 *
 */
public interface Event {
	public static int TIMEOUT_MS = 30000; //30s
	public static int FAIL_COUNT_MAX = 5;
	/**  Constructs the initial HttpClient*/

	public HttpClient constructHttpClient();
	
	/**  Authorizes the HTTP Post method */
	public HttpPost setAuthorization(HttpPost httpPost);

	/** Constructs a basic HTTP Post request */
	public HttpPost constructHttpPost();
	/** Makes an entity for the Post message */
	public MultipartEntity makeEntity();
	/** Processes the event and returns true if successfull, otherwise false */
	public boolean processForSuccess();
	/** Processes the event and returns the response of the event */
	public HttpResponse processRaw();
	/** Incremement the amount of times the event has failed */
	public void incrementFailCount();
	/** returns the amount of times the event has failed */
	public int getFailCount();
	/** returns the path of the image associated with this event on disk */
	public Uri getImagePath();
	/** returns the time stamp of the event when it was created */
	public String getTimeStamp();
	/** returns the description of the image associated with this event on disk */
	public String getImageDescription();
	/** returns the list of the tags of the image associated with this event on disk */
	public List<String> getImageTag();
	
}
