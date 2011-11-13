package roman10.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class FileUtilsStatic {
	private static final String TAG = "FileUtilsStatic";
	public static final String DEFAULT_DIR = "/sdcard/adash/";
	public static final String DEFAULT_STREAMLET_DIR = DEFAULT_DIR + "streamlet/";

	public static boolean is_external_storage_available() {
		//String test = Environment.getExternalStorageState();
		//Log.v("test", test);
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public static final int BYTE_TO_MB_FACTOR = 0x100000;
	public static long getAvailableExternalStorage() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
	            //long totalBlocks = stat.getBlockCount();
	            long availableBlocks = stat.getAvailableBlocks();
	            return availableBlocks*blockSize;
			} catch (IllegalArgumentException e) {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public static int getAvailableExternalPer() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
	            int availablePercent = stat.getAvailableBlocks() * 100 / stat.getBlockCount();
	            return availablePercent;
			} catch (IllegalArgumentException e) {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public static long getAvailablePhoneStorage() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks*blockSize;
	}
	
	public static int getAvailablePhonePer() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        int availablePercent = stat.getAvailableBlocks() * 100 / stat.getBlockCount();
        return availablePercent;
	}
	
	public static void createDirIfNotExist(String _path) {
		File f = new File(_path);
		try {
			if (f.exists()) {
				//directory already exists
			} else {
				if (f.mkdirs()) {
					Log.v(TAG, "createDirIfNotExist created " + _path);
				} else {
					Log.v(TAG, "createDirIfNotExist failed to create " + _path);
				}
			}
		} catch (Exception e) {
			//create directory failed
			Log.v(TAG, "createDirIfNotExist failed to create " + _path);
		}
	}
	
	public static void deleteAllFiles() {
		try {
			File[] tfiles = new File(DEFAULT_STREAMLET_DIR).listFiles();
			if (tfiles != null) {
				for (File currentthumb : tfiles) {
					if (!currentthumb.isDirectory()) {
						currentthumb.delete();
					}
				}
			}
		} catch (Exception e) {
			//dir not exists or cannot delete the file
			e.printStackTrace();
		}
		
		try {
			File[] tfiles = new File(DEFAULT_DIR).listFiles();
			if (tfiles != null) {
				for (File currentthumb : tfiles) {
					if (!currentthumb.isDirectory()) {
						currentthumb.delete();
					}
				}
			}
		} catch (Exception e) {
			//dir not exists or cannot delete the file
			e.printStackTrace();
		}
	}
	
	public static void initDirs() {
		createDirIfNotExist(DEFAULT_DIR);
		createDirIfNotExist(DEFAULT_STREAMLET_DIR);
	}
	
	
	public static void sortFilesByDateDesc(File[] _files) {
    	Arrays.sort(_files, new Comparator<File>() {
			public int compare(File o1, File o2) {
				if (o1.lastModified() > o2.lastModified()) {
					return -1;
				} else if (o1.lastModified() < o2.lastModified()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
    }
	
	public static int copyFileToDest(String _src, String _dest) {
		InputStream myInput;
		byte[] buffer = new byte[2048];
		int length;
		try {
			myInput = new FileInputStream(_src);
			OutputStream myOutput = new FileOutputStream(_dest);
			while ((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
			}
			//byte[] data = new byte[myInput.available()];
			//myOutput.write(data);
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (FileNotFoundException e1) {
			return -1;
		} catch (IOException e2) {
			return -1;
		}
		return 0;
	}
	
	//append file _fa to the end of file _fdest
	public static int appendFileToFile(File _fa, File _fdest) {
		try {
			InputStream myInput = new FileInputStream(_fa);
			OutputStream output = new FileOutputStream(_fdest, true);
			byte[] buffer = new byte[2048];
			int length = 0;
			while ((length = myInput.read(buffer))>0){
				output.write(buffer, 0, length);
			}
			output.flush();
			myInput.close();
			output.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return 0;
	}
	
	//convert file to drawable
	public static Drawable convert_file_to_drawable(String fullname) {
		Bitmap bm = null;
    	try {
			InputStream is = new FileInputStream(fullname);
			bm = BitmapFactory.decodeStream(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (bm!=null) {
			return new BitmapDrawable(bm);
		} else {
			return null;
		}
	}
	
	//convert file to drawable
	public static Drawable convert_file_to_drawable(File tf) {
		Bitmap bm = null;
    	try {
			InputStream is = new FileInputStream(tf);
			bm = BitmapFactory.decodeStream(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (bm!=null) {
			return new BitmapDrawable(bm);
		} else {
			return null;
		}
	}
	
	public static void clearBitmap(Bitmap _bm) {
		if (_bm!=null) {
			_bm.recycle();
			_bm = null;
			System.gc();
		}
	}
}
