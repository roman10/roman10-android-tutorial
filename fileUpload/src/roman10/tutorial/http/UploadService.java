package roman10.tutorial.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class UploadService extends Service {
	private Context mContext;
	private PowerManager.WakeLock wl;	//lock used when uploading is on-going  
	
	private static Queue<String> mUploadFileQueue = new LinkedList<String>();
	private final static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final static Lock readLock = readWriteLock.readLock();
	private final static Lock writeLock = readWriteLock.writeLock();
	public static void addUploadFile(String _task) {
		writeLock.lock();
		try {
			mUploadFileQueue.add(_task);
		} finally {
			writeLock.unlock();
		}
	}
	public static String removeUploadFile() {
		writeLock.lock();
		try {
			if (!mUploadFileQueue.isEmpty()) {
				return mUploadFileQueue.remove();
			} else {
				return null;
			}
		} finally {
			writeLock.unlock();
		}
	}
	public static String topUploadFile() {
		readLock.lock();
		try {
			if (!mUploadFileQueue.isEmpty()) {
				return mUploadFileQueue.peek();
			} else {
				return null;
			}
		} finally {
			readLock.unlock();
		}
	}
	public static boolean noFileToUpload() {
		return mUploadFileQueue.isEmpty();
	}
	
	@Override
	public void onCreate() {
		mContext = this.getApplicationContext();
	}
	
	UploadFileTask uploadFileTask = null;
	@Override
	public void onStart(Intent intent, int startid) {
		Log.i("onStart", "start upload files");
		if (uploadFileTask == null || uploadFileTask.isCancelled()) {
			uploadFileTask = new UploadFileTask();
			uploadFileTask.execute();
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void stopService() {
		this.stopSelf();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (wl != null) {
			wl.release();
		}
	}
	
	//TODO: to make it more efficient, don't create http client every time
	public void uploadFile(String pFileFullPathName) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			httpClient.getParams().setParameter("http.socket.timeout", new Integer(90000)); // 90 second 
			HttpPost httpPost = new HttpPost("http://172.26.190.224/receive.php");
			SimpleMultipartEntity mpEntity = new SimpleMultipartEntity();
			File vFile = new File(pFileFullPathName);
		    mpEntity.addPart("myfile", pFileFullPathName.substring(pFileFullPathName.lastIndexOf("/")+1), new FileInputStream(vFile), "text/plain");
			
			httpPost.setEntity(mpEntity);
			HttpResponse lResponse = httpClient.execute(httpPost);
			if (lResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) { 
                Log.e("UploadService-uploadFile","Response Status line code:"+ lResponse.getStatusLine()); 
		    }
			HttpEntity resEntity = lResponse.getEntity(); 
            if (resEntity == null) { 
                Log.e("UploadService-uploadFile", "No Response!"); 
            } 
		} finally {
			httpClient.getConnectionManager().shutdown(); 
		}
	}
	
	private class UploadFileTask extends AsyncTask<Object, Integer, Object> {
		String lCurrentFileName;
		@Override
		protected void onPreExecute() {
			/*acquire PARTIAL_WAKE_LOCK WakeLock, so that the processing can continue even when screen is off*/
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, 
					"generateStreamletTask");
			wl.acquire();
		}
		@Override
		protected Object doInBackground(Object... arg0) {
			 try {
				while (!noFileToUpload()) {
					lCurrentFileName = topUploadFile();
					Log.i("UploadFileTask-doInBackground", "upload file " + lCurrentFileName);
					//upload the file
					uploadFile(lCurrentFileName);
					removeUploadFile();		//after upload is done, we then remove the item from the queue
					this.publishProgress();
				}
	        } catch (Exception e) {
				Log.i("UploadFileTask-doInBackground", "exception");
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			//update the UI about upload progress
			FileUpload.textDisplay.append(lCurrentFileName);
		}
		
		@Override
		protected void onPostExecute(Object res) {
			//TODO: update UI about upload finish
			uploadFileTask = null;
			stopService();		//finish the service
		}
	}

}
