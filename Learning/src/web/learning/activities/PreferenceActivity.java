package web.learning.activities;

import web.learning.LearningService;
import web.learning.R;
import web.learning.database.DataBaseHandler;
import web.learning.database.DataSave;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class PreferenceActivity extends Activity implements View.OnClickListener {

	public static final String AUTO_BOOT = "auto_boot";
	public static final String UPD_WORD_INTERVAL = "update_word_interval";
	public static final String SETTINGS = "settings";
	public static final int MaxInterval = 23; // 24 hours
	public static final int DEFAULT_UPD_WORD_INTERVAL = 3; // 3 hour
	
	private Button start_stop_button, update_button;
	private CheckBox autoBoot;
	private SeekBar updateWrdInt_bar;
	private TextView currentInterval;
	private DataBaseHandler dBHandler;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference);
        
        dBHandler = new DataBaseHandler(this);
        TextView db_Info_view = (TextView) findViewById(R.id.dB_info);
        db_Info_view.setText("Информация о базе данных функций PHP :\n" + 
        		"Всего записей в базе : " + Integer.toString( dBHandler.GetMaxID() ) +
        		"\nПоследняя проверка обновлений базы данных проводилась : \n" + 
        		DataSave.GetLastDate( this ));
        
        autoBoot = (CheckBox) findViewById(R.id.BootRunService_Check);
        start_stop_button = (Button) findViewById(R.id.buttonStartStop);
        update_button = (Button) findViewById(R.id.updateDataBase_button);
        updateWrdInt_bar = (SeekBar) findViewById(R.id.UpdateWordInterval_bar);
        currentInterval = (TextView) findViewById(R.id.updatePeriodCurrent_text);
        updateWrdInt_bar.setMax(MaxInterval);
        
        // Loading Settings From sharedpreffs
        SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
        autoBoot.setChecked(settings.getBoolean(AUTO_BOOT, true));
        updateWrdInt_bar.setProgress(settings.getInt(UPD_WORD_INTERVAL, DEFAULT_UPD_WORD_INTERVAL) - 1);
        currentInterval.setText(" " + Integer.toString(updateWrdInt_bar.getProgress() + 1) + " hour ");
        
        if (LearningService.IsActive()){
        	// Stop button active
        	start_stop_button.setText(R.string.stop_butt);
        }
        else {
        	// Start button active
        	start_stop_button.setText(R.string.start_butt);
        }
        start_stop_button.setOnClickListener(this);
        autoBoot.setOnClickListener(this);
        update_button.setOnClickListener(this);
        updateWrdInt_bar.setOnSeekBarChangeListener(new BarChangeListener());
    }
	
    @Override
    public void onBackPressed(){
    	//DataBaseHandler.setNewContext(getParent());
    	super.onBackPressed();
    }
    
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
        case R.id.buttonStartStop:
        	if (start_stop_button.getText() == this.getString(R.string.stop_butt)){
        		// Stop button active
        		stopService(new Intent(this, LearningService.class));
        		start_stop_button.setText(R.string.start_butt);
        	}
        	else{
        		// Start button active
        		startService(new Intent(this, LearningService.class));
        		start_stop_button.setText(R.string.stop_butt);
        	}
            break;
        case R.id.BootRunService_Check:
        	// Save Settings to SharedPreferences
        	SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(AUTO_BOOT, autoBoot.isChecked());
			editor.commit();
        	break;
        case R.id.updateDataBase_button:
        	// Update Database
        	DataBaseHandler dBHandler = new DataBaseHandler(this);
        	dBHandler.check_DB_Update();
        	break;
		}
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
	
	private class BarChangeListener implements SeekBar.OnSeekBarChangeListener {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			int prog = progress + 1;
			SharedPreferences settings = getSharedPreferences(SETTINGS, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(UPD_WORD_INTERVAL, prog);
			editor.commit();
        	currentInterval.setText(" " + Integer.toString(prog) + " hour ");
        	LearningService.changeUpdateInterval(prog);
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
