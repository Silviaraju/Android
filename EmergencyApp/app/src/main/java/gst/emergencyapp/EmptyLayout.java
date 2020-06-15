package gst.emergencyapp;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class EmptyLayout extends Fragment{

TextView statusTextView;
JDBCRunner loginJdbcRunner;
LoginModule loginModule;

String TAG = "Empty Fragment: ";

	public EmptyLayout() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.emptylayout, container,
				false);
		return rootView;
	}
	
		@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onViewCreated(view, savedInstanceState);
			statusTextView=(TextView)getView().findViewById(R.id.textView);


		}

	public void setText(String inp){
		statusTextView.setText("Login Successful  "+'\n'+" "+inp);
	}


	public void updateLocation(String latitude,String longitude){
		String SQLQUERY="UPDATE `users` SET `lat`='"+latitude+"',`lng`='"+longitude+"' WHERE `username`='"+commondatas.USERNAME+"'";
		JDBCRunner loginJdbcRunner=new JDBCRunner(getActivity().getApplicationContext(), SQLQUERY, commondatas.HOSTIP, commondatas.DBNAME, commondatas.DBUSERNAME, commondatas.DBPASSWORD, false, new AsyncFunction() {

			@Override
			public void preExecute() {
				// TODO Auto-generated method stub

			}

			@Override
			public void postExecute(ResultSet rs) {
				// TODO Auto-generated method stub

			}

			@Override
			public void doInBackground() {
				// TODO Auto-generated method stub

			}
		});
		loginJdbcRunner.execute();
	}

}
