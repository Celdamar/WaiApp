package nz.co.android.cowseye2.activity;

import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.event.SubmissionEventBuilder;
import nz.co.android.cowseye2.fragments.NavigationDrawerFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This activity is the basis for all submission activity parts. This just
 * provides functionality for the shared components of a submission activity
 *
 * @author lanemitc
 *
 */
public abstract class AbstractSubmissionActivity extends ActionBarActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	protected RiverWatchApplication myApplication;
	protected SubmissionEventBuilder submissionEventBuilder;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.AppBaseTheme);
		myApplication = (RiverWatchApplication) getApplication();
		submissionEventBuilder = SubmissionEventBuilder
				.getSubmissionEventBuilder(myApplication);

	}

	/* Sets up the User Interface */
	protected void setupUI() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();// getSupportMenuInflater();
													// //

		// inflater.inflate(R.menu.top_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.nextpage) {
			nextActivety();
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	abstract protected void nextActivety();

	/** When the hardware back button gets pressed */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		setResult(RESULT_CANCELED, intent);
		finish();
	}

}
