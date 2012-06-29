package web.learning.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import web.learning.activities.MainActivity;
import web.learning.activities.PreferenceActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class DataBaseHandler {
	
	public static final String MAXID_VALUE = "MaxID_In_Base";
	
	protected static final String STORAGE_NAME = "Database_storage";
	protected static final String OBJECT_FILE = "Obj_N";
	protected static final String LAST_DOUNLOAD_DB = "Last_download_num";
	protected static final String databaseURLBase = "http://www.kovalyov-lex.narod.ru/data/PHP/Database";
	
	protected String EXTERNAL_DIR = "";
	private static int MaxID; // Max id contain maximum object id number
	private Context context;
	private int lastChunkedID = 0;
	protected static UpdateTask task;
	protected static check_UpdateTask check_task;
	protected static check_UpdateTaskQuite check_task_quite;
	protected static int last_DB_num;
	protected static Date last_check;
	
	public DataBaseHandler(Context _context){
		context = _context;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			EXTERNAL_DIR = context.getExternalCacheDir().toString();
		}else{
			EXTERNAL_DIR = null;
		}
		SharedPreferences settings = context.getSharedPreferences(PreferenceActivity.SETTINGS, 0);
		MaxID = settings.getInt(MAXID_VALUE, 0);
		last_DB_num = settings.getInt(LAST_DOUNLOAD_DB, 0);
	}
	
	public boolean isReady(){
		return (EXTERNAL_DIR != null);
	}
	
	public static int getMaxID(){
		return MaxID;
	}
	
	public static void stopAll(){
		if (task != null){
			task.cancel(true);
			task = null;
		}
		if (check_task != null){
			check_task.cancel(true);
			check_task = null;
		}
		if (check_task_quite != null){
			check_task_quite.cancel(true);
			check_task_quite = null;
		}
	}
	
	public void check_DB_Update_Quite(){
		if ( check_task_quite == null ){
			// start update_check
			if (task != null){
				if (check_task.getStatus().equals(AsyncTask.Status.RUNNING) && !check_task.isCancelled()){
	    			// do hothing
	    		}else{
	    			if (task.getStatus().equals(AsyncTask.Status.RUNNING) && !task.isCancelled()){
	    				// update task is going!
	    				// do nothing
	    			}else{
	    				// update task dont running
	    				task.cancel(true);
	    				task = null;
	    				check_task.cancel(true);
	    				check_task = null;
	    				check_task_quite = new check_UpdateTaskQuite();
	        			check_task_quite.execute();
	    			}
	    		}	
	    	}else{
	    		if (check_task != null){
	    			Log.v("Update", "cancel & clear");
	    			check_task.cancel(true);
	    			check_task = null;
	    			// start quite check
	    			check_task_quite = new check_UpdateTaskQuite();
	    			check_task_quite.execute();
	    		}else{
	    			Log.v("Quite Update", "clear");
	    			check_task_quite = new check_UpdateTaskQuite();
	    			check_task_quite.execute();
	    		}
	    	}
		}else{
			if (check_task_quite.getStatus().equals(AsyncTask.Status.RUNNING) && !check_task.isCancelled()){
				// check running
				// do nothing
			}else{
				check_task_quite.cancel(true);
				// start update_check
				if (task != null){
					if (check_task.getStatus().equals(AsyncTask.Status.RUNNING) && !check_task.isCancelled()){
		    			// do hothing
		    		}else{
		    			if (task.getStatus().equals(AsyncTask.Status.RUNNING) && !task.isCancelled()){
		    				// update task is going!
		    				// do nothing
		    			}else{
		    				// update task dont running
		    				task.cancel(true);
		    				task = null;
		    				check_task.cancel(true);
		    				check_task = null;
		    				check_task_quite = new check_UpdateTaskQuite();
		        			check_task_quite.execute();
		    			}
		    		}	
		    	}else{
		    		if (check_task != null){
		    			Log.v("Update", "cancel & clear");
		    			check_task.cancel(true);
		    			check_task = null;
		    			// start quite check
		    			check_task_quite = new check_UpdateTaskQuite();
		    			check_task_quite.execute();
		    		}else{
		    			Log.v("Quite Update", "clear");
		    			check_task_quite = new check_UpdateTaskQuite();
		    			check_task_quite.execute();
		    		}
		    	}
			}
		}
	}
	
	public void check_DB_Update(){
		if (task != null){
			if (check_task.getStatus().equals(AsyncTask.Status.RUNNING) && !check_task.isCancelled()){
    			// do hothing
    			Toast.makeText(context, "Обновление базы данных уже запущено", Toast.LENGTH_SHORT).show();
    		}else{
    			if (task.getStatus().equals(AsyncTask.Status.RUNNING) && !task.isCancelled()){
    				// update task is going!
    				Toast.makeText(context, "Обновление базы данных уже запущено", Toast.LENGTH_SHORT).show();
    			}else{
    				// update task dont running
    				task.cancel(true);
    				task = null;
    				check_task.cancel(true);
    				check_task = new check_UpdateTask();
    				check_task.execute();
    			}
    		}	
    	}else{
    		if (check_task != null){
    			Log.v("Update", "cancel & clear");
    			check_task.cancel(true);
    			check_task = new check_UpdateTask();
    			check_task.execute();
    		}else{
    			Log.v("Update", "clear");
    			check_task = new check_UpdateTask();
    			check_task.execute();
    		}
    	}
	}
	
	private void ctasrtDownload(int nbase){
		if (task != null){
	   		task.cancel(true);
	   	}
	   	task = new UpdateTask(nbase);
	   	task.execute();
	}
	
	private class check_UpdateTask extends AsyncTask<Void,Void,Integer>{

		@Override
		protected Integer doInBackground(Void... params) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	    StrictMode.setThreadPolicy(policy);
    	    
    	    ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    boolean wifi = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    	    boolean data = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    	    
    	    if ( !(wifi || data) ) {
    	    	// no connection
    	    	return -2;
    	    }
    	    
    	    HttpURLConnection conn = null;
    	    
			for (int i = 1; true ;i++){
				try {
					conn = (HttpURLConnection) new URL(databaseURLBase + Integer.toString(last_DB_num + i)).openConnection();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					Log.e("DBdownloader", "Url parsing was failed: " + databaseURLBase +Integer.toString(last_DB_num + i));
					return -1;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Log.e("DBdownloader", databaseURLBase + Integer.toString(last_DB_num + i) + " does not exists");
					return -1;
				}
				try {
					if ( conn.getResponseCode() == 404){
						conn.disconnect();
						return (i - 1);
					}else{
						conn.disconnect();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				}
			}
		}
		
		@Override
		protected void onPostExecute(final Integer result) {
			if ( result < 0 ) {
				if (result == -1)
					Toast.makeText(context, "Проблемы с подключением к сети интернет", Toast.LENGTH_LONG).show();
				if (result == -2){
					Toast.makeText(context, "Вы не подключены к сети интернет", Toast.LENGTH_LONG).show();
				}
				return;
			}
    	    DataSave.SaveCorrentTime(context); // save time of last update
			if (result > 0){
				// update DB
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    	    builder.setMessage("Доступны новые обновления, загрузить их?")
	    	           .setCancelable(false)
	    	           .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	    	               public void onClick(DialogInterface dialog, int id) {
	    	            	   ctasrtDownload(result);
	    	               }
	    	           })
	    	           .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	    	               public void onClick(DialogInterface dialog, int id) {
	    	            	   
	    	               }
	    	           });
	    	    AlertDialog alert = builder.create();
	    	    alert.show();
			}else{
				Toast.makeText(context, "База данных не требует обновлений", Toast.LENGTH_LONG).show();
			}
			return;
		}
		
	}
	
	private class check_UpdateTaskQuite extends AsyncTask<Void,Void,Integer>{

		@Override
		protected Integer doInBackground(Void... params) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	    StrictMode.setThreadPolicy(policy);
    	    
    	    ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    boolean wifi = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    	    boolean data = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    	    
    	    if ( !(wifi || data) ) {
    	    	// no connection
    	    	return -1;
    	    }

    	    HttpURLConnection conn = null;
    	    
			for (int i = 1; true ;i++){
				try {
					conn = (HttpURLConnection) new URL(databaseURLBase + Integer.toString(last_DB_num + i)).openConnection();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					Log.e("DBdownloader", "Url parsing was failed: " + databaseURLBase +Integer.toString(last_DB_num + i));
					return -1;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Log.e("DBdownloader", databaseURLBase + Integer.toString(last_DB_num + i) + " does not exists");
					return -1;
				}
				
  		      	/*
  		      	try {
					conn.connect();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Log.v("Connection","I/O while connected");
					e1.printStackTrace();
					return -1;
				}*/
				
				try {
					if ( conn.getResponseCode() == 404){
						conn.disconnect();
						return (i - 1);
					}else{
						conn.disconnect();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.v("Resp Code","I/O while load");
					return -1;
				}
			}
		}
		
		@Override
		protected void onPostExecute(final Integer result) {
			if ( result < 0 ) return;
    	    DataSave.SaveCorrentTime(context); // save time of last update
			if (result > 0){
				// update DB
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    	    builder.setMessage("Доступны новые обновления, загрузить их?")
	    	           .setCancelable(false)
	    	           .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	    	               public void onClick(DialogInterface dialog, int id) {
	    	            	   ctasrtDownload(result);
	    	               }
	    	           })
	    	           .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	    	               public void onClick(DialogInterface dialog, int id) {
	    	            	   
	    	               }
	    	           });
	    	    AlertDialog alert = builder.create();
	    	    alert.show();
			}
		}
		
	}
	
	private String ChunkBase(String buff){
		String endStr = "";
		String object = "";
		FileOutputStream fos = null;
		int begin = buff.indexOf("<object", 0);
		if (begin == -1) return buff;
		int end = buff.indexOf("</object>", begin);
		if (end == -1) return buff;
		int lastend = 0;
		File obj_file = null;
		while ( (begin != -1) && (end != -1) ){
			begin =  buff.indexOf(">",begin) + 1;
			object = buff.substring(begin, end);
			lastend = end + 9;
			// save object
			obj_file = new File(EXTERNAL_DIR, OBJECT_FILE + Integer.toString(lastChunkedID+1) );
			if (obj_file.exists()) {
				try {
					obj_file.delete();
					obj_file.createNewFile();
				} catch (IOException ex) {
					Log.e("ChunkBase", "Can't renew file for write id = " + Integer.toString(lastChunkedID+1));
				}
			}
			fos = null;
			try {
        		fos = new FileOutputStream(obj_file);
    			//fos = context.openFileOutput(STORAGE_NAME,Context.MODE_PRIVATE);
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			Log.e("ChunkBase", "Can't open file for write id = " + Integer.toString(lastChunkedID+1));
    			return "";
    		}
			try {
				fos.write(object.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("ChunkBase", "Can't write to file with id = " + Integer.toString(lastChunkedID+1));
			}
			try {
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("ChunkBase", "Can't write to file with id = " + Integer.toString(lastChunkedID+1));
			}
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("ChunkBase", "Can't close file with id = " + Integer.toString(lastChunkedID+1));
			}
			
			lastChunkedID++;
			begin = buff.indexOf("<object", end);
			end = buff.indexOf("</object>", begin);
		}
		endStr = buff.substring(lastend);
		return endStr;
	}
	
	
	private class UpdateTask extends AsyncTask<Void,Integer,Void>{
		
		private boolean stop = true;
		int size, nToDownload;
		
		public UpdateTask(int numToDownload){
			nToDownload = numToDownload;
		}
		
		public void confirmStart(){
			stop = false;
		}
		
		@Override
		protected void onPreExecute(){
			stop = true;
			size = 0;
			
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	    StrictMode.setThreadPolicy(policy);
    	    
    	    ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    boolean wifi = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    	    boolean data = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    	    
    	    if ( !(wifi || data) ) {
    	    	cancel(true);
    	    	try {
					finalize();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    	Toast.makeText(context, "Вы не подключены к сети", Toast.LENGTH_LONG).show();
    	    	return;
    	    }
    	    
    	    if (data){
    	    	HttpURLConnection conn = null;
    	    	for (int i = 1; i <= nToDownload; i++){
		    	    try {
						conn = (HttpURLConnection) new URL(databaseURLBase + Integer.toString(last_DB_num + i)).openConnection();
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", "Url parsing was failed: " + databaseURLBase + Integer.toString(last_DB_num + i));
		    		    return;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", databaseURLBase + Integer.toString(last_DB_num + i) + " does not exists");
		    		    return;
					}
		    	    size += conn.getContentLength();
				    conn.disconnect();
    	    	}
	    	    //conn.setDoInput(true);
			    //conn.setRequestProperty("Connection", "Keep-Alive");
	    	    int kb = size / 1024;
	    	    int mb = kb / 1024;
	    	    String file_Size = "";
	    	    if (mb > 0){
	    	    	int mb_size = 0;
	    	    	mb_size = (kb - mb * 1024) * 100 / 1024;
	    	    	file_Size += Integer.toString(mb) + "," + Integer.toString(mb_size) + " Mb";
	    	    }else
	    	    if (kb > 0){
	    	    	int kb_size = 0;
	    	    	kb_size = (size - kb * 1024) * 100 / 1024;
	    	    	file_Size += Integer.toString(kb) + "," + Integer.toString(kb_size) + " Kb";
	    	    }
	    	    
	    	    AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    	    builder.setMessage("Вы не подключены к сети WiFi, загрузка из сети оператора может быть платной. " +
	    	    		"Вы согласны на загрузку " + file_Size + " из сети вашего оператора?")
	    	           .setCancelable(false)
	    	           .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	    	               public void onClick(DialogInterface dialog, int id) {
	    	            	   task.confirmStart();
	    	               }
	    	           })
	    	           .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	    	               public void onClick(DialogInterface dialog, int id) {
	    	            	   task.cancel(true);
	    	            	   try {
	    	            		   task.finalize();
	    	            	   } catch (Throwable e) {
	    	            		   // TODO Auto-generated catch block
	    	            		   e.printStackTrace();
	    	            	   }
	    	            	   task = null;
	    	               }
	    	           });
	    	    AlertDialog alert = builder.create();
	    	    alert.show();
    	    }
    	    
    	    if (wifi){
    	    	HttpURLConnection conn = null;
    	    	for (int i = 1; i <= nToDownload; i++){
		    	    try {
						conn = (HttpURLConnection) new URL(databaseURLBase + Integer.toString(last_DB_num + i)).openConnection();
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", "Url parsing was failed: " + databaseURLBase + Integer.toString(last_DB_num + i));
		    		    return;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", databaseURLBase + Integer.toString(last_DB_num + i) + " does not exists");
		    		    return;
					}
		    	    size += conn.getContentLength();
				    conn.disconnect();
    	    	}
	    	    //conn.setDoInput(true);
			    //conn.setRequestProperty("Connection", "Keep-Alive");
	    	    int kb = size / 1024;
	    	    int mb = kb / 1024;
	    	    String file_Size = "";
	    	    if (mb > 0){
	    	    	int mb_size = 0;
	    	    	mb_size = (kb - mb * 1024) * 100 / 1024;
	    	    	file_Size += Integer.toString(mb) + "," + Integer.toString(mb_size) + " Mb";
	    	    }else
	    	    if (kb > 0){
	    	    	int kb_size = 0;
	    	    	kb_size = (size - kb * 1024) * 100 / 1024;
	    	    	file_Size += Integer.toString(kb) + "," + Integer.toString(kb_size) + " Kb";
	    	    }
	    	    
    	    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    	    builder.setMessage("Обновить базу данных?\nРазмер загружаемых файлов: " +
	    	    		file_Size)
	    	           .setCancelable(false)
	    	           .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	    	               public void onClick(DialogInterface dialog, int id) {
	    	            	   task.confirmStart();
	    	               }
	    	           })
	    	           .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	    	               public void onClick(DialogInterface dialog, int id) {
	    	            	   task.cancel(true);
	    	            	   try {
	    	            		   task.finalize();
	    	            	   } catch (Throwable e) {
	    	            		   // TODO Auto-generated catch block
	    	            		   e.printStackTrace();
	    	            	   }
	    	            	   task = null;
	    	               }
	    	           });
	    	    AlertDialog alert = builder.create();
	    	    alert.show();
    	    	return ;
    	    }
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			while (stop && !this.isCancelled()){
				Log.v("Thread","I'm waiting to start");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (this.isCancelled()) return null;
			
			Log.v("Thread","I'm start update");
			
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	    StrictMode.setThreadPolicy(policy);
    	    
    	    publishProgress(0);
    	    
    	    String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
	        	
	        	// clear dir
	        	//File file = new File(EXTERNAL_DIR);
	        	//for(File f : file.listFiles())
	            //    f.delete();
	    		
	    		HttpURLConnection conn = null;
	    		int maxLines = 2000;
	    		int i=0;
	    		int load_size = 0;
	    		lastChunkedID = MaxID;
	    		for (int it = 1; it <= nToDownload;it++){
		    	    try {
						conn = (HttpURLConnection) new URL(databaseURLBase + Integer.toString(last_DB_num + it)).openConnection();
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", "Url parsing was failed: " + databaseURLBase + Integer.toString(last_DB_num + it));
		    		    return null;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", databaseURLBase + Integer.toString(last_DB_num + it) + " does not exists");
		    		    return null;
					}
		    	    conn.setDoInput(true);
	  		      	conn.setRequestProperty("Connection", "Keep-Alive");
	
	  		      	try {
						conn.connect();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", databaseURLBase + Integer.toString(last_DB_num + it) + " does not exists");
		    		    if ( conn != null )
		    		    	  conn.disconnect();
		    		    return null;
					}
	  		      	BufferedReader rd = null;
					try {
						rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					} catch (UnsupportedEncodingException e2) {
						// TODO Auto-generated catch block
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", "Error while creating InputStreamReader");
						return null;
					}				
				    String line;
				    String sbuffer = "";
				    StringBuilder str_build = new StringBuilder();
				    try {
						while ((line = rd.readLine()) != null) {
							str_build.append(line);
							load_size += line.length() + 11;
							i++;
							if (this.isCancelled()) return null;
							if (i == maxLines){
								publishProgress(load_size * 100 / size);
								i = 0;
								sbuffer = str_build.toString();
								sbuffer = ChunkBase(sbuffer);
								str_build.delete(0, str_build.length());
								str_build.append(sbuffer);
							}
						}
						sbuffer = ChunkBase(str_build.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", "Error while creating InputStreamReader");
						return null;
					}
				    
		  			try {
						rd.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Log.e("DBdownloader", "Can't close input reader!");
					}
	
		    		conn.disconnect();
		    		conn = null;
		    		MaxID = lastChunkedID;
	    		}
				
				publishProgress(-1);
				SearchDatabase searchbase = new SearchDatabase(context);
				searchbase.UpdateSearchBase();
			}
			else{
				// Notify that database don't load
				publishProgress(-2);
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (values[0] == -2){
				Toast.makeText(context, "Нет доступа к внешней памяти, повторите попытку позже", Toast.LENGTH_LONG).show();
				return;
			}
			if (values[0] == -1){
				Toast.makeText(context, "Подождите, создаётся таблица базы данных...", Toast.LENGTH_LONG).show();
				return;
			}
			if (values[0] == 0){
				Toast.makeText(context, "Пожалуйста, подождите... \nИдёт загрузка базы данных.", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, "Загружено " + Integer.toString(values[0]) + " %", Toast.LENGTH_SHORT).show();
				Log.v("onProgressUpdate",Integer.toString(values[0]));
			}
		}
		
		@Override
        protected void onPostExecute(Void result) {
			// Save MaxID
    		SharedPreferences settings = context.getSharedPreferences(PreferenceActivity.SETTINGS, 0);
    		SharedPreferences.Editor editor = settings.edit();
			editor.putInt(MAXID_VALUE, MaxID);
			
			editor.putInt(LAST_DOUNLOAD_DB, last_DB_num + nToDownload);
			editor.commit();
			Toast.makeText(context, "База данных успешно обновлена.", Toast.LENGTH_LONG).show();
        	MainActivity.refresh();
		}
	}
	
	public int GetMaxID(){
		return MaxID;
	}
	
	public Item GetItemFromBese(int id){
		Item it = new Item();
		FileInputStream fis = null;
		File fileinp = new File(EXTERNAL_DIR, OBJECT_FILE + Integer.toString(id) ); //
		try {
			fis = new FileInputStream(fileinp); //
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return it;
		}
		String line = "";
		String sbuffer = "";
		BufferedReader rd = null;
		rd = new BufferedReader(new InputStreamReader(fis));
		try {
			while ((line = rd.readLine()) != null){
				sbuffer += line;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.w("Database", "Can't get next line in file");
		}
		try {
			rd.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("Database", "Can't close input stream");
		}
		int endposition = 0, startposition = 0;
		startposition = sbuffer.indexOf("<name>");
		if (startposition != -1){
			// get name
			endposition = sbuffer.indexOf("</name>", startposition + 6);
			if (startposition + 6 < endposition)
				it.NAME = sbuffer.substring(startposition + 6, endposition);
		}
		startposition = sbuffer.indexOf("<versions>");
		if (startposition != -1){
			// get versions
			endposition = sbuffer.indexOf("</versions>", startposition + 10);
			if (startposition + 10 < endposition)
				it.VERSIONS = sbuffer.substring(startposition + 10, endposition);
		}
		startposition = sbuffer.indexOf("<short-description>");
		if (startposition != -1){
			// get short-description
			endposition = sbuffer.indexOf("</short-description>", startposition + 19);
			if (startposition + 19 < endposition)
				it.SH_DESCRIPTION = sbuffer.substring(startposition + 19, endposition);
		}
		startposition = sbuffer.indexOf("<description>");
		if (startposition != -1){
			// get description
			endposition = sbuffer.indexOf("</description>", startposition + 13);
			if (startposition + 13 < endposition)
				it.DESCRIPTION = sbuffer.substring(startposition + 13, endposition);
		}
		startposition = sbuffer.indexOf("<output>");
		if (startposition != -1){
			// get output
			endposition = sbuffer.indexOf("</output>", startposition + 8);
			if (startposition + 8 < endposition)
				it.OUTPUT = sbuffer.substring(startposition + 8, endposition);
		}
		startposition = sbuffer.indexOf("<errors>");
		if (startposition != -1){
			// get errors
			endposition = sbuffer.indexOf("</errors>", startposition + 8);
			if (startposition + 8 < endposition)
				it.ERRORS = sbuffer.substring(startposition + 8, endposition);
		}
		startposition = sbuffer.indexOf("<example>");
		if (startposition != -1){
			// get example
			endposition = sbuffer.indexOf("</example>", startposition + 9);
			if (startposition + 9 < endposition)
				it.EXAMPLE = sbuffer.substring(startposition + 9, endposition);
		}
		startposition = sbuffer.indexOf("<example_result>");
		if (startposition != -1){
			// get example-result
			endposition = sbuffer.indexOf("</example_result>", startposition + 16);
			if (startposition + 16 < endposition)
				it.EXAMPLE_RESULT = sbuffer.substring(startposition + 16, endposition);
		}
		endposition = 0;
		startposition = sbuffer.indexOf("<parameters>",endposition);
		// get parameters
		while (startposition != -1){
			endposition = sbuffer.indexOf("</parameters>", startposition + 12);
			if (startposition + 12 < endposition){
				it.PARAMETERS.add(sbuffer.substring(startposition + 12, endposition));
			}
			startposition = sbuffer.indexOf("<parameters>",endposition + 13);
		}
		return it;
	}
}
