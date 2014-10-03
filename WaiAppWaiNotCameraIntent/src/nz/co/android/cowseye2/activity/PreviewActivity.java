package nz.co.android.cowseye2.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.database.IncidentBuilder;
import nz.co.android.cowseye2.event.GetIncidentsEvent;
import nz.co.android.cowseye2.event.SubmissionEvent;
import nz.co.android.cowseye2.event.SubmissionEventBuilder;
import nz.co.android.cowseye2.event.SubmissionEventBuilderException;
import nz.co.android.cowseye2.fragments.NavigationDrawerFragment;
import nz.co.android.cowseye2.fragments.PreviewFragment;
import nz.co.android.cowseye2.service.GetIncidentsAsyncTask;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * The activity for showing a preview of the pollution event
 *
 * This will allow the user to see what they have done so far and to submit a
 * pollution event to the server
 *
 * @author Mitchell Lane (modified by Hamish Cundy, SYNERGY2, 2013)
 *
 */
public class PreviewActivity extends AbstractSubmissionActivity {

	// private TextView previewTextView;
	// private ListView tagslist;
	// private List <String> imageTags;
	private ProgressDialog progressDialog;
	private Handler handler;
	protected ProgressDialog pd;
	private Context con;
	private int timeCount;
	private Runnable r;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupDrawer();
		handler = new Handler();
		con = this;
		System.out.println("stetup_start");
		setupUI();
		System.out.println("stetup_finish");
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
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setTitle(getString(R.string.sending_incident_title));
		progressDialog.setMessage(getString(R.string.sending_incident_msg));

		// image.setOnClickListener(new
		// Utils.StartNextActivityEventOnClickListener(this,
		// SelectImageActivity.class));

		// description.setOnClickListener(new
		// Utils.StartNextActivityEventOnClickListener(this,
		// DescriptionActivity.class));

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// super.onCreateOptionsMenu(menu);
	// MenuItem item = menu.findItem(R.id.nextpage);
	// item.setVisible(false);
	// item.setEnabled(false);
	// return true;
	// }

	@Override
	protected void nextActivety() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Uri uri = Uri.parse(submissionEventBuilder.getImagePath().toString());
		Log.d(toString(), "onResume " + uri.toString());
		// Utils.RotateImageURI(image,uri);
		f.setPreviewImageOn(uri, this);
		f.setLocationPreview(submissionEventBuilder);
		f.setDiscriptionPreview(submissionEventBuilder);
		f.setTagPreview(submissionEventBuilder);
	}

	/**
	 * Submits a pollution event to the server
	 *
	 * @throws SubmissionEventBuilderException
	 *             if not enough data
	 */
	protected void submitPollutionEvent()
			throws SubmissionEventBuilderException {
		final SubmissionEvent currentEvent = submissionEventBuilder.build(); // - throws SubmissionEventBuilderException if not enough data
		
		IncidentBuilder ib = new IncidentBuilder();
		ib.setId(myApplication.getDatabaseAdapter().getAllIncidents().size());
		ib.setDescription(currentEvent.getImageDescription());
		ib.setImageUrl(currentEvent.getImagePath().getPath());
		ib.setLatitude((float)currentEvent.getGeoCoordinates().latitude);
		ib.setLocalImageUrl(currentEvent.getImagePath().getPath());
		ib.setLocalThumbnailUrl(currentEvent.getImagePath().getPath());
		ib.setLongitude((float)currentEvent.getGeoCoordinates().longitude);
		ib.setThumbnailUrl(currentEvent.getImagePath().getPath());
		ib.setPhysicalLocation(currentEvent.getAddress());
		myApplication.getDatabaseAdapter().insertIncident(ib.build());
		System.out.println(currentEvent.getImagePath().getPath()+" "+new File(currentEvent.getImagePath().getPath()).exists());


		/*
		if (myApplication.isOnline()) {
			progressDialog.show();

			new Thread(new Runnable() {
				@Override
				public void run() {
					final boolean success = RiverWatchApplication
							.processEventResponse(currentEvent.processRaw());
					handler.post(new Runnable() {

						@Override
						public void run() {
							Log.i(toString(),
									"successfully processed event? : "
											+ success);
							// only actually remove event if successful
							if (success) {
								Toast.makeText(
										PreviewActivity.this,
										getString(R.string.success_submission_msg),
										Toast.LENGTH_LONG).show();
								//myApplication.deleteImage(currentEvent);
								new GetIncidentsAsyncTask(
										new GetIncidentsEvent(myApplication, 0,
												50), myApplication).execute();
								// go back to starting activity
								Intent intent = new Intent(
										PreviewActivity.this,
										MainScreenActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
								// Finishes all previous activities on the
								// activity stack
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_SINGLE_TOP);
								startActivity(intent);
								finish();
							}
							// if unsuccessful stop event handling and move the
							// event to the end of the queue
							else {
								Toast.makeText(
										PreviewActivity.this,
										getString(R.string.failure_submission_msg),
										Toast.LENGTH_LONG).show();
							}
							progressDialog.dismiss();
						}
					});
				}
			}).start();
		} else {
			AlertDialog.Builder build = new AlertDialog.Builder(this);
			build.setMessage("Could not connect to internet. Your submission will be automatically submitted when you have internet coverage.");
			build.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							pd = ProgressDialog.show(con, "Caching image",
									"Saving submission data to cache..");

							new Thread(new Runnable() {
								@Override
								public void run() {
									// we now want to store the submission data
									// in the image (in case the app gets
									// killed)
									// then poll for Internet/set listener for
									// internet. Once submitted, delete image as
									// above
									try {

										ExifInterface exif = new ExifInterface(
												currentEvent.getImagePath()
														.getPath());
										LatLng coord = currentEvent
												.getGeoCoordinates();
										String store = coord.latitude
												+ " "
												+ coord.longitude
												+ "\n"
												+ currentEvent
														.getImageDescription()
												+ "\n";
										Log.d("pva", currentEvent
												.getImagePath().getPath()
												+ " "
												+ currentEvent.getImagePath());

										List<String> tags = currentEvent
												.getImageTag();

										for (String s : tags) {
											store = store + s + " ";
										}
										exif.setAttribute("UserComment", store);
										exif.saveAttributes();
										Log.d("Preview", "Stop stuff");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									startTimer();
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											pd.dismiss();
											Intent intent = new Intent(
													PreviewActivity.this,
													MainScreenActivity.class);
											// Finishes all previous activities
											// on the activity stack
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
													| Intent.FLAG_ACTIVITY_SINGLE_TOP);
											startActivity(intent);
											finish();
										}
									});

								}

							}).start();

						}
					});
			AlertDialog dialog = build.create();
			dialog.show();

		}*/

	}

	// sends the event to the server

	public void SubmitButton(View v) {
		try {
			// attempt to submit a pollution event
			submitPollutionEvent();
		} catch (SubmissionEventBuilderException e) {
			Toast.makeText(PreviewActivity.this,
					"You have not succesfully given all required information",
					Toast.LENGTH_LONG).show();
		}
	}

	private void startTimer() {
		timeCount = 1;
		// handler.post(r)

		r = new Runnable() {
			@Override
			public void run() {
				if (myApplication.isOnline()) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							File dir = PreviewActivity.this.getDir("",
									Context.MODE_WORLD_READABLE);
							String pathToDir = dir.getAbsolutePath();
							// Log.d("MainScreenAct", pathToDir);
							File[] fileNames = dir.listFiles();
							Log.d("MainScreenAct", pathToDir + " "
									+ fileNames.length);
							for (File f : fileNames) {
								ExifInterface exif = null;

								try {
									exif = new ExifInterface(f.getPath());
									Log.d("MSA",
											exif.getAttribute("UserComment")
													+ "");
									if (exif.getAttribute("UserComment") != null) {
										SubmissionEventBuilder build = SubmissionEventBuilder
												.getSubmissionEventBuilder(myApplication);

										Scanner sc = new Scanner(exif
												.getAttribute("UserComment"));

										LatLng coord = new LatLng(sc
												.nextDouble(), sc.nextDouble());
										build.setGeoCoordinates(coord);
										sc.nextLine();

										build.setImageDescription(sc.nextLine());
										List<String> tagList = new ArrayList<String>();
										while (sc.hasNext()) {
											tagList.add(sc.next());
										}

										build.setImageTag(tagList);
										build.setImagePath(Uri.parse(f
												.getPath()));

										final SubmissionEvent event = build
												.build();

										final boolean result = RiverWatchApplication
												.processEventResponse(event
														.processRaw());

										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												if (result == false) {

													Toast.makeText(
															getApplicationContext(),
															"Could not send cached submission. Will try again later",
															Toast.LENGTH_SHORT)
															.show();
												} else {
													//myApplication.deleteImage(event);
													Toast.makeText(
															getApplicationContext(),
															"Successfully sent cached submissions",
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										});
									}
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SubmissionEventBuilderException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

					}).start();

				} else {

					timeCount = timeCount * 2;
					Log.d("previewAct", "Internet unavailable, next try in "
							+ timeCount + "mins");
					handler.postDelayed(r, timeCount * 60 * 1000);
				}
			}
		};
		handler.post(r);
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
	private PreviewFragment f;

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments

		if (justopened) {
			justopened = false;

			FragmentManager fragmentManager = getSupportFragmentManager();
			f = PreviewFragment.newInstance(position);

			fragmentManager.beginTransaction()
					.replace(R.id.container, f, "tag_Select_Image_frag")
					.commit();

		} else {

			//myApplication.deleteImage(submissionEventBuilder.getImagePath().toString());

			Intent i = new Intent(PreviewActivity.this,
					MainScreenActivity.class);

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			i.putExtra("selectedItem", position);
			System.out.println("here0");
			startActivity(i);
			System.out.println("here1");
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

			// getMenuInflater().inflate(R.menu.main, menu);
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
