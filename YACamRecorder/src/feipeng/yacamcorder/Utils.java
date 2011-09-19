package feipeng.yacamcorder;

import java.io.File;

public class Utils {
	public static int puEncodingFormat = 0;
	public static int puContainerFormat = 0;
	public static int puResolutionChoice = 0;
	public static void createDirIfNotExist(String _path) {
		File lf = new File(_path);
		try {
			if (lf.exists()) {
				//directory already exists
			} else {
				if (lf.mkdirs()) {
					//Log.v(TAG, "createDirIfNotExist created " + _path);
				} else {
					//Log.v(TAG, "createDirIfNotExist failed to create " + _path);
				}
			}
		} catch (Exception e) {
			//create directory failed
			//Log.v(TAG, "createDirIfNotExist failed to create " + _path);
		}
	}
}