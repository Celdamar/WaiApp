package nz.co.android.cowseye2.activity;

import java.io.IOException;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.common.Constants;
import nz.co.android.cowseye2.fragments.NavigationDrawerFragment;
import nz.co.android.cowseye2.fragments.SelectImageFragment;
import nz.co.android.cowseye2.utility.AlertBuilder;
import nz.co.android.cowseye2.utility.Utils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * The activity for selecting an image for a pollution event submission
 * 
 * This will allow the user to either capture a new image or select an image
 * from the gallery
 * 
 * @author lanemitc
 * 
 */
public class SelectImageActivity extends AbstractSubmissionActivity {

	private Uri cameraFileUri = null; // holds path to the image taken or
										// retrieved

	private boolean fromGallery;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setupDrawer();
		setupUI();
		loadState(savedInstanceState);
		// starts a new submission event
		submissionEventBuilder.startNewSubmissionEvent();
	}

	private void setupDrawer() {
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		mNavigationDrawerFragment.showDrawerToggle(false);
	}

	/* Sets up the User Interface */
	@Override
	protected void setupUI() {
		super.setupUI();
		// If we have GPS disabled then ask to activate it
		if (!myApplication.isGPSEnabled()) {
			AlertBuilder.buildGPSAlertMessage(SelectImageActivity.this, true)
					.show();
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(R.string.select_image_title);
	}

	public void CaptureImage(View v) {
		startActivityForResult(new Intent(SelectImageActivity.this,
				TakePictureActivity.class), Constants.REQUEST_CODE_TAKE_PICTURE);

	}

	// we do not want to get picture from the gallery

	// public void retriveGallery(View v) {
	// // open gallery
	// // dealt with at onActivityResult()
	// retrieveImageFromGallery();
	// }

	@Override
	protected void nextActivety() {
		// goes to the description activity
		if (cameraFileUri != null) {
			// save the image URI to the submissionEventBuilder
			submissionEventBuilder.setImagePath(cameraFileUri);
			submissionEventBuilder.setFromGallery(fromGallery);
			// start description activity
			startActivity(new Intent(SelectImageActivity.this,
					RecordLocationActivity.class));
			// Toast.makeText(SelectImageActivity.this,
			// getString(R.string.saving_image), Toast.LENGTH_LONG).show();
		} else
			Toast.makeText(SelectImageActivity.this,
					getString(R.string.please_select_a_image),
					Toast.LENGTH_LONG).show();
	}

	/**
	 * Loads any state back in Loads the path of the image if taken
	 */
	private void loadState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(Constants.IMAGE_URI_KEY)) {
				cameraFileUri = Uri.parse(savedInstanceState
						.getString(Constants.IMAGE_URI_KEY));
				setPreviewImageOn(cameraFileUri);
			}
			fromGallery = savedInstanceState
					.getBoolean(Constants.FROM_GALLERY_KEY);
		}
	}

	/** Save the camera file URI if we have taken a picture */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (cameraFileUri != null && !cameraFileUri.equals(""))
			outState.putString(Constants.IMAGE_URI_KEY,
					cameraFileUri.toString());
		outState.putBoolean(Constants.FROM_GALLERY_KEY, fromGallery);

	}

	// /** Creates an intents to open the camera application and initiates it */
	// protected void takeImageWithCamera() {
	// // create Intent to take a picture and return control to the calling
	// // application
	// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// // create a file to save the image
	// cameraFileUri = Utils.getNewCameraFileUri();
	// // set the image file name
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
	// // trigger activity
	// startActivityForResult(intent, Constants.REQUEST_CODE_CAMERA);
	// }

	/** Makes an intent to retrieve an image from the gallery */
	protected void retrieveImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// check if error here

		// Coming from capturing an image from native activity
		if (requestCode == Constants.REQUEST_CODE_TAKE_PICTURE
				&& resultCode == Activity.RESULT_OK) {
			if (data == null || data.getData() == null) {
				// Picture has been taken natively, get the path from activity
				cameraFileUri = Uri.parse(data
						.getStringExtra(Constants.IMAGE_URI_KEY));
				setPreviewImageOn(cameraFileUri);
				fromGallery = false;
			}
		}
		// removed this option, we only want a picture taken with the
		// application
		// Coming from gallery
		if (requestCode == Constants.REQUEST_CODE_GALLERY
				&& resultCode == Activity.RESULT_OK) {
			if (data != null) {
				cameraFileUri = data.getData();
				setPreviewImageOn(cameraFileUri);
				fromGallery = true;
			}
		}
		Log.i(toString(), "cameraFileUri : " + cameraFileUri);
	}

	/**
	 * Enables the preview image, first by trying to decode the URI natively
	 * into a bitmap If this fails then the image will be loaded from the uri
	 * handled by the system
	 * 
	 * @param cameraFileUri
	 *            - path to the image
	 */
	private void setPreviewImageOn(Uri cameraFileUri) {
		Log.d(toString(), "setPreviewImageOn: ");

		try {
			Bitmap b = Utils.getAppFriendlyBitmap(cameraFileUri,
					getContentResolver());
			Log.d(toString(), "bitt..  " + b);

			if (b == null)
				throw new IOException("Bitmap returned is null");
			f.setPreviewBitmapImageOn(b);
		} catch (IOException e) {
			Log.e(toString(), "bitmap failed to decode : " + e);
			f.setPreviewURIImageOn(cameraFileUri);
		}
	}

	// /**
	// * Enables the preview image
	// *
	// * @param uriToImage
	// * - URI to the image captured or selected
	// */
	// private void setPreviewURIImageOn(Uri uriToImage) {
	// if (uriToImage != null) {
	// // sets preview text view to invisible
	// previewTextView.setVisibility(View.INVISIBLE);
	// // sets image to visible
	// previewImageView.setVisibility(View.VISIBLE);
	// // set background preview image to image taken
	// try {
	// ExifInterface exif = new ExifInterface(uriToImage.toString());
	// double angle = 0.0;
	// int orientation = Integer.parseInt(exif
	// .getAttribute(ExifInterface.TAG_ORIENTATION));
	// switch (orientation) {
	// case ExifInterface.ORIENTATION_ROTATE_90:
	// angle = 90.0;
	// break;
	// case ExifInterface.ORIENTATION_ROTATE_180:
	// angle = 180.0;
	// break;
	// case ExifInterface.ORIENTATION_ROTATE_270:
	// angle = 270.0;
	// break;
	// }
	//
	// if (orientation > 0.0) {
	// Matrix matrix = new Matrix();
	// previewImageView.setScaleType(ScaleType.MATRIX); // required
	// int height = previewImageView.getHeight();
	// int width = previewImageView.getWidth();
	//
	// //
	// matrix.postRotate((float)angle,previewImageView.getMeasuredHeight()/2,previewImageView.getMeasuredWidth()/2);
	// Log.d(toString(), String.format(
	// "previewImageView width: %d height: %d", width,
	// height));
	// matrix.postRotate((float) angle, height / 2, width / 2);
	// previewImageView.setImageMatrix(matrix);
	// }
	// } catch (IOException e) {
	// Log.e(toString(), "failed to find exif image data : " + e);
	// }
	// previewImageView.setImageURI(uriToImage);
	//
	// }
	//
	// }
	//
	// /**
	// * Enables the preview image
	// *
	// * @param bitmap
	// * - the image bitmap
	// */
	// private void setPreviewBitmapImageOn(Bitmap bitmap) {
	// if (bitmap != null) {
	// // sets preview text view to invisible
	// previewTextView.setVisibility(View.INVISIBLE);
	// // sets image to visible
	// previewImageView.setVisibility(View.VISIBLE);
	// // set background preview image to image taken
	// previewImageView.setImageBitmap(bitmap);
	// }
	//
	// }

	@Override
	public void onBackPressed() {
		if (cameraFileUri != null) {
			String imagePath = cameraFileUri.toString();
			// delete image
			if (imagePath != null && !imagePath.equals("")) {
				//myApplication.deleteImage(imagePath);
			}
		}
		super.onBackPressed();
	}

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private boolean justopened = true;
	private SelectImageFragment f;

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		if (justopened == true) {
			justopened = false;
			FragmentManager fragmentManager = getSupportFragmentManager();
			f = SelectImageFragment.newInstance(position);

			fragmentManager.beginTransaction()
					.replace(R.id.container, f, "tag_Select_Image_frag")
					.commit();
		} else {
			if (cameraFileUri != null) {
				String imagePath = cameraFileUri.toString();
				// delete image
				if (imagePath != null && !imagePath.equals("")) {
					//myApplication.deleteImage(imagePath);
				}
			}
			Intent i = new Intent(SelectImageActivity.this,
					MainScreenActivity.class);

			i.setFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);

			i.putExtra("selectedItem", position);

			startActivity(i);
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.

			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
