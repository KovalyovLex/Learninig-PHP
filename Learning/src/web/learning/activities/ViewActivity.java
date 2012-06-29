package web.learning.activities;

import web.learning.LearningService;
import web.learning.R;
import web.learning.database.DataBaseHandler;
import web.learning.database.Item;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ViewActivity extends Activity{
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			// show message about exit
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);//
			alt_bld.create();     
			alt_bld.setMessage("Попробуйте загрузить приложение позже, когда буде доступна внешняя память.");
			alt_bld.setCancelable(false);
			alt_bld.setPositiveButton("OK", new OnClickListener() { 
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					ViewActivity.this.finish();
				} 
			});
			alt_bld.show();
			return;
		}
		
        setContentView(R.layout.view);
        
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.cancelAll();
        int obj_id = LearningService.DEF_OBJ_ID;
        Bundle bundle = getIntent().getExtras();
        if (MainActivity.ACTION.equals(getIntent().getAction())){
        	// start from mainActivity
        	obj_id = bundle.getInt(MainActivity.ITEM_ID, LearningService.DEF_OBJ_ID);
        }else{
        	// start from service
            
            // get OBJ_ID from SharedPreferences
        	SharedPreferences settings = getSharedPreferences(PreferenceActivity.SETTINGS, 0);
        	obj_id = settings.getInt(LearningService.CURRENT_OBJ_ID, LearningService.DEF_OBJ_ID);
        }

    	DataBaseHandler dBHandler = new DataBaseHandler(this);
    	Item item = dBHandler.GetItemFromBese(obj_id);
    	
    	TextView nameVIEW, versionsVIEW, sh_descriptVIEW, descriptVIEW, parametersVIEW,
    		outputVIEW, errorsVIEW, exampleVIEW, example_resultVIEW;
    	nameVIEW = (TextView) findViewById(R.id.VIEWname_text);
    	versionsVIEW = (TextView) findViewById(R.id.VIEWversions_text);
    	sh_descriptVIEW = (TextView) findViewById(R.id.VIEWsh_deskript_text);
    	descriptVIEW = (TextView) findViewById(R.id.VIEWdescription_text);
    	parametersVIEW = (TextView) findViewById(R.id.VIEWparameters_text);
    	outputVIEW = (TextView) findViewById(R.id.VIEWoutput_text);
    	errorsVIEW = (TextView) findViewById(R.id.VIEWerrors_text);
    	exampleVIEW = (TextView) findViewById(R.id.VIEWexample_text);
    	example_resultVIEW = (TextView) findViewById(R.id.VIEWexample_result_text);
    	
    	nameVIEW.setText(item.GetName());
    	versionsVIEW.setText(item.GetVersions());
    	sh_descriptVIEW.setText(item.GetSh_Description());
    	descriptVIEW.setText(item.GetDescription());
    	if (item.GetParameters().size() == 0){
    		// parameters is empty
    		parametersVIEW.setVisibility(8);
    		parametersVIEW = (TextView) findViewById(R.id.Dparameters_text);
    		parametersVIEW.setVisibility(8);
    		FrameLayout fL = (FrameLayout) findViewById(R.id.FrameLayout04);
    		fL.setVisibility(8);
    	}else{
    		String params = "";
    		for (int i = 0; i < item.GetParameters().size(); i++){
    			if (i == item.GetParameters().size() - 1) 
    				params += item.GetParameters().get(i);
    			else 
    				params += item.GetParameters().get(i) + " \n";
    		}
    		parametersVIEW.setText(params);
    	}
    	if (item.GetOutput() == ""){
    		// output is empty
    		outputVIEW.setVisibility(8);
    		outputVIEW = (TextView) findViewById(R.id.Doutput_text);
    		outputVIEW.setVisibility(8);
    		FrameLayout fL = (FrameLayout) findViewById(R.id.FrameLayout05);
    		fL.setVisibility(8);
    	}else{
    		outputVIEW.setText(item.GetOutput());
    	}
    	if (item.GetErrors() == ""){
    		errorsVIEW.setVisibility(8);
    		errorsVIEW = (TextView) findViewById(R.id.Derrors_text);
    		errorsVIEW.setVisibility(8);
    		FrameLayout fL = (FrameLayout) findViewById(R.id.FrameLayout06);
    		fL.setVisibility(8);
    	}else{
    		errorsVIEW.setText(item.GetErrors());
    	}
    	if (item.GetExample() == ""){
    		exampleVIEW.setVisibility(8);
    		exampleVIEW = (TextView) findViewById(R.id.Dexample_text);
    		exampleVIEW.setVisibility(8);
    		FrameLayout fL = (FrameLayout) findViewById(R.id.FrameLayout07);
    		fL.setVisibility(8);
    	}else{
    		exampleVIEW.setText(item.GetExample());
    	}
    	if (item.GetExample_result() == ""){
    		example_resultVIEW.setVisibility(8);
    		example_resultVIEW = (TextView) findViewById(R.id.Dexample_result_text);
    		example_resultVIEW.setVisibility(8);
    		FrameLayout fL = (FrameLayout) findViewById(R.id.FrameLayout07);
    		fL.setVisibility(8);
    	}else{
    		example_resultVIEW.setText(item.GetExample_result());
    	}
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
