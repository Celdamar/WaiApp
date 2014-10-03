package nz.co.android.cowseye2.fragments;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.activity.RecordLocationActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class RecordLocationFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static RecordLocationFragment newInstance(int sectionNumber) {
		RecordLocationFragment fragment = new RecordLocationFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public RecordLocationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.location_layout, container,
				false);
		return rootView;
	}

	public SupportMapFragment getMap(RecordLocationActivity Activity) {
		System.out.println("1111111111111111111111111111111111111111111");
		return (SupportMapFragment) Activity.getSupportFragmentManager()
				.findFragmentById(R.id.mapview);
	}

}
