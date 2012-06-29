package web.learning;

import web.learning.activities.MainActivity;
import web.learning.activities.PreferenceActivity;
import web.learning.activities.ViewActivity;
import web.learning.database.DataBaseHandler;
import web.learning.database.Item;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class OnScreenOnReceiver extends BroadcastReceiver {

	private static final int NotifyID = 1298;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		TelephonyManager telMan = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telMan.getCallState() != 0) return; // incoming call
		
		KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Activity.KEYGUARD_SERVICE);  
		if ( keyguardManager.inKeyguardRestrictedInputMode() ){
			// wait unlock or PowerDownScreen
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			while ( (keyguardManager.inKeyguardRestrictedInputMode()) && (pm.isScreenOn()) ){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (!pm.isScreenOn()) return; // was locked
		}
		
		DataBaseHandler dBHand = new DataBaseHandler(context);
		if ( !dBHand.isReady() )
			return; // database not ready
		if (dBHand.GetMaxID() == 0) return; // database is empty
		SharedPreferences settings = context.getSharedPreferences(PreferenceActivity.SETTINGS, 0);
		int obj_ID = settings.getInt(LearningService.CURRENT_OBJ_ID, dBHand.GetMaxID());
		Item item = dBHand.GetItemFromBese(obj_ID);
		
    	Intent _intent = new Intent(context, ViewActivity.class);
    	Bundle b = new Bundle();
        b.putInt(MainActivity.ITEM_ID,obj_ID);
        intent.putExtras(b);
	    intent.setAction(MainActivity.ACTION);
	    Notification notification = new Notification(R.drawable.php_black,
	    		"Learning", System.currentTimeMillis());
	    notification.setLatestEventInfo(context,
	        	"Функция дня:", item.GetName() + " — " + item.GetSh_Description(),  
	        	PendingIntent.getActivity(context, 0, _intent, 0));
	    NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    mManager.cancelAll();
	    mManager.notify(NotifyID, notification);
	    
	    Toast.makeText(context, "Функция дня: \n\n" + 
	    item.GetName()+ " — " + item.GetSh_Description(), Toast.LENGTH_LONG).show();
	}

}
