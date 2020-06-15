package gst.emergencyapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;


public class LoginModule extends Activity implements Login.loginInterface,Register.registerInterface, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
	LinearLayout llHeader;
	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;
	Register register;
	Login login;
	EmptyLayout emptyLayout=null;
	Button btnLogin,btnRegister;
	String HEADER_SELECTED="#232323";
	String HEADER_UNSELECTED="#000000";
	GoogleApiClient mGoogleApiClient;
	LocationRequest mLocationRequest;
	Location mLastLocation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLocation();
		setContentView(R.layout.loginmain);
		register=new Register();
		register.setLoginModule(this);	
		login=new Login();
		login.setLoginModule(this);
		llHeader=(LinearLayout)findViewById(R.id.llHeader);
		llHeader.setVisibility(View.VISIBLE);
		fragmentManager=getFragmentManager();
		
		btnLogin=(Button)findViewById(R.id.btnMainLogin);
		btnLogin.setBackgroundColor(Color.parseColor(HEADER_SELECTED));//0e5a4e
		
		btnRegister=(Button)findViewById(R.id.btnMainRegister);
		btnRegister.setBackgroundColor(Color.parseColor(HEADER_UNSELECTED));
		
		btnLogin.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				fragmentTransaction=fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.logincontainer, login);
				fragmentTransaction.commit();
				btnLogin.setBackgroundColor(Color.parseColor(HEADER_SELECTED));
				btnRegister.setBackgroundColor(Color.parseColor(HEADER_UNSELECTED));
			}
		});
		
		btnRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(commondatas.localDBMode)
				{
					fragmentTransaction=fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.logincontainer, register);
					fragmentTransaction.commit();
					btnLogin.setBackgroundColor(Color.parseColor(HEADER_UNSELECTED));
					btnRegister.setBackgroundColor(Color.parseColor(HEADER_SELECTED));
				}
				else
				{
					if(login.getHostIPAddress().length()>6)
					{
						commondatas.HOSTIP=login.getHostIPAddress().toString();
						fragmentTransaction=fragmentManager.beginTransaction();
						fragmentTransaction.replace(R.id.logincontainer, register);
						fragmentTransaction.commit();
					}
					else
					{				
						login.showErrorMessage("Enter Host IPAddress to register");
					}
						
				}
			
			}
		});

	}

	public void initLocation(){
		// Create an instance of GoogleAPIClient.
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.build();
		}
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(10000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
				.addLocationRequest(mLocationRequest);
		PendingResult<LocationSettingsResult> result =
				LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
						builder.build());
		result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
			@Override
			public void onResult(LocationSettingsResult locationSettingsResult) {
				final Status status = locationSettingsResult.getStatus();
				final LocationSettingsStates locationSettingsStates= locationSettingsResult.getLocationSettingsStates();
				switch (status.getStatusCode()) {
					case LocationSettingsStatusCodes.SUCCESS:
						// All location settings are satisfied. The client can
						// initialize location requests here.

						break;
					case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
						// Location settings are not satisfied, but this can be fixed
						// by showing the user a dialog.
						// Show the dialog by calling startResolutionForResult(),
						// and check the result in onActivityResult().

						break;
					case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
						// Location settings are not satisfied. However, we have no way
						// to fix the settings so we won't show the dialog.

						break;
				}
			}
		});

	}



	protected void onStart() {
		mGoogleApiClient.connect();
		createLocationRequest();

		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);
		if (mLastLocation != null) {
			Log.e("Location","Received"+mLastLocation.getLatitude()+" Lng"+mLastLocation.getLongitude());
		}
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		fragmentTransaction=fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.logincontainer, login);
		fragmentTransaction.commit();

		if (mGoogleApiClient.isConnected() ) {
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this);
		}
	}

	@Override
	public void registerSuccessfull(boolean successfull) {
		// TODO Auto-generated method stub
		fragmentTransaction=fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.logincontainer, login);
		fragmentTransaction.commit();
		btnLogin.setBackgroundColor(Color.parseColor(HEADER_SELECTED));
		btnRegister.setBackgroundColor(Color.parseColor(HEADER_UNSELECTED));
		Toast.makeText(getApplicationContext(), "Registration Successfull!", 1).show();
	}

	@Override
	public void loginSuccessfull(boolean successfull) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Welcome Login Successfull!", 1).show();
		if(commondatas.USERTYPE.equalsIgnoreCase("admin")){
			llHeader.setVisibility(View.GONE);
			fragmentTransaction=fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.logincontainer, new CustomMap());
			fragmentTransaction.commit();
		}
		else{
			llHeader.setVisibility(View.GONE);
			fragmentTransaction=fragmentManager.beginTransaction();
			emptyLayout=new EmptyLayout();
			fragmentTransaction.replace(R.id.logincontainer, emptyLayout);
			fragmentTransaction.commit();
		}
	}


	@Override
	public void onLocationChanged(Location location) {
		mLastLocation = location;
		Log.e("Location","Received"+mLastLocation.getLatitude()+" Lng"+mLastLocation.getLongitude());
		if(emptyLayout!=null){
			emptyLayout.setText("Latitude: "+mLastLocation.getLatitude()+'\n'+" Longitude: "+mLastLocation.getLongitude());
			emptyLayout.updateLocation(String.valueOf(mLastLocation.getLatitude()),String.valueOf(mLastLocation.getLongitude()));
		}
	}
}
