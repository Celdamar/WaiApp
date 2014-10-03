package nz.co.android.cowseye2.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.fragments.DescriptionFragment;
import nz.co.android.cowseye2.fragments.NavigationDrawerFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * The activity for inputting the description for a pollution event
 *
 * This will allow the user to enter a description and select appropriate tags
 *
 * @author lanemitc
 *
 */
public class DescriptionActivity extends AbstractSubmissionActivity {

	private String imageDescription;
	private String imageTag;
	protected CharSequence[] _options = { "Cow", "Dog", "Goat", "Horse",
			"Litter", "Pollution", "River", "Sheep", "Stock" };
	protected boolean[] _selections = new boolean[_options.length];
	private List<String> imageTags;

	private List<String> tosendtags;
	private int numberOfSelections = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageTags = new ArrayList<String>();

		tosendtags = new ArrayList<String>();
		setupDrawer();
		setupUI();
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

	/* Sets up the UI */
	@Override
	protected void setupUI() {
		super.setupUI();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	@Override
	protected void nextActivety() {
		if (!f.hasDescription()) {
			Toast.makeText(DescriptionActivity.this,
					getString(R.string.pleaseEnterDescription),
					Toast.LENGTH_LONG).show();
		}

		// description has been entered and recognized by user and this
		// will move the application onto the recorfd location activity
		else {
			tosendtags.clear();
			for (int i = 0; i < _options.length; i++) {
				Log.i("ME", _options[i] + " selected: " + _selections[i]);
				if (_selections[i]) {
					tosendtags.add((String) _options[i]);
					numberOfSelections = numberOfSelections + 1; // used to keep
																	// count of
																	// if any
																	// tags are
																	// selected
																	// or not
				}
			}

			imageDescription = f.getText();
			submissionEventBuilder.setImageDescription(imageDescription);
			// checks if there has been any tags selected, else doesn't let the
			// user progress through
			if (numberOfSelections == 0) {
				Toast toast = Toast
						.makeText(DescriptionActivity.this,
								getString(R.string.pleaseChooseTags),
								Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.LEFT, 0,
						-15);
				toast.show();

			} else {
				//TODO remove this submissionEventBuilder.setGeoCoordinates call, only for testing.
				//submissionEventBuilder.setGeoCoordinates(new LatLng(10,10));
				Intent intent = new Intent(DescriptionActivity.this,
						RecordLocationActivity.class);	//TODO this is the correct one, other is just for testing
						//PreviewActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {

		System.out.println("Item was selected");
		// An item was selected. You can retrieve the selected item using
		imageTag = (String) parent.getItemAtPosition(pos);
		submissionEventBuilder.setImageTag(imageTags);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	public void SelectTagButtonClick(View view) {
		showDialog(0);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
				.setTitle("Image Tags")
				.setMultiChoiceItems(_options, _selections,
						new DialogSelectionClickHandler())
				.setPositiveButton("OK", new DialogButtonClickHandler())
				.create();
	}

	public class DialogSelectionClickHandler implements
			DialogInterface.OnMultiChoiceClickListener {
		@Override
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			Log.i("ME", _options[clicked] + " selected: " + selected);
		}
	}

	public class DialogButtonClickHandler implements
			DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int clicked) {
			switch (clicked) {
			case DialogInterface.BUTTON_POSITIVE:
				printSelectedImageTags();
				break;
			}
		}
	}

	protected void printSelectedImageTags() {
		for (int i = 0; i < _options.length; i++) {
			Log.i("ME", _options[i] + " selected: " + _selections[i]);
		}
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
	private DescriptionFragment f;

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments

		if (justopened) {
			justopened = false;

			FragmentManager fragmentManager = getSupportFragmentManager();
			f = DescriptionFragment.newInstance(position);

			fragmentManager.beginTransaction()
					.replace(R.id.container, f, "tag_Select_Image_frag")
					.commit();

		} else {

			//myApplication.deleteImage(submissionEventBuilder.getImagePath().toString()); //TODO un-comment when submission goes to the home page not having to back out. 

			Intent i = new Intent(DescriptionActivity.this,
					MainScreenActivity.class);

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

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
