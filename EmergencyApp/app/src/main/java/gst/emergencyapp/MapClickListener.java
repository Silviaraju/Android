package gst.emergencyapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

	public interface MapClickListener
	{
		public void onMarkerClicked(double latitude, double longitude, Marker latlng);
		public void onMapClicked(double latitude, double longitude, LatLng latlng);
		public void onMapLongClicked(double latitude, double longitude, LatLng latlng);
	}
