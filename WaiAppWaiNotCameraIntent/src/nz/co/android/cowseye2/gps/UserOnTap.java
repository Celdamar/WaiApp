package nz.co.android.cowseye2.gps;

import nz.co.android.cowseye2.R;
import android.content.Context;
import android.widget.Toast;

import com.google.android.maps.OverlayItem;

/**
 * Responds to a tap on the user's location
 * @author lanemitc
 *
 */
public class UserOnTap implements OnTapInterface{

	private Context context;

	public UserOnTap(Context mainActivityContext) {
		this.context = mainActivityContext;
	}

	@Override
	public boolean onTap(OverlayItem marker, Context ctx) {
		Toast.makeText(ctx, R.string.thisIsTheLocationFound, Toast.LENGTH_SHORT).show();
        //return true to indicate we've taken care of it
        return true;
	}

}
