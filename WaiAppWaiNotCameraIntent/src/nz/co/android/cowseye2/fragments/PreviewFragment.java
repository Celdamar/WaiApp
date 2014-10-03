package nz.co.android.cowseye2.fragments;

import java.io.IOException;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.activity.DescriptionActivity;
import nz.co.android.cowseye2.activity.PreviewActivity;
import nz.co.android.cowseye2.event.SubmissionEventBuilder;
import nz.co.android.cowseye2.utility.Utils;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class PreviewFragment extends Fragment {

	private int maxLength = 1000;

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static PreviewFragment newInstance(int sectionNumber) {
		PreviewFragment fragment = new PreviewFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public PreviewFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.preview_layout, container,
				false);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((PreviewActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	/**
	 * Enables the preview image, first by trying to decode the URI natively
	 * into a bitmap If this fails then the image will be loaded from the uri
	 * handled by the system
	 * 
	 * @param cameraFileUri
	 *            - path to the image
	 * @param previewActivity
	 */
	public void setPreviewImageOn(Uri cameraFileUri,
			PreviewActivity previewActivity) {
		System.out.println("setPreviewImageOn_start");
		System.out.println("setPreviewImageOn_1.0");
		System.out.println("setPreviewImageOn_1.1");
		ImageView image = (ImageView) previewActivity
				.findViewById(R.id.Preview_Image_Image);

		System.out.println("setPreviewImageOn_1");
		// sets TextView to invisible
		image.setVisibility(View.VISIBLE);
		System.out.println("setPreviewImageOn_2.1");
		try {
			Bitmap b = Utils.getAppFriendlyBitmap(cameraFileUri, getActivity()
					.getContentResolver());
			System.out.println("setPreviewImageOn_2");
			if (b == null) {
				throw new IOException("Bitmap returned is null");
			}
			System.out.println("setPreviewImageOn_3");
			image.setImageBitmap(b);
		} catch (IOException e) {
			Log.e(toString(), "bitmap failed to decode : " + e);
			image.setImageURI(cameraFileUri);
			int ih = image.getMeasuredHeight();// height of imageView
			int iw = image.getMeasuredWidth();// width of imageView
			int iH = image.getDrawable().getIntrinsicHeight();// original height
																// of underlying
																// image
			int iW = image.getDrawable().getIntrinsicWidth();// original width
																// of underlying
																// image
			Log.d(toString(),
					String.format("ih: %d iw:%d iH: %d iW: %d", ih, iw, iH, iW));
			// image.setImageURI(cameraFileUri);

		}
		System.out.println("setPreviewImageOn_finish");
	}

	public void setLocationPreview(SubmissionEventBuilder submissionEventBuilder) {
		System.out.println("setLocationPreview_start");
		TextView location = (TextView) getActivity().findViewById(
				R.id.PreviewLocationText);
		LatLng latlng = submissionEventBuilder.getGeoCoordinates();
		if (latlng != null) { // try and set geo coordinate location first
			double lat = latlng.latitude;
			double lon = latlng.longitude;
			location.setText(String.format("%s %.2f, %.2f",
					getString(R.string.geocoordinates_text), lat, lon));
		}

		else {
			// otherwise set address
			location.setText(submissionEventBuilder.getAddress());
			// location.setText("16 Kepler Way");
			// location.setOnClickListener(new
			// Utils.StartNextActivityEventOnClickListener(this,
			// RecordLocationActivity.class));
		}
		System.out.println("setLocationPreview_finish");
	}

	public void setDiscriptionPreview(
			SubmissionEventBuilder submissionEventBuilder) {
		System.out.println("setDiscriptionPreview_start");
		TextView description = (TextView) getActivity().findViewById(
				R.id.PreviewDescriptionText);
		description.setMovementMethod(new ScrollingMovementMethod());
		String descriptionText = submissionEventBuilder.getImageDescription();
		if (descriptionText.length() > maxLength)
			descriptionText = descriptionText.substring(0, maxLength);
		description.setText(submissionEventBuilder.getImageDescription());
		System.out.println("setDiscriptionPreview_finish");
	}

	public void setTagPreview(SubmissionEventBuilder submissionEventBuilder) {
		System.out.println("setTagPreview_start");
		TextView tag = (TextView) getActivity().findViewById(
				R.id.PreviewImageTag);

		StringBuffer st = new StringBuffer();

		for (String s : submissionEventBuilder.getImageTag()) {
			if (s != null) {
				st.append(s);
				st.append(", ");
			}
		}

		String text = st.toString();
		if (text.length() > 0)
			text = text.substring(0, text.length() - 2);
		System.out.println("Text value is   " + text);
		String ntext = text.substring(0, text.length());
		tag.setText(ntext);

		tag.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(
				getActivity(), DescriptionActivity.class));
		System.out.println("setTagPreview_finish");
	}
}
