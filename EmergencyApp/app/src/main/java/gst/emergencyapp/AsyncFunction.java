package gst.emergencyapp;

import java.sql.ResultSet;



	  public interface AsyncFunction{
		    public void preExecute();
		    public void doInBackground();
		    public void postExecute(ResultSet rs);
		  }
