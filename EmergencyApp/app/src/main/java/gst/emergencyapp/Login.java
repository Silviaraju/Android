package gst.emergencyapp;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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


public class Login extends Fragment{
String hostIPAddress="";

EditText edtUserName,edtPassword,edtHostIPAddress;
Button btnLogin;
TextView statusTextView;
JDBCRunner loginJdbcRunner;
LoginModule loginModule;
DataBaseHandler dbHander;
SQLiteDatabase sqliteDB;
String TAG = "Login Fragment: ";
public interface loginInterface{
	public void loginSuccessfull(boolean successfull);
}

	public void setLoginModule(LoginModule loginModule) {
		this.loginModule=loginModule;	
	}
	
	public Login() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.login, container,
				false);
		return rootView;
	}
	
		@Override
	public void onResume() {
		super.onResume();
		if(commondatas.localDBMode)
		{
			openDB(true);
		}
	}
	
	@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onViewCreated(view, savedInstanceState);
			if(commondatas.localDBMode)
			{
				LinearLayout hostIPLL=(LinearLayout)getView().findViewById(R.id.llHostIP);
				hostIPLL.setVisibility(View.GONE);
				openDB(true);
			}
			edtHostIPAddress=(EditText)getView().findViewById(R.id.edtHostIP);
			edtUserName=(EditText)getView().findViewById(R.id.edtUserName);
			edtPassword=(EditText)getView().findViewById(R.id.edtPassword);
			statusTextView=(TextView)getView().findViewById(R.id.txtStatus);
			btnLogin=(Button)getView().findViewById(R.id.btnLogin);
			btnLogin.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(commondatas.localDBMode && verify())
					{
					if(login_verify())
					{
					openDB(false);
					((loginInterface)loginModule).loginSuccessfull(true);
					}
					else
					{
						showErrorMessage("Invalid UserName or Password");
					}
					}
					else
					{
					if(verify())
					{
					startLoginVerification();
					}
					}
				}
			});
			
		}

	public String getHostIPAddress() {
		return edtHostIPAddress.getText().toString();
	}
	
	public void showErrorMessage(String Message)
	{
		statusTextView.setText(Message);
		Animation slide = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.push_left_in);
		statusTextView.startAnimation(slide);
		statusTextView.setVisibility(View.VISIBLE);

	}
	
	

		public boolean verify()
		{
			boolean ret=true;
			if(!commondatas.localDBMode)
			{
			if(edtHostIPAddress.getText().toString().length()<1)
			{edtHostIPAddress.setError("Host IP Required");ret=false;}
			else
			{commondatas.HOSTIP=edtHostIPAddress.getText().toString();}
			}
			
			if(edtUserName.getText().toString().length()<1){edtUserName.setError("Enter Username");ret=false;}
			if(edtPassword.getText().toString().length()<1){edtPassword.setError("Enter Password");ret=false;}
			return ret;
		}
		
		public void startLoginVerification()
		{
			String SQLQUERY="select * from users where username='"+edtUserName.getText().toString()+"'";
			loginJdbcRunner=new JDBCRunner(getActivity().getApplicationContext(), SQLQUERY, commondatas.HOSTIP, commondatas.DBNAME, commondatas.DBUSERNAME, commondatas.DBPASSWORD, true, new AsyncFunction() {
				
				@Override
				public void preExecute() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void postExecute(ResultSet rs) {
					// TODO Auto-generated method stub
					try {
						if(rs.next())
						{
							commondatas.USERNAME=edtUserName.getText().toString();	
							commondatas.PASSWORD=edtPassword.getText().toString();
							commondatas.HOSTIP=edtHostIPAddress.getText().toString();
							commondatas.USERTYPE=rs.getString("type");
							((loginInterface)loginModule).loginSuccessfull(true);
						}
						else
						{
							showErrorMessage("Invalid UserName or Password");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
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
		
		

		public boolean login_verify() {
			String userName = edtUserName.getText().toString();
			String passWord = edtPassword.getText().toString();
			final Cursor cursor = sqliteDB
					.query(false, "users", new String[] {"username","password"}, null,
							null, null, null, null, null);

			cursor.moveToFirst();
			if (cursor.getPosition() != -1) 
			{
				while (!cursor.isAfterLast()) {
					if(userName.equalsIgnoreCase(cursor.getString(0).toString()) && passWord.equalsIgnoreCase(cursor.getString(1).toString()))
							{
						print("User Exsist"+userName);
						return true;
							}
					cursor.moveToNext();
				}

			}
			return false;

		}

		public boolean openDB(boolean open) {
			if (open) {
				print("Opening DB");
				dbHander = new DataBaseHandler(getActivity().getApplicationContext());
				try {
					dbHander.createDataBase();
					dbHander.openDataBase();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}

				sqliteDB = dbHander.getReadableDatabase();
				return true;
			} else {
				print("Closing DB");
				sqliteDB.close();
				dbHander.close();
				return true;
			}

		}

		public void print(String what) {
			System.out.println(TAG + what);
		}

}
