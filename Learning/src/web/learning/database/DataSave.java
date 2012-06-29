package web.learning.database;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

public class DataSave {
	private static final String MOUNTH = "last_mounth";
	private static final String YEAR = "last_year";
	private static final String MOUNTHDAY = "last_day";
	private static final String WEEKDAY = "last_wday";
	private static final String HOUR = "last_hour";
	private static final String MINUTE = "last_minute";
	private static final String SETTINGS = "last_save";
	
	public static void SaveCorrentTime(Context context){
		Date curr = new Date();
		SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
		String mounth = null;
		switch (curr.getMonth()){
		case 0:
			mounth = "������";
			break;
		case 1:
			mounth = "�������";
			break;
		case 2:
			mounth = "�����";
			break;
		case 3:
			mounth = "������";
			break;
		case 4:
			mounth = "���";
			break;
		case 5:
			mounth = "����";
			break;
		case 6:
			mounth = "����";
			break;
		case 7:
			mounth = "�������";
			break;
		case 8:
			mounth = "��������";
			break;
		case 9:
			mounth = "�������";
			break;
		case 10:
			mounth = "������";
			break;
		case 11:
			mounth = "�������";
			break;
		}
		String weekday = null;
		switch (curr.getDay()){
		case 0:
			weekday = "�����������";
			break;
		case 1:
			weekday = "�������";
			break;
		case 2:
			weekday = "�����";
			break;
		case 3:
			weekday = "�������";
			break;
		case 4:
			weekday = "�������";
			break;
		case 5:
			weekday = "�������";
			break;
		case 6:
			weekday = "�����������";
			break;
		}
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(YEAR, Integer.toString(curr.getYear()) );
		editor.putString(WEEKDAY, weekday);
		editor.putString(MOUNTH, mounth);
		editor.putString(MOUNTHDAY, Integer.toString(curr.getDate()) );
		editor.putString(HOUR, Integer.toString(curr.getHours()) );
		editor.putString(MINUTE, Integer.toString(curr.getMinutes()) );
		editor.putInt(MOUNTH + "I", curr.getMonth());
		editor.putInt(MOUNTHDAY + "I", curr.getDate());
		editor.putInt(YEAR + "I", curr.getYear());
		editor.commit();
	}
	
	public static String GetLastYear(Context context){
		SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
		return settings.getString(YEAR, null);
	}
	
	public static String GetLastMounth(Context context){
		SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
		return settings.getString(MOUNTH, null);
	}
	
	public static String GetLastDayOfMounth(Context context){
		SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
		return settings.getString(MOUNTHDAY, null);
	}
	
	public static String GetLastDayOfWeek(Context context){
		SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
		return settings.getString(WEEKDAY, null);
	}
	
	public static String GetLastHour(Context context){
		SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
		return settings.getString(HOUR, null);
	}
	
	public static String GetLastMinute(Context context){
		SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
		return settings.getString(MINUTE, null);
	}
	
	public static String GetLastDate(Context context){
		String wee = "";
		String year = GetLastYear(context);
		String mounth = GetLastMounth(context);
		String dayofmounth = GetLastDayOfMounth(context);
		String dayofweek = GetLastDayOfWeek(context);
		String hour = GetLastHour(context);
		String minute = GetLastMinute(context);
		if ( year == null || mounth == null || dayofmounth == null || 
				dayofweek == null || hour == null || minute == null ) return "�������";
		wee = dayofmounth + " , " + mounth + " � " + hour + ":" + minute;
		return wee;
	}
	
	public static int GetDaysFromLastCheck(Context context){
		Date curr = new Date();
		int days = 0;
		int lday, lmoun, lye;
		SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
		lmoun = settings.getInt( MOUNTH + "I", curr.getMonth() );
		lday = settings.getInt( MOUNTHDAY + "I", curr.getDate() );
		lye = settings.getInt( YEAR + "I", curr.getYear() - 1 );
		days = 365 * (curr.getYear() - lye ) + 30 * (curr.getMonth() - lmoun) + curr.getDate() - lday;
		return days;
	}
	
}
