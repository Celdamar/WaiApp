package nz.co.android.cowseye2.utility;


import nz.co.android.cowseye2.R;
import nz.co.android.cowseye2.RiverWatchApplication;
import nz.co.android.cowseye2.activity.RecordLocationActivity;
import nz.co.android.cowseye2.gps.MapManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.WindowManager.BadTokenException;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class AlertBuilder {

	public static AlertDialog buildAlertMessageNoInternet(final Context context) {
		//Activity transfer to wifi settings

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getResources().getString(R.string.no_internet_message))
		.setCancelable(false)
		.setPositiveButton(context.getResources().getString(R.string.positive_button_title), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.negative_button_title), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		return builder.create();
	}

	public static AlertDialog buildGPSAlertMessage(final Context context, final boolean fromSubmission) {
		String message = context.getResources().getString(R.string.gps_message);
		if(fromSubmission)
			message = context.getResources().getString(R.string.gps_message_submission);
		//Activity transfer to GPS settings
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(context.getResources().getString(R.string.positive_button_title), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.negative_button_title), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		return builder.create();
	}

	public static AlertDialog buildAlertMessageUpdatePosition(final RecordLocationActivity locationActivity, final MapManager mapHelper, final Context context, final LatLng userLatLng) {
		try{
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getResources().getString(R.string.newLocationFound) +"\n"+context.getResources().getString(R.string.wouldYouLikeToUpdate))
		.setCancelable(false)
		.setPositiveButton(context.getResources().getString(R.string.positive_button_title), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				locationActivity.setAddress(userLatLng);
				//mapHelper.drawUserPosition(userLatLng);
				mapHelper.setMapViewToLocation(userLatLng);
				dialog.cancel();
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.negative_button_title), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		return builder.create();
		}
		catch(BadTokenException e){
			//view has been destroyed
			Log.e("AlertBuilder", "Trying to alert user of new location : "+e);
		}
		return null;

	}

	/** Builds a dialog where the user has to input the correct pin to launch an intent*/
	public static AlertDialog buildServerPrompt(final Context context) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Enter Server location");
		final EditText input = new EditText(context);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);
		builder.setCancelable(true)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				RiverWatchApplication.server_path = input.getText().toString().trim();
				RiverWatchApplication.submission_path = RiverWatchApplication.server_path + "/submit/";
			}
		})
		.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		return builder.create();

	}

}
