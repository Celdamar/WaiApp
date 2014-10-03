package nz.co.android.cowseye2.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.activity.MainScreenActivity;
import nz.co.android.cowseye2.activity.IncidentGalleryActivity.ThumbnailClickListener;
import nz.co.android.cowseye2.database.Incident;
import nz.co.android.cowseye2.view.RiverWatchGallery;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;

public class IncidentGalleryFragment extends Fragment{
	
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
	
	
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static IncidentGalleryFragment newInstance(int sectionNumber) {
		IncidentGalleryFragment fragment = new IncidentGalleryFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public IncidentGalleryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.incident_gallery_layout, container, false);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainScreenActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));

		myApplication = (RiverWatchApplication) getActivity().getApplication();
		incidents = myApplication.getDatabaseAdapter().getAllIncidents();
	
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		//setContentView(R.layout.incident_gallery_layout);
		
		Intent intent = getActivity().getIntent();
		
		pageNumber = intent.getIntExtra("Page Number", 1);

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

	}

	private void setupUI() {
		myGallery = (RiverWatchGallery) (getActivity().findViewById(R.id.incident_gallery));

		myGallery.setupUI(myApplication, incidents);
		myGallery.setImageAdapterSelection(pageNumber);

	}


	private void setupThumbnails(){
		thumbnailsStarted = false;
		holdingThumbsLayout = (LinearLayout)
				getActivity().findViewById(R.id.holding_thumbs_linear_layout);
		thumbnailsLayout =
				(FrameLayout)getActivity().findViewById(R.id.holding_thumbs_frame_layout);
		scrollView = (ScrollView) getActivity().findViewById(R.id.thumbnails_scrollview);
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
			ImageButton image = new ImageButton(getActivity());
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
	
	
}