package web.learning;

import web.learning.activities.PreferenceActivity;
import web.learning.database.DataBaseHandler;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class LearningService extends Service {

	
	public static final int HOUR_INTERVAL = 3600000; // 1 hour
	public static final String CURRENT_OBJ_ID = "current_id";
	public static final int DEF_OBJ_ID = 1;
	
	private int MAX_ID;
    private static int INTERVAL = 0;
    private static final int FIRST_RUN = 1000; // 1 sec
    private static int REQUEST_CODE = 11223344;

    private static boolean active = false;
    private static AlarmManager alarmManager;
    private static BroadcastReceiver mReceiver;
    private static Context context;
    
    @Override
    public void onCreate() {
        // Get max Id from settings
    	SharedPreferences settings = getSharedPreferences(PreferenceActivity.SETTINGS, 0);
    	MAX_ID = settings.getInt(DataBaseHandler.MAXID_VALUE, 0);
        
    	if (MAX_ID < DEF_OBJ_ID){
        	Log.v("LearningService onCreate","I'm not started, because DB is empty");
        	active = false;
        	return; // service not starting because database is empty
        }
        
        super.onCreate();
        context = this;
        if (INTERVAL == 0) {
        	// initialize time intervals from SharedPreferences
        	INTERVAL = HOUR_INTERVAL * settings.getInt(PreferenceActivity.UPD_WORD_INTERVAL, PreferenceActivity.DEFAULT_UPD_WORD_INTERVAL);
        }
        
        // initialize receiver ON_SCREEN_ON
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mReceiver = new OnScreenOnReceiver();
        registerReceiver(mReceiver, filter);
        // receiver initialized
        
        active = true;
        startService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(this.getClass().getName(), "onBind(..)");
        return null;
    }

    @Override
    public void onDestroy() {
    	if (!active ) return;
        if (alarmManager != null) {
            Intent intent = new Intent(this, UpdateFunctionService.class);
            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0));
        }
        active = false;
        unregisterReceiver(mReceiver);
        //Toast.makeText(this, "Сервис остановлен", Toast.LENGTH_SHORT).show();
        Log.v(this.getClass().getName(), "Service onDestroy(). Stop AlarmManager at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
    }
    
    public static void changeUpdateInterval(int newInterval){
    	if (newInterval > 0){
    		INTERVAL = HOUR_INTERVAL * newInterval;
    		if (active) restart();
    	}
    }
    
    private static void restart(){
    	if (!active) return;
    	if (alarmManager != null) {
            Intent intent = new Intent(context, UpdateFunctionService.class);
            alarmManager.cancel(PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0));
        }
    	Intent intent = new Intent(context, UpdateFunctionService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);

        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + FIRST_RUN,
                INTERVAL,
                pendingIntent);
    }
    
    public static boolean IsActive(){
    	return active;
    }
    
    private void startService() {
    	
        Intent intent = new Intent(this, UpdateFunctionService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + FIRST_RUN,
                INTERVAL,
                pendingIntent);

        //Toast.makeText(this, "Сервис запущен", Toast.LENGTH_SHORT).show();
        Log.v(this.getClass().getName(), "AlarmManger started at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
    }
}
