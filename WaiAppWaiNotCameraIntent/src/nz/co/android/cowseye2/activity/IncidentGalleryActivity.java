package nz.co.android.cowseye2.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.database.Incident;
import nz.co.android.cowseye2.fragments.GridIncidentGalleryFragment;
import nz.co.android.cowseye2.fragments.HomeFragment;
import nz.co.android.cowseye2.fragments.IncidentGalleryFragment;
import nz.co.android.cowseye2.fragments.MainViewFragment;
import nz.co.android.cowseye2.fragments.NavigationDrawerFragment;
import nz.co.android.cowseye2.fragments.WaterReadingFragment;
import nz.co.android.cowseye2.view.RiverWatchGallery;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class IncidentGalleryActivity extends ActionBarActivity implements
NavigationDrawerFragment.NavigationDrawerCallbacks {

	private Button backButton;
	private RiverWatchGallery myGallery;
	private RiverWatchApplication myApplication;

	private FrameLayout thumbnailsLayout; // this is the layout that will contain the thumbnails of the pages
	private LinearLayout holdingThumbsLayout;
	private ScrollView scrollView; // the scrollview for containing the thumbnails
	private boolean thumbnailLayoutOrigami = false;
	private boolean thumbnailsStarted = false;
	private ArrayList<ImageButton> thumbnails;
	private int currentPosition = 0;
	public static Matrix skewUpMatrix;
	public static Matrix skewDownMatrix;
	public static RectF imageRectangleSkewedUp;
	public static RectF imageRectangleSkewedDown;

	private static final float THUMBNAIL_SKEW = 0.3f;
	private static final int THUMBNAIL_WIDTH = 100;
	private static final int THUMBNAIL_HEIGHT = 75;
	private static final float THUMBNAIL_SCALEX = 0.8f;
	private static final float THUMBNAIL_SCALEY = 0.8f;
	public static final int UNSELECTED_ALPHA = 160;
	public static final int SELECTED_ALPHA = 255;

	private int pageNumber;

	private List<Incident> incidents;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incident_gallery_layout);
		myApplication = (RiverWatchApplication) getApplication();
		Intent intent = getIntent();
		pageNumber = intent.getIntExtra("Page Number", 1);
		incidents = myApplication.getDatabaseAdapter().getAllIncidents();

		setupUI();

		System.out.println(incidents==null?"null":incidents.size());
		for(Incident i : incidents){
			System.out.println(i.getImageUrl());
			System.out.println(new File(i.getImageUrl()).exists());
		}

		 setupThumbnails();
		 constructMatrices();
		 constructImageRectangleBounds();
		 redrawThumbnails();

	}

	private void setupUI() {
		myGallery = (RiverWatchGallery) (findViewById(R.id.incident_gallery));

		myGallery.setupUI(myApplication, incidents);
		myGallery.setImageAdapterSelection(pageNumber);

	}


	private void setupThumbnails(){
		thumbnailsStarted = false;
		holdingThumbsLayout = (LinearLayout)
				findViewById(R.id.holding_thumbs_linear_layout);
		thumbnailsLayout =
				(FrameLayout)findViewById(R.id.holding_thumbs_frame_layout);
		scrollView = (ScrollView) findViewById(R.id.thumbnails_scrollview);
		thumbnails = new ArrayList<ImageButton>();
	}

	/* Constructs the matrices for skewing the thumbnails */
	private void constructMatrices(){
		skewUpMatrix = new Matrix();
		skewUpMatrix.postSkew(0f, THUMBNAIL_SKEW*-1, THUMBNAIL_WIDTH/2,0);
		skewUpMatrix.postScale(THUMBNAIL_SCALEX, THUMBNAIL_SCALEY,0,
				THUMBNAIL_HEIGHT);
		skewDownMatrix = new Matrix();
		skewDownMatrix.postSkew(0f, THUMBNAIL_SKEW*1, THUMBNAIL_WIDTH/2,0);
		skewDownMatrix.postScale(THUMBNAIL_SCALEX, THUMBNAIL_SCALEY,0,
				THUMBNAIL_HEIGHT);
	}

	/* Constructs the rectangles for the bounds of the image after skewing */
	private void constructImageRectangleBounds(){
		imageRectangleSkewedUp = new RectF(0, 0,
				THUMBNAIL_WIDTH,THUMBNAIL_HEIGHT);
		skewUpMatrix.mapRect(imageRectangleSkewedUp);
		imageRectangleSkewedDown= new RectF(0, 0,
				THUMBNAIL_WIDTH,THUMBNAIL_HEIGHT);
		skewDownMatrix.mapRect(imageRectangleSkewedDown);
	}

	/** Sets the selected page to the given thumbnail position */
	public void setThumbnailSelection(int pos){
		if(pos!=currentPosition ){
			if(thumbnails!=null && thumbnails.size()>0 && thumbnails.size()>pos){
				//set alpha on old thumbnail and remove color filter
				ImageButton oldImage = thumbnails.get(currentPosition);
				oldImage.setAlpha(UNSELECTED_ALPHA);
				oldImage.setBackgroundColor(0000);
				currentPosition = pos;
				//set alpha on new thumbnail and put on color filter
				ImageButton newImage = thumbnails.get(currentPosition);
				newImage.setBackgroundColor(Color.BLACK);
				newImage.setAlpha(SELECTED_ALPHA);

				repositionScrollView(newImage);
			}
		}
	}
	/* Repositions the scroll view to fit the new image selected */
	private void repositionScrollView(ImageButton newImage) {
		//TODO use layoutparams
		// double edgeImage = newImage.getX();
		// double edgeScroll = bhsv.getScrollX();
		//
		// // move scrollview right if image is offscreen on the left
		// if(edgeImage < edgeScroll)
		// bhsv.scrollTo((int) edgeImage, 0);
		// edgeImage = newImage.getX()+newImage.getDrawable().getIntrinsicWidth();
		// edgeScroll = bhsv.getScrollX()+bhsv.getWidth();
		// // move scrollview left if image is offscreen on the right
		// if(edgeImage > edgeScroll){
		// bhsv.scrollTo((int) edgeImage-bhsv.getWidth(), 0);
		// }
	}

	/** Deletes all the thumnbnails in the content layout */
	private void resetThumbnails(){
		thumbnailsLayout.removeAllViews();
		thumbnails = new ArrayList<ImageButton>();
	}

	public void redrawThumbnails() {
		if(thumbnails==null)
			resetThumbnails();
		int maxHeight = 0;
		//x coordinate of the last thumbnail placed
		float lastX = 0f;
		for(int pos = 0; pos < incidents.size(); pos++){
			Log.d(toString(), "thumnbnail at pos: "+pos);
			//create image
			ImageButton image = new ImageButton(this);
			int wd;
			int ht;
			image.setImageResource(R.drawable.default_thumb);
			image.setOnClickListener(new ThumbnailClickListener(pos));
			if(thumbnailLayoutOrigami){
				image.setPadding(0, 0, 0, 0);
				image.setScaleType(ScaleType.MATRIX);

				boolean skewUp = (pos%2==0)? true : false;
				image.setImageMatrix((skewUp)? skewUpMatrix : skewDownMatrix);
				wd = (int) ((skewUp)? imageRectangleSkewedUp.width() :
					imageRectangleSkewedDown.width());
				ht = (int) ((skewUp)? imageRectangleSkewedUp.height()*1.5 :
					imageRectangleSkewedDown.height()*1.5);
				if(ht > maxHeight)
					maxHeight = ht;
			}
			else{
				wd = image.getDrawable().getIntrinsicWidth();
				ht = image.getDrawable().getIntrinsicHeight();
				if(ht > maxHeight)
					maxHeight = ht;
				lastX+=15;
			}
			image.setBackgroundDrawable(null);
			if(pos!=currentPosition)
				image.setAlpha(UNSELECTED_ALPHA);
			else
				image.setAlpha(SELECTED_ALPHA);
			// image.setX(lastX);
			FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(wd, ht);
			flp.setMargins((int)lastX, 0, 0, 0);
			image.setLayoutParams(flp);
			thumbnails.add(image);
			holdingThumbsLayout.addView(image);
			// thumbnailsLayout.addView(image,lp);
			lastX+=wd;
		}
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) lastX,
				maxHeight);
		thumbnailsLayout.setLayoutParams(lp);
	}

	public void setThumbnailsLayout(FrameLayout layout){
		thumbnailsLayout = layout;
		holdingThumbsLayout.addView(thumbnailsLayout);
	}

	/* Class for dealing with a click on the given page thumbnail */
	public class ThumbnailClickListener implements OnClickListener {

		private final int position;

		public ThumbnailClickListener(int position) {
			this.position = position;
		}

		public void onClick(View v) {
			if (position != currentPosition) {
				// setThumbnailSelection(position);
				myGallery.setImageAdapterSelection(position);
			}
		}
	}
	
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private boolean justopened = true;
	private IncidentGalleryFragment f;
	private GridIncidentGalleryFragment f2;

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments

		if (justopened) {
			justopened = false;

			FragmentManager fragmentManager = getSupportFragmentManager();
			f = IncidentGalleryFragment.newInstance(position);

			fragmentManager.beginTransaction()
					.replace(R.id.container, f, "incident_gallery_frag")
					.commit();

		} else {

			//myApplication.deleteImage(submissionEventBuilder.getImagePath().toString()); //TODO un-comment when submission goes to the home page not having to back out. 

			FragmentManager fragmentManager = getSupportFragmentManager();
			f2 = GridIncidentGalleryFragment.newInstance(position);

			fragmentManager.beginTransaction()
					.replace(R.id.container, f, "grid_incident_gallery_frag")
					.commit();
		}
	}

}
