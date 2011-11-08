package roman10.media.camcorder;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsStatic {
	private static final String PERFERENCE_FILE_NAME = "roman10.media.camcorder.Settings.SharedPreferencesFile";
	private static String ENDF = "ENDF";
	private static String CONF = "CONF";
	private static String RESC = "RESC";
	
	
	public static int getEncodingFormat(Context _context) {
		SharedPreferences prefs = _context.getSharedPreferences(PERFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(ENDF, 0);
	}
	public static void setEncodingFormat(Context _context, int _option) {
		SharedPreferences.Editor prefs = _context.getSharedPreferences(PERFERENCE_FILE_NAME , Context.MODE_PRIVATE).edit();
		prefs.putInt(ENDF, _option);
		prefs.commit();
	}
	
	public static int getContainerFormat(Context _context) {
		SharedPreferences prefs = _context.getSharedPreferences(PERFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(CONF, 0);
	}
	public static void setContainerFormat(Context _context, int _option) {
		SharedPreferences.Editor prefs = _context.getSharedPreferences(PERFERENCE_FILE_NAME , Context.MODE_PRIVATE).edit();
		prefs.putInt(CONF, _option);
		prefs.commit();
	}
	
	public static int getResolutionChoice(Context _context) {
		SharedPreferences prefs = _context.getSharedPreferences(PERFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(RESC, 0);
	}
	public static void setResolutionChoice(Context _context, int _option) {
		SharedPreferences.Editor prefs = _context.getSharedPreferences(PERFERENCE_FILE_NAME , Context.MODE_PRIVATE).edit();
		prefs.putInt(RESC, _option);
		prefs.commit();
	}
	
}
