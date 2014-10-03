package nz.co.android.cowseye2.fragments;

import java.util.List;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.activity.GridIncidentGalleryActivity;
import nz.co.android.cowseye2.activity.IncidentGalleryActivity;
import nz.co.android.cowseye2.activity.MainScreenActivity;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.IntentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GridIncidentGalleryFragment extends Fragment{
	
	private RiverWatchApplication myApplication;
	private LayoutInflater inflater;

	private List<Incident> incidents;

	private Handler handler;
	private int position;
	
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static GridIncidentGalleryFragment newInstance(int sectionNumber) {
		GridIncidentGalleryFragment fragment = new GridIncidentGalleryFragment(sectionNumber);
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public GridIncidentGalleryFragment(int position) {
		this.position = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		this.inflater = inflater;

		View rootView = inflater.inflate(R.layout.grid_incident_gallery_layout, container, false);
		inflater.inflate(R.layout.incident_gallery_layout, container, false);		
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	//	setContentView(R.layout.grid_incident_gallery_layout);
		
		GridView gridview = (GridView) getActivity().findViewById(R.id.gridview);
		if(gridview == null){ System.out.println("gridview is null");}
		gridview.setAdapter(new ImageAdapter(getActivity()));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {				
				loadIncidentGallery(position);
			}
		});
		
		
		inflater =  (LayoutInflater) getActivity().getApplicationContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}
	
	public void loadIncidentGallery(int pos){

		//intent.putExtra("Page Number", position);
		getActivity().getIntent().putExtra("Page Number", pos);
		FragmentManager fm = this.getFragmentManager();
		IncidentGalleryFragment f = IncidentGalleryFragment.newInstance(position);
		fm.beginTransaction().replace(R.id.container, f).commit();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainScreenActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		System.out.println("onAttach");
		
		myApplication = (RiverWatchApplication) getActivity().getApplication();
		incidents = myApplication.getDatabaseAdapter().getAllIncidents();
		this.handler = new Handler();		
			
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
			/*GetImageEvent event = new GetImageEvent(incident.getThumbnailUrl());
			new GetImageAsyncTask(myApplication, getActivity(), holder, event, position,
					incident.getId()).execute();*/
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

