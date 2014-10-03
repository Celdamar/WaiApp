package nz.co.android.cowseye2.activity;

import java.util.List;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.database.Incident;
import nz.co.android.cowseye2.event.GetImageEvent;
import nz.co.android.cowseye2.service.GetImageAsyncTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GridIncidentGalleryActivity extends Activity {

	private Button backButton;

	private RiverWatchApplication myApplication;
	private LayoutInflater inflater;

	private List<Incident> incidents;
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_incident_gallery_layout);

		myApplication = (RiverWatchApplication) getApplication();
		incidents = myApplication.getDatabaseAdapter().getAllIncidents();
		this.handler = new Handler();
		// Cache the LayoutInflate to avoid asking for a new one each time.
		inflater = LayoutInflater.from(this);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));
		gridview.setOnItemClickListener(new OnItemClickListener() {
			Intent intent = new Intent(GridIncidentGalleryActivity.this,
					IncidentGalleryActivity.class);

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {

				intent.putExtra("Page Number", position);
				startActivity(intent);
			}
		});
		// super.onActivityResult(0, 0, this.getIntent());
		 //this.setResult(0, this.getIntent());
		//moveTaskToBack(true);
		//GridIncidentGalleryActivity.this.finish();
	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			return incidents.size();
		}

		@Override
		public Incident getItem(int position) {
			return incidents.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// create a new ImageView for each item referenced by the Adapter
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) { // if it's not recycled, initialize some
				// attributes
				convertView = inflater.inflate(
						R.layout.incident_layout_cellwithouttext, null);
				holder = new ViewHolder();

				holder.imageView = (ImageView) convertView
						.findViewById(R.id.incident_image);
				holder.descriptionView = (TextView) convertView
						.findViewById(R.id.incident_description);
				holder.progressBar = (ProgressBar) convertView
						.findViewById(R.id.incident_progress_bar);

				holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				holder.imageView.setPadding(8, 8, 8, 8);
				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the View
				holder = (ViewHolder) convertView.getTag();
			}
			buildView(position, holder);

			return convertView;
		}
	}

	public class ViewHolder {
		ProgressBar progressBar;
		ImageView imageView;
		TextView descriptionView;
	}

	/* Build a View for the MyGalleryImage adapter */
	public void buildView(int position, final ViewHolder holder) {
		Incident incident = incidents.get(position);
		// try to get from local storage
		String localImageUri = incident.getLocalThumbnailUrl();
		if (localImageUri != null && !localImageUri.equals("")) {
			setImage(holder, localImageUri, position);
		} else {
			/**
			 * If fails to get from local storage, put a progress bar in and
			 * download
			 */
			// launch asynctask to get image
			GetImageEvent event = new GetImageEvent(incident.getThumbnailUrl());
			new GetImageAsyncTask(myApplication, this, holder, event, position,
					incident.getId()).execute();
		}
	}

	public void setImage(ViewHolder holder, String pathName, int positionInArray) {
		if (pathName != null && !pathName.equals("")) {
			incidents.get(positionInArray).setLocalThumbnailUrl(pathName);
			Bitmap bm = BitmapFactory.decodeFile(pathName);
			holder.imageView.setImageBitmap(bm);
			holder.progressBar.setVisibility(View.INVISIBLE);
		}
	}

}