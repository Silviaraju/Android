package gst.emergencyapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import android.os.Handler;

import static gst.emergencyapp.R.layout.map;

public class CustomMap extends Fragment{
	private GoogleMap googleMap;
	MapClickListener mapClickListener;
	MapView mMapView;
	EditText editText;
	Handler handler;
	Runnable r;
	Circle c;
	Button button;
	ArrayList<String> users;
	SmsManager smsManager;
	public CustomMap() {
	}

	public void setClickListeners(MapClickListener mapClickListener ){
		this.mapClickListener=mapClickListener;
	}

	public void readAllUsers(){
		users=new ArrayList<>();
		String SQLQUERY="select * from users";
		JDBCRunner loginJdbcRunner=new JDBCRunner(getActivity().getApplicationContext(), SQLQUERY, commondatas.HOSTIP, commondatas.DBNAME, commondatas.DBUSERNAME, commondatas.DBPASSWORD, true, new AsyncFunction() {

			@Override
			public void preExecute() {
				// TODO Auto-generated method stub

			}

			@Override
			public void postExecute(ResultSet rs) {
				// TODO Auto-generated method stub
				try {
					if(rs.next()){
                        do{
							if(!rs.getString(2).equalsIgnoreCase("admin"))
							users.add(rs.getString(2)+","+rs.getString(7)+","+rs.getString(8)+","+rs.getString(6)+",");
                        }while(rs.next());
                    }
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub

			}
		});
		loginJdbcRunner.execute();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView=inflater.inflate(map,container,false);
		mMapView = (MapView) rootView.findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);

		mMapView.onResume(); // needed to get the map to display immediately

		try {
			MapsInitializer.initialize(getActivity().getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		mMapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap mMap) {
				googleMap = mMap;
				initilizeMap();
			}
		});
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		smsManager=SmsManager.getDefault();
		readAllUsers();
		handler=new Handler();
		button=(Button)getView().findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for(int i=0;i<users.size();i++){
					final String[] inp=users.get(i).split(",");
					Log.e("CHK",users.get(i));

					float[] distance = new float[2];

					Location.distanceBetween( Double.parseDouble(inp[1]), Double.parseDouble(inp[2]),
							c.getCenter().latitude, c.getCenter().longitude, distance);

					if( distance[0] < c.getRadius()  ){
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								Log.e("Sending SMS",inp[3]);
								smsManager.sendTextMessage(inp[3],null,"Emergency Alert Rasied in your location, Be alert!",null,null);
							}
						},3000);
					} else {
					//HANDLED

					}
				}
			}
		});

		editText=(EditText)getView().findViewById(R.id.editText);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				handler.removeCallbacks(r);
				handler.postDelayed(r,1500);

			}
		});

		r=new Runnable() {
			@Override
			public void run() {
				Log.e("Chc","Calle");
				if(editText.getText().toString().length()>0){
					googleMap.clear();

					c=googleMap.addCircle(new CircleOptions()
							.center(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()))
							.radius(Double.parseDouble(editText.getText().toString()))
							.strokeColor(Color.RED)
							.fillColor(Color.BLUE));

				}
			}
		};
	}




/**
* function to load map If map is not created it will create it for you
* */
private void initilizeMap() {

// check if map is created successfully or not
if (googleMap == null) {
Toast.makeText(getActivity(),
		"Sorry! unable to create maps", Toast.LENGTH_SHORT)
		.show();
}
else
{
	// Changing map type
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			// googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			// Showing / hiding your current location
			googleMap.setMyLocationEnabled(true);
			// Enable / Disable zooming controls
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			// Enable / Disable my location button
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			// Enable / Disable Compass icon
			googleMap.getUiSettings().setCompassEnabled(true);
			// Enable / Disable Rotate gesture
			googleMap.getUiSettings().setRotateGesturesEnabled(true);
			// Enable / Disable zooming functionality
			googleMap.getUiSettings().setZoomGesturesEnabled(true);
			
			googleMap.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
					mapClickListener.onMapClicked(arg0.latitude, arg0.longitude, arg0);
				}
			});
			
			googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
				
				@Override
				public void onMapLongClick(LatLng arg0) {
					// TODO Auto-generated method stub
					mapClickListener.onMapLongClicked(arg0.latitude, arg0.longitude, arg0);
				}
			});
			googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker arg0) {
					// TODO Auto-generated method stub
					mapClickListener.onMarkerClicked(arg0.getPosition().latitude, arg0.getPosition().longitude, arg0);
					return true;
				}
			});
}
}


/*
* creating random postion around a location for testing purpose only
*/
private double[] createRandLocation(double latitude, double longitude) {

return new double[] { latitude + ((Math.random() - 0.5) / 500),
longitude + ((Math.random() - 0.5) / 500),
150 + ((Math.random() - 0.5) * 10) };
}

	/**
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param colour 0-9
	 */
	public void addMarker(double latitude,double longitude,String title,int colour)
	{
		MarkerOptions marker = new MarkerOptions().position(
				new LatLng(latitude, longitude))
				.title(title);

		if (colour == 0)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		if (colour== 1)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		if (colour== 2)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
		if (colour== 3)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		if (colour== 4)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
		if (colour== 5)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
		if (colour== 6)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		if (colour== 7)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
		if (colour== 8)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
		if (colour== 9)
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude,
				longitude)).zoom(15).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		googleMap.addMarker(marker);


	}

}
