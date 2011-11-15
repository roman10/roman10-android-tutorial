package roman10.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;

public class EnvUtils {
	public static boolean is_external_storage_available() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	//this requires ACCESS_NETWORK_STATE 
	public static boolean isOnline(Context pContext) {
		 ConnectivityManager cm = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo ni = cm.getActiveNetworkInfo();
		 if (ni == null) {
			 return false;
		 } else {
			 return ni.isConnected();
			 //return ni.isConnectedOrConnecting();
		 }
	}
	
	
	
	public static long getAvailableSDSpace() {
		 StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());        
		 long blockSize = statFs.getBlockSize();
		 long totalSize = statFs.getBlockCount()*blockSize;
		 long availableSize = statFs.getAvailableBlocks()*blockSize;
		 long freeSize = statFs.getFreeBlocks()*blockSize;
		 return availableSize;
	}
}
