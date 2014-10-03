package nz.co.android.cowseye2.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.common.Constants;
import nz.co.android.cowseye2.view.Preview;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * This is used for natively taking a picture via the camera on the phone or tablet
 * @author Mitchell Lane
 *
 */
public class TakePictureActivity  extends ActionBarActivity {

	//private Button backButton;
	private Button captureButton;
	private Preview preview;
	private boolean pictureTaken = false;
	private Display display;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.take_picture);
		Bundle extras = getIntent().getExtras();
		//		myApplication = getApplication();
		setupUI();
	}

	public void setupUI(){
		captureButton = (Button)findViewById(R.id.capture_image_button);
		//backButton = (Button)findViewById(R.id.backButton);


		display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		preview = new Preview(this,display);
		FrameLayout previewFrameLayout = ((FrameLayout) findViewById(R.id.previewFrameLayout));
		previewFrameLayout.addView(preview);
		captureButton.setOnClickListener(new CaptureOnClickListener());
//		backButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				endActivityUnsuccessfully();
//			}
//		});
	}


	private class CaptureOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if(!pictureTaken){
				pictureTaken = true;
				preview.camera.takePicture(shutterCallback, null,null, jpegCallback);
			}
		}
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
			Log.d(toString(), "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				String pathname = null;
				//boolean rotateBitmap = true;
				int rotateAmount = 0;
				int curRotate = preview.getCurDisplayOrientation();

				switch (curRotate) {
				//case 0: rotateBitmap = false; break;
				case 90: rotateAmount = ExifInterface.ORIENTATION_ROTATE_90; break;
				case 180: rotateAmount = ExifInterface.ORIENTATION_ROTATE_180; break;
				case 270: rotateAmount = ExifInterface.ORIENTATION_ROTATE_270; break;
				}

				pathname = savePictureToDisk(data);
				if(pathname==null) {
					endActivityUnsuccessfully();
					return;
				}
				//if(rotateBitmap){
					ExifInterface exif = new ExifInterface(pathname);
					exif.setAttribute(ExifInterface.TAG_ORIENTATION,String.valueOf(rotateAmount));
					try {
						exif.saveAttributes();
					} catch (Exception e) {
						e.printStackTrace();
					}
				//}
				pictureTaken = false;
		        endActivitySuccessfully(pathname);
			}
			catch(IOException e){
				Log.e(toString(), "IOException : " +e);
				endActivityUnsuccessfully();
			}
		}

		private String saveBitmapToDisk(Bitmap rotatedBitmap) throws IOException {
			try{
				final long num = System.currentTimeMillis();
				final String ID = getString(R.string.app_name).replaceAll("\\s", "") +num;
				File dir = TakePictureActivity.this.getDir("", Context.MODE_WORLD_READABLE);
				String pathToDir = dir.getAbsolutePath();
				final String pathName = pathToDir + File.separator+ ID;
				FileOutputStream out = new FileOutputStream(pathName);
				rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
				return pathName;
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			throw new IOException("Could not create file or could not write to created file");
		}

		private String savePictureToDisk(byte[] data) throws IOException{
			FileOutputStream outStream = null;
			try {
				// write to local file system
				final long num = System.currentTimeMillis();
				final String ID = getString(R.string.app_name).replaceAll("\\s", "") +num;
				File dir = TakePictureActivity.this.getDir("", Context.MODE_WORLD_READABLE);
				String pathToDir = dir.getAbsolutePath();
				final String pathName = pathToDir + File.separator+ ID;
				Log.d(toString(),"Picture Path: " + pathName);
				outStream = new FileOutputStream(String.format(
						"%s.jpg", pathName));
				outStream.write(data);
				outStream.close();
				Log.d(toString(), "onPictureTaken - wrote bytes: " + data.length);
				return String.format("%s.jpg", pathName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			throw new IOException("Could not create file or could not write to created file");
		}
	};

	private void endActivitySuccessfully(String filePathName) {
		Log.d(toString(), "endActivitySuccessfully");
		Intent i = new Intent();
		i.putExtra(Constants.IMAGE_URI_KEY, filePathName);
		setResult(RESULT_OK, i);
		finish();
	}

	private void endActivityUnsuccessfully() {
		Log.d(toString(), "endActivityUnsuccessfully");
		Intent i = new Intent();
		setResult(RESULT_CANCELED, i);
		finish();
	}

	@Override
	public void onBackPressed() {
		endActivityUnsuccessfully();
	}


}

