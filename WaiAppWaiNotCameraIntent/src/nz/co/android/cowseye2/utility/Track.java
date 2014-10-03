package nz.co.android.cowseye2.utility;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * Basic class to track actions that occur within the application. The actions
 * may be directly related to user input such as a button being pressed.
 * Alternatively, it could be application-driven, such as a network event
 * finishing.
 * 
 * @author Ben Vidulich <ben@vidulich.co.nz>
 * 
 */
public class Track {

	/**
	 * The URL to POST the tracking data to.
	 * 
	 * The preferred method is to set POST_URL to null and define it in
	 * AndroidManifest.xml. <meta-data android:name="track.PostUrl"
	 * android:value="http://track.com/..." />
	 */
	private static final String POST_URL = null;

	/**
	 * A storage container for the tracking data being sent to the server.
	 * 
	 * @author Ben Vidulich <ben@vidulich.co.nz>
	 * 
	 */
	private static class TrackingData {
		/**
		 * The current context.
		 */
		private Context context;
		/**
		 * Describes the type of tracking action. For example, "Duration" could
		 * be used for a timing action, or "Event" to indicate an event has
		 * fired.
		 */
		private String category;

		/**
		 * Describes the tracking action that occured. For example, "Resumed"
		 * could be used to measure how long an activity remains in the Resumed
		 * state, or "Clicked" if a button was clicked.
		 */
		private String action;

		/**
		 * Additional information to describe the tracking action. For example,
		 * "MainActivity" could be used to indicate that the tracking action had
		 * something to do with the activity MainActivity.
		 */
		private String label;

		/**
		 * Optional numerical data.
		 */
		private Double val;

		public TrackingData(Context context, String category, String action,
				String label, Double val) {
			super();
			this.context = context;
			this.category = category;
			this.action = action;
			this.label = label;
			this.val = val;
		}

		public Context getContext() {
			return context;
		}

		public String getCategory() {
			return category;
		}

		public String getAction() {
			return action;
		}

		public String getLabel() {
			return label;
		}

		public Double getVal() {
			return val;
		}
		
		/**
		 * Creates a HashMap containing key-value pairs of all the data in an
		 * instance of the TrackingData class.
		 * 
		 * @param td
		 *            Tracking data
		 * @return HashMap containing key-value pairs
		 */
		public HashMap<String, String> getHashMap() {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("category", getCategory());
			hm.put("action", getAction());
			hm.put("label", getLabel());
			hm.put("value", getVal().toString());
			return hm;
		}

	}

	/**
	 * Sends the tracking data to the server in a separate (background) thread.
	 * 
	 * @author Ben Vidulich <ben@vidulich.co.nz>
	 * 
	 */
	private static class PostTrackDataTask extends
			AsyncTask<TrackingData, Void, Void> {

		@Override
		protected Void doInBackground(TrackingData... params) {
			// Ensure correct number of params
			if (params.length != 1 || params[0] == null) {
				return null;
			}

			TrackingData td = params[0];
			URL url = null;
			HttpURLConnection urlConnection = null;
			OutputStream out = null;
			
			String urlParams = prepareUrlParams(td);
			
			try {
				url = new URL(Track.getPostURL(td.getContext()));
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setChunkedStreamingMode(0);
				out = new BufferedOutputStream(
						urlConnection.getOutputStream());
				out.write(urlParams.getBytes());
				out.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
			}
			
			return null;
		}

		/**
		 * Creates a string containing containing URL encoded key-value pairs.
		 * 
		 * e.g. foo=bar&lorem=ipsum
		 * 
		 * @param td
		 *            Tracking data
		 * @return URL encoded string
		 */
		private String prepareUrlParams(TrackingData td) {
			HashMap<String, String> hm = td.getHashMap();
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Entry<String, String> entry : hm.entrySet()) {
				// Skip the entry if the value is null
				if (entry.getValue() == null) {
					continue;
				}
				// Add ampersands '&' between key-value pairs
				if (first) {
					first = false;
				} else {
					sb.append("&");
				}
				// URL encode each key-value pair
				try {
					sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
					sb.append("=");
					sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			return sb.toString();
		}

	}

	/**
	 * Record an instance of an event occurance.
	 * 
	 * @param context
	 *            The current context.
	 * @param category
	 *            Describes the type of tracking action. For example, "Duration"
	 *            could be used for a timing action, or "Event" to indicate an
	 *            event has fired.
	 * @param action
	 *            Describes the tracking action that occured. For example,
	 *            "Resumed" could be used to measure how long an activity
	 *            remains in the Resumed state, or "Clicked" if a button was
	 *            clicked.
	 */
	public static void track(Context context, String category, String action) {
		Track.track(context, category, action, null, null);
	}

	/**
	 * Record an instance of an event occurance.
	 * 
	 * @param context
	 *            The current context.
	 * @param category
	 *            Describes the type of tracking action. For example, "Duration"
	 *            could be used for a timing action, or "Event" to indicate an
	 *            event has fired.
	 * @param action
	 *            Describes the tracking action that occured. For example,
	 *            "Resumed" could be used to measure how long an activity
	 *            remains in the Resumed state, or "Clicked" if a button was
	 *            clicked.
	 * @param label
	 *            Additional information to describe the tracking action. For
	 *            example, "MainActivity" could be used to indicate that the
	 *            tracking action had something to do with the activity
	 *            MainActivity.
	 */
	public static void track(Context context, String category, String action,
			String label) {
		Track.track(context, category, action, null, null);
	}

	/**
	 * Record an instance of an event occurance.
	 * 
	 * @param context
	 *            The current context.
	 * @param category
	 *            Describes the type of tracking action. For example, "Duration"
	 *            could be used for a timing action, or "Event" to indicate an
	 *            event has fired.
	 * @param action
	 *            Describes the tracking action that occured. For example,
	 *            "Resumed" could be used to measure how long an activity
	 *            remains in the Resumed state, or "Clicked" if a button was
	 *            clicked.
	 * @param label
	 *            Additional information to describe the tracking action. For
	 *            example, "MainActivity" could be used to indicate that the
	 *            tracking action had something to do with the activity
	 *            MainActivity.
	 * @param val
	 *            Optional numerical data.
	 */
	public static void track(Context context, String category, String action,
			String label, Double val) {
		TrackingData td = new TrackingData(context, category, action, label,
				val);
		new PostTrackDataTask().execute(td);
	}

	/**
	 * Gets the URL to send the tracking data to.
	 * 
	 * @param context
	 *            The current application context.
	 * @return The URL to send the tracking data to.
	 */
	private static String getPostURL(Context context) {
		if (Track.POST_URL != null) {
			return Track.POST_URL;
		} else if (context != null) {
			try {
				ApplicationInfo ai = context.getPackageManager()
						.getApplicationInfo(context.getPackageName(),
								PackageManager.GET_META_DATA);
				Bundle bundle = ai.metaData;
				String url = bundle.getString("track.PostUrl");
				return url;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return null;
	}

}
