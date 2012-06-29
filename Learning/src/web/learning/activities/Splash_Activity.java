package web.learning.activities;

import java.util.ArrayList;
import java.util.List;

import web.learning.R;
import web.learning.search.SearchHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

public class Splash_Activity extends Activity{
	private static final int SPLASH_TIME = 1000; // 1 sec
	private static Load_and_splash_Task loader;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		TextView text = (TextView) findViewById(R.id.splash_text);  
		text.setText("Version " + String.valueOf( getVersion() ) );
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			loader = new Load_and_splash_Task();
			loader.execute();
		}else{
			// show message about exit
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);//
			alt_bld.create();     
			alt_bld.setMessage("Попробуйте загрузить приложение позже, когда буде доступна внешняя память.");
			alt_bld.setCancelable(false);
			alt_bld.setPositiveButton("OK", new OnClickListener() { 
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					Splash_Activity.this.finish();
				} 
				});
			alt_bld.show();
		}
	}
	
	@Override  
	protected void onDestroy() {  
		super.onDestroy();  
		if (loader != null && loader.getStatus() == Status.RUNNING)  
			loader.cancel(true);  
		loader = null;
	} 
	
	private String getVersion(){
		String sVersion = "?";  
		PackageInfo pInfo = null;  
		try {  
			 pInfo = getPackageManager().getPackageInfo(  
			 getPackageName(), PackageManager.GET_META_DATA);  
			 sVersion = pInfo.versionName;  
		}   
		catch (NameNotFoundException e) {  
			 e.printStackTrace();  
		}      
		return sVersion;
	}
	
	private class Load_and_splash_Task extends AsyncTask<Void,Void,Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			// update progress + sleep
	        
	        SearchHandler sHandler = new SearchHandler(Splash_Activity.this);
	        List<Integer> IDs;
	        List<String> functions;
	        // Create list of View
	        functions = new ArrayList<String>();
	        IDs = new ArrayList<Integer>();
	        
	        for (int i = 1; i <= sHandler.GetMaxID();i++) {
	        	functions.add(sHandler.GetNEXTSortName());
	        	IDs.add(sHandler.GetNEXTSortID());
	        }
	        try {
				Thread.sleep(SPLASH_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        MainActivity.Initialize(IDs, functions);
	        
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			finish();
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
		}
	}
}
