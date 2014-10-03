package nz.co.android.cowseye2.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;








import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.activity.GridIncidentGalleryActivity;
import nz.co.android.cowseye2.event.GetImageEvent;
import nz.co.android.cowseye2.utility.Utils;
import nz.co.android.cowseye2.view.RiverWatchGallery;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class GetImageAsyncTask extends AsyncTask<Void, Void, String> {


	private  RiverWatchGallery riverWatchGallery;
	private  RiverWatchGallery.ViewHolder galleryHolder;
	private  GridIncidentGalleryActivity.ViewHolder gridHolder;

	private final GetImageEvent event;
	private final RiverWatchApplication myApplication;
	private final int incidentId;
	private GridIncidentGalleryActivity gridIncidentGalleryActivity;
	private int position;

	public GetImageAsyncTask(RiverWatchApplication myApplication, RiverWatchGallery riverWatchGallery, RiverWatchGallery.ViewHolder holder, GetImageEvent event, int positionInArray,  int incidentId){
		this.myApplication = myApplication;
		this.riverWatchGallery = riverWatchGallery;
		this.galleryHolder = holder;
		this.event = event;
		this.incidentId = incidentId;
		this.position = positionInArray;
	}

	public GetImageAsyncTask(RiverWatchApplication myApplication,GridIncidentGalleryActivity gridIncidentGalleryActivity,
			GridIncidentGalleryActivity.ViewHolder holder, GetImageEvent event, int position, int incidentId) {
		this.myApplication = myApplication;
		this.gridIncidentGalleryActivity = gridIncidentGalleryActivity;
		this.gridHolder = holder;
		this.event = event;
		this.position = position;
		this.incidentId = incidentId;
	}

	@Override
	protected String doInBackground(Void... Void) {
		HttpResponse response = event.processRaw();

		if(RiverWatchApplication.processEventResponse(response)){
			//save image from input stream
			return saveImageFromInputStream(response);
		}
		return null;
	}

	/**
	 * @param file path of saved image
	 */
	private String saveImageFromInputStream(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// A Simple JSON Response Read
//			InputStream instream = new BufferedInputStream(in)
			try {
				InputStream instream = entity.getContent();
				BufferedInputStream bInstream = new BufferedInputStream(instream);
				Bitmap bm = Utils.scaleBitmap(bInstream, 2);
				//save bitmap to filepath
				if(bm!=null){
					//save image
					String imagePath = myApplication.saveBitmapToDisk(bm, incidentId,  (gridIncidentGalleryActivity != null));
					//save file path to database
					if(riverWatchGallery!=null)
						myApplication.getDatabaseAdapter().updateIncidentTable(incidentId+"", myApplication.getDatabaseAdapter().getLocalImagePathNameContentValues(imagePath));
					else if(gridIncidentGalleryActivity!=null)
						myApplication.getDatabaseAdapter().updateIncidentTable(incidentId+"", myApplication.getDatabaseAdapter().getLocalThumbPathNameContentValues(imagePath));
					return imagePath;
				}
			} catch (IllegalStateException e) {
				Log.e(toString(), "IllegalStateException : "+e);
			} catch (IOException e) {
				Log.e(toString(), "IOException : "+e);

			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String imagePath) {
		if(riverWatchGallery!=null)
			riverWatchGallery.setImage(galleryHolder, imagePath,position);
		else if(gridIncidentGalleryActivity!=null)
			gridIncidentGalleryActivity.setImage(gridHolder, imagePath, position);
	}
}