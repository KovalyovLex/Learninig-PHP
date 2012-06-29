package web.learning;

import web.learning.activities.PreferenceActivity;
import web.learning.database.DataBaseHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class UpdateFunctionService extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	int MaxId;
    	SharedPreferences settings = context.getSharedPreferences(PreferenceActivity.SETTINGS, 0);
    	MaxId = settings.getInt(DataBaseHandler.MAXID_VALUE, 0);

		if (MaxId == 0) return; // database is empty
    	int nextId = 1 + (int)(Math.random() * MaxId);
    	SharedPreferences.Editor editor = settings.edit();
		editor.putInt(LearningService.CURRENT_OBJ_ID, nextId);
		editor.commit();
		
        Log.v(this.getClass().getName(), "UpdateFunction started at time: " + new java.sql.Timestamp(System.currentTimeMillis()).toString() + 
        		" Next Id = " + Integer.toString(nextId) + " Max Id = " + Integer.toString(MaxId) );
    }
}