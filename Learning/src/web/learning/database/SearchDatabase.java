package web.learning.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import web.learning.activities.PreferenceActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class SearchDatabase {

	protected static final String SEARCH_DATABASE = "Search_database";
	private static int MaxID = 0; // Max id contain maximum object id number
	private Context context;
	private String EXTERNAL_DIR;
	//private static String SearchBase;
	private static Map<String, Integer> SearchBase;
	
	public SearchDatabase(Context _context){
		context = _context;
		EXTERNAL_DIR = context.getExternalCacheDir().toString();
		SearchBase = new HashMap<String, Integer>();
		SharedPreferences settings = context.getSharedPreferences(PreferenceActivity.SETTINGS, 0);
		MaxID = settings.getInt(DataBaseHandler.MAXID_VALUE, 0);
		SearchBase = new HashMap<String, Integer>();
		FileInputStream fis = null;
		try {
			fis = context.openFileInput(SEARCH_DATABASE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			UpdateSearchBase();
			return;
		}
		try {
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MakeSearchBase();
	}
	
	public int GetMaxID(){
		return MaxID;
	}
	
	protected void UpdateSearchBase(){
		int maxId1, maxId2;
		
		SharedPreferences settings = context.getSharedPreferences(PreferenceActivity.SETTINGS, 0);
		maxId2 = settings.getInt(DataBaseHandler.MAXID_VALUE, 0);
		maxId1 = DataBaseHandler.getMaxID();
		MaxID = (maxId2 > maxId1) ? maxId2 : maxId1;
		
		Log.v("MaxID = " + Integer.toString(MaxID),"maxId1 = " + Integer.toString(maxId1) + " maxId2 = " + Integer.toString(maxId2));
		
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(SEARCH_DATABASE,Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sbuffer = "";
		String line;
		FileInputStream fis = null;
		File fileinp = null;
		BufferedReader rd = null;
		int endposition = 0, startposition = 0;
		
		int basei = 100;
		
		for (int i=1; i <= MaxID; i++){
			if ( (i % basei) == 0 ){
				Log.v("Update_Progress", Integer.toString(i));
			}
			fileinp = new File(EXTERNAL_DIR, DataBaseHandler.OBJECT_FILE + Integer.toString(i) ); //
			try {
				fis = new FileInputStream(fileinp); //
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				return ;
			}
			rd = new BufferedReader(new InputStreamReader(fis));
			try {
				while ((line = rd.readLine()) != null) {
					startposition = line.indexOf("<name>");
					if (startposition != -1){
						// get name
						endposition = line.indexOf("</name>", startposition + 6);
						if (startposition + 6 < endposition){
							sbuffer = "~\\" + line.substring(startposition + 6, endposition) + "~\\" + Integer.toString(i) + "~\\" + " \n";
							SearchBase.put(line.substring(startposition + 6, endposition), i);
							fos.write(sbuffer.getBytes());
					  	  	fos.flush();
							break;
						}
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.e("UpdateSearchBase", "I/O Exception. Can't get next line!");
			}
			try {
				rd.close();
				fis.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.e("UpdateSearchBase", "I/O Exception. Can't close readers");
			}
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("UpdateSearchBase", "I/O Exception. Can't close search_base file");
		}
	}

	public Map<String, Integer> GetSearchBase(){
		return SearchBase;
	}
	
	protected void MakeSearchBase(){
		String name = "", ID = "";
		FileInputStream fis = null;
		try {
			fis = context.openFileInput(SEARCH_DATABASE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader rd = null;
		rd = new BufferedReader(new InputStreamReader(fis));
		
		String line;
		int id = 0;
		int position = 0, startpos = 0, endpos = 0;
		
		try {
			while ((line = rd.readLine()) != null) {
				position = line.indexOf("~\\");
				if (position != -1){
					startpos = position + 2;
					endpos = line.indexOf("~\\", startpos);
					if (endpos > startpos)
						name = line.substring(startpos, endpos);
					startpos = endpos + 2;
					endpos = line.indexOf("~\\", startpos);
					if (endpos > startpos){
						ID = line.substring(startpos, endpos);
						id = Integer.parseInt(ID);
						SearchBase.put(name, id);
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.e("UpdateSearchBase", "I/O Exception. Can't get next line!");
		}
		try {
			rd.close();
			fis.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.e("UpdateSearchBase", "I/O Exception. Can't close readers");
		}
		
		Log.v("MakeSearchBase","FINISH!");
	}
	
}
