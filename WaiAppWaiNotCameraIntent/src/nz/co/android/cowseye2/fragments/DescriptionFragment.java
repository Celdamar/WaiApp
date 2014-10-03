package nz.co.android.cowseye2.fragments;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.activity.DescriptionActivity;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class DescriptionFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static DescriptionFragment newInstance(int sectionNumber) {
		DescriptionFragment fragment = new DescriptionFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public DescriptionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.description_layout,
				container, false);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((DescriptionActivity) activity).onSectionAttached(getArguments()
				.getInt(ARG_SECTION_NUMBER));
	}

	public boolean hasDescription() {
		return !getDescription().equals("");

	}

	public String getDescription() {
		EditText descriptionEditText = (EditText) getActivity().findViewById(
				R.id.descriptionText);

		return descriptionEditText.getText().toString();
	}

	public void setText(String stringExtra) {
		EditText descriptionEditText = (EditText) getActivity().findViewById(
				R.id.descriptionText);

		// Set text of description if we have it
		descriptionEditText.setTextColor(Color.BLACK);

		descriptionEditText.setText(stringExtra);

	}

	public String getText() {
		EditText descriptionEditText = (EditText) getActivity().findViewById(
				R.id.descriptionText);
		return descriptionEditText.getText().toString();

	}

	public void setFocus() {
		EditText descriptionEditText = (EditText) getActivity().findViewById(
				R.id.descriptionText);
		descriptionEditText.requestFocus();

	}
}
