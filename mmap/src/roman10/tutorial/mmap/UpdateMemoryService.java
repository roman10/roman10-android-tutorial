package roman10.tutorial.mmap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;

public class UpdateMemoryService extends Service {
	private Context mContext;
	private static native void naUpdate();
	private static native void naMap();
	private static native void naUnmap();
	
	public static final String START_STOP = "SS";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		mContext = this.getApplicationContext();
		naMap();
	}
	
	private updateTask mTask;
	@Override
	public void onStart(Intent intent, int startId) {
		int startStop = intent.getIntExtra(START_STOP, 0);
		if (startStop == 0) {
			mRun = true;
			mTask = new updateTask();
			mTask.execute(null);
		} else {
			mRun = false;
			naUnmap();
			this.stopSelf();
		}
	}

	private boolean mRun = false;
	private class updateTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (mRun) {
				naUpdate();
				SystemClock.sleep(1000);
			}
			return null;
		}
		
	}
	
	 static {
	    	System.loadLibrary("mmap");
	 }
}
