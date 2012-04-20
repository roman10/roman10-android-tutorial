package roman10.imageviewer;

import android.content.Context;
import android.content.SharedPreferences;

public class ViewerConfigStatic {
	private static final String PERFERENCE_FILE_NAME = "roman10reborn.imageviewer.SharedPreferencesFile";
	private static final String viewer_id = "viewer_id";
	private static final String show_interval = "show_interval";
	private static final String show_style = "show_style";	//0: not set, 1: in list order; 2: random
	//get and set id: 0, not set; 1, default viewer; 2, external viewer
	public static int getViewerId(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PERFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(viewer_id, 1);
	}
	public static void setViewerId(Context context, int _viewer_id) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(PERFERENCE_FILE_NAME , Context.MODE_PRIVATE).edit();
		prefs.putInt(viewer_id, _viewer_id);
		prefs.commit();
	}
	//get and set slidehow interval
	public static int getShowInterval(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PERFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(show_interval, 3);
	}
	public static void setShowInterval(Context context, int _showInterval) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(PERFERENCE_FILE_NAME , Context.MODE_PRIVATE).edit();
		prefs.putInt(show_interval, _showInterval);
		prefs.commit();
	}
	//get and set slidehow style
	public static int getShowStyle(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PERFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(show_style, 0);
	}
	public static void setShowStyle(Context context, int _showStyle) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(PERFERENCE_FILE_NAME , Context.MODE_PRIVATE).edit();
		prefs.putInt(show_style, _showStyle);
		prefs.commit();
	}
}
