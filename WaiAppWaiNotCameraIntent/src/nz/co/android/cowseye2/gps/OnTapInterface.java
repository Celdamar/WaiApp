package nz.co.android.cowseye2.gps;

import com.google.android.maps.OverlayItem;

import android.content.Context;

/**
 * Does something when a marker is tapped - uses the strategy pattern 
 * @author lanemitc
 *
 */
public interface OnTapInterface {

	/** Does something when a marker is tapped 
	 *@param marker - the marker overlay that was tapped
	 *@param ctx - the context this is from
	 *@return true if the tap was dealed with, otherwise falses 
	 */
    boolean onTap(OverlayItem marker, Context ctx);
}
