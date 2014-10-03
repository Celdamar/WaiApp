package nz.co.android.cowseye2.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class Utils {

	public static final int HTTP_OK = 200;
	public static final int HTTP_LOGIC_ERROR = 400;
	public static final int HTTP_SERVER_ERROR = 500;

	//
	public static final int MAX_PREVIEW_IMAGE_SIZE_WIDTH = 1600;
	public static final int MAX_PREVIEW_IMAGE_SIZE_HEIGHT = 1600;

	//	public static final int IMAGE_SIZE_WIDTH = 1200;
	//	public static final int IMAGE_SIZE_HEIGHT = 800;

	//	public static final int MAX_GALLERY_IMAGE_SIZE = 1600;


	//Camera Constants
	public static final File DIR_MEDIA_STORAGE = new File(Environment.getExternalStoragePublicDirectory(
			Environment.DIRECTORY_PICTURES), "RiverWatch");
	public static final String CAMERA_FILE_NAME_PREFIX = "RiverWatch_";

	/** Create a file Uri for saving an image or video */
	public static Uri getNewCameraFileUri() {
		String filename = CAMERA_FILE_NAME_PREFIX + new Date().getTime() + ".jpg";
		return Uri.fromFile(getOutputMediaFile(filename));
	}


	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(String filename) {
		// TODO: To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		// Create the storage directory if it does not exist
		if (!DIR_MEDIA_STORAGE.exists()){
			if (!DIR_MEDIA_STORAGE.mkdirs()){
				Log.e("DetailsActivity", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		File mediaFile = new File(DIR_MEDIA_STORAGE.getPath() + File.separator + filename);

		return mediaFile;
	}

	public static InputStream  getInputStream(Uri uri,ContentResolver contentResolver) throws IOException
	{
		InputStream inputStream = null;
		String scheme = uri.getScheme();
		if (scheme != null && scheme.equals("content") ) {
			inputStream = contentResolver.openInputStream(uri);
		} else {
			inputStream = new FileInputStream(uri.getPath());
		}
		return inputStream;
	}

	/**
	 * Playing around with full-size images -> OOMErrors. Bad.
	 * So, let's downscale them
	 * @param uri - holds the path to the bitmap
	 * @return the scaled-down bitmap.
	 * @throws IOException
	 */
	public static Bitmap getAppFriendlyBitmap(Uri uri, ContentResolver contentResolver) throws IOException {
		Bitmap b = null;
		InputStream inputStream = null;
		Cursor cur = null;
		try {
			//Decode image size
			Log.d("Utils","uri String: " + uri.toString());
			Log.d("Utils","uri Path: " + uri.getPath());
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			inputStream = getInputStream(uri,contentResolver);
			//BitmapFactory.decodeStream(inputStream, null, o);
			//BitmapFactory.decodeFile(uri.getPath(), o);
			inputStream.close();

			int scale = 1;
			//resize
			if (o.outHeight > MAX_PREVIEW_IMAGE_SIZE_HEIGHT || o.outWidth > MAX_PREVIEW_IMAGE_SIZE_WIDTH) {
				scale = (int)Math.pow(2, (int) Math.round(Math.log(MAX_PREVIEW_IMAGE_SIZE_WIDTH / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}
			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			inputStream = getInputStream(uri,contentResolver);
			System.gc();
			b = BitmapFactory.decodeStream(inputStream, null, o2);
			System.gc();
			//b = BitmapFactory.decodeFile(uri.getPath(), o2);;
			inputStream.close();
			System.gc();
			//re-orient
			float rotation = rotationForImage(uri);
			//			Log.d("UTILS", "rotation : "+rotation);
			if (rotation != 0f) {
				Matrix matrix = new Matrix();
				matrix.preRotate(rotation);
				System.gc();
				b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
						matrix, true);
			}
			else {
				String[] orientationColumn = {ImageColumns.ORIENTATION};
				cur = contentResolver.query(uri, orientationColumn, null, null, null);
				int orientation = -1;
				if (cur != null && cur.moveToFirst()) {
					orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
					//					Log.d("UTILS", "orientation : "+orientation);
					if (orientation > 0) {
						Matrix matrix = new Matrix();
						matrix.preRotate(orientation);
						b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
								b.getHeight(), matrix, true);
					}
				}
			}
		}
		finally {
			if (cur != null && !cur.isClosed()) {
				cur.close();
			}
		}
		return b;
	}
	/**
	 * Scales the bitmap
	 * @return the scaled-down bitmap.
	 * @throws IOException
	 */
	public static Bitmap scaleBitmap(InputStream inputStream, int scale) throws IOException {
		Bitmap b = null;

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		b = BitmapFactory.decodeStream(inputStream, null, o2);
		inputStream.close();

		return b;
	}

	/** Tries to return the rotation for an image if it has the associated EXIF data */
	public static float rotationForImage(Uri uri) {
		//if (uri.getScheme().equals("file")) {
			try {
				ExifInterface exif = new ExifInterface(uri.getPath());
				int rotation = (int)exifOrientationToDegrees(
						exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
								ExifInterface.ORIENTATION_NORMAL));
				return rotation;
			} catch (IOException e) {
				Log.e("Utils", "Error checking exif", e);
			}
		//}
		return 0f;
	}

	/* Converts ExifOrientation data to degrees */
	private static float exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	/** Goes back in the activity stack
	 *  Simply finishes the current activity */
	public static class BackEventOnClickListener implements OnClickListener{
		private Activity activity;

		/**
		 * @param a - Activity to finish upon click
		 */
		public BackEventOnClickListener(Activity a){
			this.activity = a;
		}
		@Override
		public void onClick(View v) {
			//clicking back so set the result to cancelled
			Intent intent=new Intent();
			activity.setResult(Activity.RESULT_CANCELED, intent);
			activity.finish();
		}
	}

	/** Finishes the current activity and starts a new intent to a new activity upon click */
	public static class StartNextActivityEventOnClickListener implements OnClickListener{
		private Activity activity;
		private Intent activityToStartIntent;

		/**
		 * @param a - Coming from this Activity
		 * @param activityToClass - Class of the activity to start
		 */
		public StartNextActivityEventOnClickListener(Activity a, Class<?> activityToClass){
			activityToStartIntent = constructActivityStartIntent(a, activityToClass);
			this.activity = a;
		}

		/**
		 * @param a - Coming from this Activity
		 * @param activityToStartIntent - Intent to start the new activity
		 */
		public StartNextActivityEventOnClickListener(Activity a, Intent activityToStartIntent){
			this.activityToStartIntent = activityToStartIntent;
			this.activity = a;
		}
		@Override
		public void onClick(View v) {
			activity.startActivity(activityToStartIntent);
		}
	}

	public static void RotateImageURI(ImageView imageview,Uri uri) {
		try {
			ExifInterface exif = new ExifInterface(uri.toString());
			double angle = 0.0;
			int orientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
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
			if (angle > 0.0) {
				//
				//imageview.setImageURI(uri);
				Matrix matrix=new Matrix();
				imageview.setScaleType(ScaleType.MATRIX);   //required
				//matrix.postRotate((float)angle,previewImageView.getMeasuredHeight()/2,previewImageView.getMeasuredWidth()/2);
                //int height = imageview.getHeight();
                //int width = imageview.getWidth();
    			int height=imageview.getDrawable().getIntrinsicHeight();//original height of underlying image
    			int width=imageview.getDrawable().getIntrinsicWidth();//original width of underlying image
                Log.d("Utils",String.format("Image width: %d height: %d",width,height));
				matrix.preRotate((float)angle,height/2,width/2);
				//matrix.postRotate((float)angle,0,0);
				imageview.setImageMatrix(matrix);
			}
		} catch (IOException e) {
			Log.e("Utils", "failed to find exif image data : "+e);
		}
		//sets preview text view to invisible
		imageview.setVisibility(View.VISIBLE);
		//imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageview.setImageURI(uri);
	}


	/**
	 * Helper method for creating an intent to go from one activity to another activity
	 * @param activityFrom - the activity you are coming from
	 * @param activityToClass - the class of the activity to go to
	 * @return
	 */
	public static Intent constructActivityStartIntent(Activity activityFrom, Class<?> activityToClass){
		return new Intent(activityFrom, activityToClass);
	}

}

class FlushedInputStream extends FilterInputStream {
    public FlushedInputStream(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public long skip(long n) throws IOException {
        long totalBytesSkipped = 0L;
        while (totalBytesSkipped < n) {
            long bytesSkipped = in.skip(n - totalBytesSkipped);
            if (bytesSkipped == 0L) {
                  int curbyte = read();
                  if (curbyte < 0) {
                      break;  // we reached EOF
                  } else {
                      bytesSkipped = 1; // we read one byte
                  }
           }
            totalBytesSkipped += bytesSkipped;
        }
        return totalBytesSkipped;
    }
}
