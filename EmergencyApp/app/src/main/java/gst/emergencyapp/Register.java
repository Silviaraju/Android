package gst.emergencyapp;

import java.io.IOException;
import java.sql.ResultSet;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Register extends Fragment{
	LoginModule loginModule;
	EditText edtRegUserName,edtRegPassword,edtRegConfirmPassword,edtRegUserType,edtRegEmail,edtRegPhone,edtRegAddress,edtRegAadhar;
	Button btnRegRegister;
	String TAG = "Register Fragment : ";
	DataBaseHandler dbHander;
	SQLiteDatabase sqliteDB;
	TextView statusTextView;
	
	public interface registerInterface{
		public void registerSuccessfull(boolean successfull);
	}

	public void setLoginModule(LoginModule loginModule) {
		this.loginModule=loginModule;	
	}
	
	public Register() {
	}
	
	public void showErrorMessage(String Message)
	{
		statusTextView.setText(Message);
		Animation slide = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.push_left_in);
		statusTextView.startAnimation(slide);
		statusTextView.setVisibility(View.VISIBLE);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.register, container,
				false);
		return rootView;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		edtRegAddress= (EditText) getView().findViewById(R.id.edtAddress);
				edtRegAadhar= (EditText) getView().findViewById(R.id.edtAadhar);
		edtRegUserName = (EditText) getView().findViewById(R.id.edtRegUserName);
		edtRegPassword = (EditText) getView().findViewById(R.id.edtRegPassword);
		edtRegConfirmPassword = (EditText) getView().findViewById(R.id.edtRegConfirmPassword);
		edtRegUserType = (EditText) getView().findViewById(R.id.edtRegUserType);
		edtRegEmail = (EditText) getView().findViewById(R.id.edtRegEmail);
		edtRegPhone = (EditText) getView().findViewById(R.id.edtRegPhone);
		btnRegRegister=(Button) getView().findViewById(R.id.btnRegRegister);
		statusTextView=(TextView)getView().findViewById(R.id.txtRegStatus);

		if(!commondatas.isUserTypeRequired){
			LinearLayout ll=(LinearLayout)getView().findViewById(R.id.llUserType);
			ll.setVisibility(View.GONE);
		}
		if(!commondatas.isEmailRequired){
			LinearLayout ll1=(LinearLayout)getView().findViewById(R.id.llEmail);
			ll1.setVisibility(View.GONE);
		}
		if(!commondatas.isAadharRequired){
			LinearLayout ll1=(LinearLayout)getView().findViewById(R.id.llAadhar);
			ll1.setVisibility(View.GONE);
		}
		if(!commondatas.isAddressRequired){
			LinearLayout ll1=(LinearLayout)getView().findViewById(R.id.llAddress);
			ll1.setVisibility(View.GONE);
		}
		btnRegRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(commondatas.localDBMode && verify())
				{
					openDB(true);
					if(!read_userdetails())
					{
						long status=insertUserDetails();
						if(status<0)//ERROR
						{
							showErrorMessage("Failed to register User");
						}
						else
						{
							openDB(false);
							((registerInterface)loginModule).registerSuccessfull(true);
						}
					}
				}
				else
				{
				if(verify())
				{
					registerUser();
				}
				else
				{
					
				}
				}
			}
		});

	}
	
	
public boolean verify()
{
//	EditText name, userName, password, cpassword, email, phoneNumber;
	Boolean ret=true;
	
	if(edtRegUserName.getText().toString().length()<1){edtRegUserName.setError("Enter UserName");ret=false;}else{edtRegUserName.setError(null);}
	if(edtRegPassword.getText().toString().length()<1){edtRegPassword.setError("Enter Password");ret=false;}else{edtRegPassword.setError(null);}
	if(!edtRegConfirmPassword.getText().toString().equalsIgnoreCase(edtRegPassword.getText().toString())){edtRegConfirmPassword.setError("Password Doesnt Match");ret=false;}else{edtRegConfirmPassword.setError(null);}
	if(edtRegConfirmPassword.getText().toString().length()<1){edtRegConfirmPassword.setError("Re-Enter Password");ret=false;}else{edtRegConfirmPassword.setError(null);}
	if(commondatas.isEmailRequired) {
		if (!edtRegEmail.getText().toString().contains("@")) {
			edtRegEmail.setError("E-Mail ID Invalid");
			ret = false;
		} else {
			edtRegEmail.setError(null);
		}
		if (edtRegEmail.getText().toString().length() < 1) {
			edtRegEmail.setError("Enter Email ID");
			ret = false;
		} else {
			edtRegEmail.setError(null);
		}
	}
	if(edtRegPhone.getText().toString().length()<10){edtRegPhone.setError("Invalid Phone Number");ret=false;}else{edtRegPhone.setError(null);}//It will Set but ok it wont be visible
	if(edtRegPhone.getText().toString().length()<1){edtRegPhone.setError("Enter Phone Number");ret=false;}else{edtRegPhone.setError(null);}
	if(commondatas.isUserTypeRequired) {
		boolean userTypeCheck = false;
		String userTypes = "";
		for (int i = 0; i < commondatas.USERTYPES.length; i++) {
			userTypes = userTypes + commondatas.USERTYPES[i] + "\n";
			if (edtRegUserType.getText().toString().equalsIgnoreCase(commondatas.USERTYPES[i])) {
				userTypeCheck = true;
			}
		}
		if (!userTypeCheck) {
			ret = false;
			edtRegUserType.setError("Enter Valid User Type ie: " + userTypes);
		} else {
			edtRegUserType.setError(null);
		}
	}
	return ret;
}

public void registerUser()
{
	String SQLQUERY="INSERT INTO `users`(`user_id`, `username`, `password`, `email`, `type`, `mobile`, `lat`, `lng`, `aadhar`, `address`) VALUES ('','"+edtRegUserName.getText().toString()+"','"+edtRegPassword.getText().toString()+"','"+edtRegEmail.getText().toString()+"','"+edtRegUserType.getText().toString()+"','"+edtRegPhone.getText().toString()+"', '', '', '"+edtRegAadhar.getText().toString()+"','"+edtRegAddress.getText().toString()+"' )";
	JDBCRunner registerUser=new JDBCRunner(getActivity().getApplicationContext(), SQLQUERY, commondatas.HOSTIP, commondatas.DBNAME, commondatas.DBUSERNAME, commondatas.DBPASSWORD, false, new AsyncFunction() {
		
		@Override
		public void preExecute() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void postExecute(ResultSet rs) {
			// TODO Auto-generated method stub
			((registerInterface)loginModule).registerSuccessfull(true);
		}
		
		@Override
		public void doInBackground() {
			// TODO Auto-generated method stub
			
		}
	});
	registerUser.execute();
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

public long insertUserDetails() {

	ContentValues args = new ContentValues();
	args.put("username",edtRegUserName.getText().toString());
	args.put("password",edtRegPassword.getText().toString());
	args.put("mobile",edtRegPhone.getText().toString());
	args.put("email",edtRegEmail.getText().toString());
	args.put("type",edtRegUserType.getText().toString());

	print("Inserting User Details in DB");
	long rc = sqliteDB.insert("users",null, args);
	System.out.println("$$$$$$$$$$$ rows returnedDB1  :" + rc);
	return rc;

}

public boolean read_userdetails() {
	
	String username = edtRegUserName.getText().toString();
	final Cursor cursor = sqliteDB
			.query(false, "users", new String[] {"username"}, null,
					null, null, null, null, null);

	cursor.moveToFirst();
	if (cursor.getPosition() != -1) 
	{
		while (!cursor.isAfterLast()) {
			if(username.equalsIgnoreCase(cursor.getString(0).toString()))
					{
				print("User Exsist"+username);
				return true;
					}
			cursor.moveToNext();
		}

	}
	return false;

}


void print(String what) {
	System.out.println(TAG + what);
}
}
