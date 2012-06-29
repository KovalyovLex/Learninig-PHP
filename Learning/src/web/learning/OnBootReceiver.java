package web.learning;

import web.learning.activities.PreferenceActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	SharedPreferences settings = context.getSharedPreferences(PreferenceActivity.SETTINGS, 0);
    	if (settings.getBoolean(PreferenceActivity.AUTO_BOOT, true)){
    		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            	Intent serviceLauncher = new Intent(context, LearningService.class);
            	context.startService(serviceLauncher);
            	Log.v(this.getClass().getName(), "Service loaded while device boot.");
        	}
        }
    }
}