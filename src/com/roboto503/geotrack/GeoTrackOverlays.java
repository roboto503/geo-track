package com.roboto503.geotrack;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class GeoTrackOverlays extends ItemizedOverlay<OverlayItem> {

	private Context context;
	private List <OverlayItem> overlays; 
	
	public GeoTrackOverlays(Context context, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		overlays = new ArrayList<OverlayItem>();
	}//GeoTrackOverlays

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	public void addOverlay(OverlayItem item){
		overlays.add(item);
		populate();
		
	}//addOverlay

}
