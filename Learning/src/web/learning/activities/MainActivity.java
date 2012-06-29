package web.learning.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;
import web.learning.R;
import web.learning.database.DataBaseHandler;
import web.learning.database.DataSave;
import web.learning.search.SearchHandler;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity{
	
	public static final String ITEM_ID = "mainItem_id";
	public static final String ACTION = "send_action";
	private static List<Integer> IDs;
	private static List<String> functions;
	private static boolean needRefresh = false;
	private static final int CheckUpdPeriod = 2; // 2 days;
	private static Context context = null;
	private static boolean needstop = true;
	
	public static void Initialize(List<Integer> id, List<String> function){
		IDs = id;
		functions = function;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	context = this;
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.cancelAll();
        
    	if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			 String query = getIntent().getStringExtra(SearchManager.QUERY);
			 ShowRequestResult(query);
			 return;
		}
	
    	if (DataSave.GetDaysFromLastCheck(this) > CheckUpdPeriod){
    		Log.v("Periodic Check", "start check, last update" + Integer.toString(DataSave.GetDaysFromLastCheck(this)));
    		DataBaseHandler dbHandler = new DataBaseHandler(this);
    		dbHandler.check_DB_Update_Quite();
    	}
    	// start main screen
    	
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, functions));
        
        ListView lv = getListView();		
        lv.setTextFilterEnabled(true);
        
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
        	  	Intent intent = new Intent();
				intent.setClass(MainActivity.this, ViewActivity.class);
				Bundle b = new Bundle();
		        b.putInt(ITEM_ID, IDs.get((int)id));
		        intent.putExtras(b);
			    intent.setAction(ACTION);
			    startActivity(intent);
          }
        });
        
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (needRefresh) {
			needRefresh = false;
			SearchHandler sHandler = new SearchHandler(this);
			
			List<String> functions = new ArrayList<String>();
	        IDs = new ArrayList<Integer>();
	        
	        for (int i = 1; i <= sHandler.GetMaxID();i++) {
	        	functions.add(sHandler.GetNEXTSortName());
	        	IDs.add(sHandler.GetNEXTSortID());
	        }
	        
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, functions));
	        
	        ListView lv = getListView();		
	        lv.setTextFilterEnabled(true);
	        
	        lv.setOnItemClickListener(new OnItemClickListener() {
	          public void onItemClick(AdapterView<?> parent, View view,
	              int position, long id) {
	        	  	Intent intent = new Intent();
					intent.setClass(MainActivity.this, ViewActivity.class);
					Bundle b = new Bundle();
			        b.putInt(ITEM_ID, IDs.get((int)id));
			        intent.putExtras(b);
				    intent.setAction(ACTION);
				    startActivity(intent);
	          }
	        });
		}
	}
	
	@Override
	public void onDestroy(){
		if (needstop){
			DataBaseHandler.stopAll();
		}
		needstop = true;
		super.onDestroy();
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	needstop = false;
    	super.onConfigurationChanged(newConfig);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.open_preferences:
	        	Intent intent = new Intent();
				intent.setClass(MainActivity.this, PreferenceActivity.class);
				startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onSearchRequested() {
		return super.onSearchRequested();
	}
	
	public static void refresh(){
		Log.v("MainActivity","Need refresh");
		if (context != null){
			SearchHandler sHandler = new SearchHandler(context);
			
			List<String> functions = new ArrayList<String>();
	        IDs = new ArrayList<Integer>();
	        
	        for (int i = 1; i <= sHandler.GetMaxID();i++) {
	        	functions.add(sHandler.GetNEXTSortName());
	        	IDs.add(sHandler.GetNEXTSortID());
	        }
	        
	        ((ListActivity) context).setListAdapter(new ArrayAdapter<String>(context, R.layout.list_item, functions));
	        
	        ListView lv = ((ListActivity) context).getListView();		
	        lv.setTextFilterEnabled(true);
	        
	        lv.setOnItemClickListener(new OnItemClickListener() {
	          public void onItemClick(AdapterView<?> parent, View view,
	              int position, long id) {
	        	  	Intent intent = new Intent();
					intent.setClass(context, ViewActivity.class);
					Bundle b = new Bundle();
			        b.putInt(ITEM_ID, IDs.get((int)id));
			        intent.putExtras(b);
				    intent.setAction(ACTION);
				    context.startActivity(intent);
	          }
	        });
		}else
			needRefresh = true;
	}
	
	private void ShowRequestResult(String query){
		SearchHandler sHandler = new SearchHandler(this);
		Map<String,Integer> map = sHandler.GetSearchMap(query);
		map = new TreeMap<String, Integer>(map);
		// Create list of View
        Vector<String> functions = new Vector<String>();
        IDs = new Vector<Integer>();
        Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
        for (int i = 0; i < map.size();i++) {
        	Map.Entry<String, Integer> pair = it.next();
        	functions.add(pair.getKey());
        	IDs.add(pair.getValue());
        }
        
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, functions));
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
        	  	Intent intent = new Intent();
				intent.setClass(MainActivity.this, ViewActivity.class);
				Bundle b = new Bundle();
		        b.putInt(ITEM_ID, IDs.get((int)id));
		        intent.putExtras(b);
			    intent.setAction(ACTION);
			    startActivity(intent);
          }
        });
	}
}