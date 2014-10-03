package nz.co.android.cowseye2.fragments;

import java.io.IOException;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.activity.SelectImageActivity;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class SelectImageFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SelectImageFragment newInstance(int sectionNumber) {
		SelectImageFragment fragment = new SelectImageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public SelectImageFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.select_image_layout,
				container, false);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((SelectImageActivity) activity).onSectionAttached(getArguments()
				.getInt(ARG_SECTION_NUMBER));
	}

	public void addImage(Bitmap bitmap) {
		ImageView i = (ImageView) getActivity()
				.findViewById(R.id.preview_image);
		i.setVisibility(View.VISIBLE);
		i.setImageBitmap(bitmap);

	}

	public void setPreviewBitmapImageOn(Bitmap bitmap) {
		ImageView previewImageView = (ImageView) getActivity().findViewById(
				R.id.preview_image);

		TextView previewTextView = (TextView) getActivity().findViewById(
				R.id.preview_text);

		if (bitmap != null) {
			// sets preview text view to invisible
			previewTextView.setVisibility(View.INVISIBLE);
			// sets image to visible
			previewImageView.setVisibility(View.VISIBLE);
			// set background preview image to image taken
			previewImageView.setImageBitmap(bitmap);
		}

	}

	public void setPreviewURIImageOn(Uri uriToImage) {
		ImageView previewImageView = (ImageView) getActivity().findViewById(
				R.id.preview_image);

		TextView previewTextView = (TextView) getActivity().findViewById(
				R.id.preview_text);
		if (uriToImage != null) {
			// sets preview text view to invisible
			previewTextView.setVisibility(View.INVISIBLE);
			// sets image to visible
			previewImageView.setVisibility(View.VISIBLE);
			// set background preview image to image taken
			try {
				ExifInterface exif = new ExifInterface(uriToImage.toString());
				double angle = 0.0;
				int orientation = Integer.parseInt(exif
						.getAttribute(ExifInterface.TAG_ORIENTATION));
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					angle = 90.0;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					angle = 180.0;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					angle = 270.0;
					break;
				}

				if (orientation > 0.0) {
					Matrix matrix = new Matrix();
					previewImageView.setScaleType(ScaleType.MATRIX); // required
					int height = previewImageView.getHeight();
					int width = previewImageView.getWidth();

					// matrix.postRotate((float)angle,previewImageView.getMeasuredHeight()/2,previewImageView.getMeasuredWidth()/2);
					Log.d(toString(), String.format(
							"previewImageView width: %d height: %d", width,
							height));
					matrix.postRotate((float) angle, height / 2, width / 2);
					previewImageView.setImageMatrix(matrix);
				}
			} catch (IOException e) {
				Log.e(toString(), "failed to find exif image data : " + e);
			}
			previewImageView.setImageURI(uriToImage);

		}

	}
}
