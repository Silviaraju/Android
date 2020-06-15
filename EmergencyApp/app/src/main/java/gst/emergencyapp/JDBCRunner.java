package gst.emergencyapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

//public interface AsyncFunction{
//  public void preExecute();
//  public void doInBackground();
//  public void postExecute();
//}

public class JDBCRunner
{
  
  
  Connection conn;
public String SQLQUERY="",IPADDRESS="",DB_NAME="",USER_NAME="",PASSWORD="";
public boolean read=false;
AsyncFunction asf;
Context context;

public JDBCRunner(Context context,String SQLQUERY,String IPADDRESS,String DBNAME,String DBUSERNAME,String DBPASSWORD,boolean read,AsyncFunction asf)
{
  // TODO Auto-generated constructor stub
  this.SQLQUERY=SQLQUERY;
  this.IPADDRESS=IPADDRESS;
  this.DB_NAME=DBNAME;
  this.USER_NAME=DBUSERNAME;
  this.PASSWORD=DBPASSWORD;
  this.asf=asf;
  this.context=context;
  this.read=read;
  
}

public void execute()
{
	Log.e("GOPI", "Executing");
	new QuerySQL().execute("");
}


public class QuerySQL extends AsyncTask<String, Void, Boolean> {
  Exception error;
  ResultSet rs;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
     	  Log.e("GOPI", "PRE EXECUTE");
    }

    @Override
    protected Boolean doInBackground(String... args) {
     	  Log.e("GOPI", "DO IN BACK");
    try {
      
      
      Class.forName("com.mysql.jdbc.Driver");
   //  conn = DriverManager.getConnection("jdbc:mysql://"+IPADDRESS+":3306/"+DB_NAME,USER_NAME,PASSWORD);
     conn = DriverManager.getConnection("jdbc:mysql://192.168.0.65:3306/emergencyapp","root","root");
    } catch (SQLException se) {
      Log.e("ERRO1",se.getMessage());
    } catch (ClassNotFoundException e) {
      Log.e("ERRO2",e.getMessage());
    } catch (Exception e) {
        Log.e("ERRO3",e.getMessage());
    }
    
    if(read){//READ
    try {
    	Log.e("GOPI", "False READ "+SQLQUERY);
  //    String COMANDOSQL="select * from users where username='"+user+"' && password='"+pass+"'";
      String COMANDOSQL=SQLQUERY;
      Statement statement = conn.createStatement();
      rs = statement.executeQuery(COMANDOSQL);
    if(rs.next()){
    	rs.previous();
      return true;
    }
    Log.e("GOPI", "False "+COMANDOSQL);
return false; 

    } catch (Exception e) {
      error = e;
      e.printStackTrace();
      return false;
//      Toast.makeText(getBaseContext(),"Successfully Registered...", Toast.LENGTH_LONG).show();
    }
    }
    else
    {
    	Log.e("GOPI", "False WRITE "+SQLQUERY);
      try {
        Statement statement = conn.createStatement();
        //int success=statement.executeUpdate("insert into users values('"+userName.getText().toString()+"','"+password.getText().toString()+"','"+phoneNumber.getText().toString()+"','"+email.getText().toString()+"')");
        int success=statement.executeUpdate(SQLQUERY);
        if (success >= 1) {
          // successfully created product
          
          return true;
          // closing this screen
//          finish();
        } else {
          // failed to create product
          return false;
        }


        
        // Toast.makeText(getBaseContext(),
        // "Successfully Inserted.", Toast.LENGTH_LONG).show();
      } catch (Exception e) {
        error = e;
        return false;
//        Toast.makeText(getBaseContext(),"Successfully Registered...", Toast.LENGTH_LONG).show();
      }

    }


    }

  @Override
    protected void onPostExecute(Boolean result1) {
   	  Log.e("GOPI", "POST EXECUTE"+result1);
      if(result1)
      {//DATA PRESENT
 
        asf.postExecute(rs);
      }else
      {
        if(error!=null)
        {
        
        	//asf.postExecute(null);
        }
        else
        {//EMPTY RS
        	  Toast.makeText(context,"Failed" ,Toast.LENGTH_LONG).show();
          //Toast.makeText(getBaseContext(),"Check your credentials!!!" ,Toast.LENGTH_LONG).show();
        }
      }
      super.onPostExecute(result1);
    }
}
}
