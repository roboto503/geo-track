package com.roboto503.geotrack;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class GeoTrackOverlays extends ItemizedOverlay<OverlayItem> {

	//variables
	private Context context;
	private List <OverlayItem> overlays; 
	
	/** creates a list of overlays*/
	public GeoTrackOverlays(Context context, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		overlays = new ArrayList<OverlayItem>();
	}//GeoTrackOverlays

	/** creates overlay item*/
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	/** returns the size of all overlays*/
	@Override
	public int size() {
		return overlays.size();
	}
	
	/** adds a new overlay to the list*/
	public void addOverlay(OverlayItem item){
		overlays.add(item);
		populate();
		
	}//addOverlay

}
