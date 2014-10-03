package nz.co.android.cowseye2.fragments;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.activity.MainScreenActivity;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GalleryLoadingFragment extends Fragment {
	
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static GalleryLoadingFragment newInstance(int sectionNumber) {
		GalleryLoadingFragment fragment = new GalleryLoadingFragment(sectionNumber);
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	private int position;

	public GalleryLoadingFragment(int position) {
		this.position = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gallery_loading_fragment, container,
				false);
		inflater.inflate(R.layout.grid_incident_gallery_layout, container, false);
		return rootView;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		GridIncidentGalleryFragment f = GridIncidentGalleryFragment.newInstance(position);
		this.getFragmentManager().beginTransaction().replace(R.id.container, f).commit();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainScreenActivity) activity).onSectionAttached(getArguments()
				.getInt(ARG_SECTION_NUMBER));
	}

}
