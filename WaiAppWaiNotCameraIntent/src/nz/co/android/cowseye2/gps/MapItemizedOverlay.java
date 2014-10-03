package nz.co.android.cowseye2.gps;

import java.util.ArrayList;

import nz.co.android.cowseye2.gps.OnTapInterface;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public class MapItemizedOverlay extends ItemizedOverlay<OverlayItem>
{
    //member variables
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Context mContext;
    private int mTextSize;
    private OnTapInterface onTapStrategy;


    public MapItemizedOverlay(Drawable defaultMarker, Context context, int textSize, OnTapInterface onTapStrategy)
    {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
        mTextSize = textSize;
        this.onTapStrategy = onTapStrategy;
    }


    //In order for the populate() method to read each OverlayItem, it will make a request to createItem(int)
    // define this method to properly read from our ArrayList
    @Override
    protected OverlayItem createItem(int i)
    {
        return mOverlays.get(i);
    }


    @Override
    public int size()
    {
        return mOverlays.size();
    }

    @Override
    protected boolean onTap(int index)
    {
        OverlayItem item = mOverlays.get(index);
        if(item==null)
        	throw new NullPointerException("No marker found at the given index!");
        // uses strategy pattern
        return onTapStrategy.onTap(item, mContext);
    }

    @Override
    public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow)
    {
        super.draw(canvas, mapView, shadow);

        if (shadow == false)
        {
            //cycle through all overlays
            for (int index = 0; index < mOverlays.size(); index++)
            {
                OverlayItem item = mOverlays.get(index);

                // Converts lat/lng-Point to coordinates on the screen
                GeoPoint point = item.getPoint();
                Point ptScreenCoord = new Point() ;
                mapView.getProjection().toPixels(point, ptScreenCoord);
                //Paint
                Paint paint = new Paint();
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(mTextSize);
                Typeface tf = Typeface.create("Helvetica",Typeface.BOLD);
                paint.setTypeface(tf);
                boolean isSat = mapView.isSatellite();
                if(isSat)
                    paint.setARGB(200, 255, 255, 255); // alpha, r, g, b (White, semi see-through)
                else
                	paint.setARGB(200, 0, 0, 0); // alpha, r, g, b (Black, semi see-through)

                //show text to the right of the icon
                canvas.drawText(item.getTitle(), ptScreenCoord.x, ptScreenCoord.y+mTextSize, paint);
            }
        }
    }


    /** Adds an overlay but includes some extra information for the strategy object 
     * to deal with */
    public void addOverlayWithId(OverlayItem overlay, int friendID)
    {
        mOverlays.add(overlay);
        populate();
    }


    public void addOverlay(OverlayItem overlay)
    {
        mOverlays.add(overlay);
        populate();
    }


    public void removeOverlay(OverlayItem overlay)
    {
        mOverlays.remove(overlay);
        populate();
    }
    
    public void removeAllOverlays()
    {
    	mOverlays = new ArrayList<OverlayItem>();
    	populate();
    }


    public void clear()
    {
        mOverlays.clear();
        populate();
    }


}