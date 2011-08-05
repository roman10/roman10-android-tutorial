package roman10.tutorial.utils;

import android.os.Environment;

public class EnvironmentUtilsStatic {
	public static boolean is_external_storage_available() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
}
