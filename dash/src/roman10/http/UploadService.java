package roman10.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;


import roman10.media.dash.VideoBrowser;

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
	
	@Override
	public void onCreate() {
		mContext = this.getApplicationContext();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.i("onStart", "start upload files");
		UploadFileTask uploadFileTask = new UploadFileTask();
		uploadFileTask.execute();
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
			
			HttpPost httpPost = new HttpPost("http://cervino.ddns.comp.nus.edu.sg/~a0075306/receive.php");
//			File lFile = new File(pFileFullPathName);
//			FileEntity lEntity;
//			lEntity = new FileEntity(lFile, "binary/octet-stream"); 
//			lEntity.setChunked(true);
			
			SimpleMultipartEntity mpEntity = new SimpleMultipartEntity();
			File vFile = new File(pFileFullPathName);
		    mpEntity.addPart("myfile", pFileFullPathName.substring(pFileFullPathName.lastIndexOf("/")+1), new FileInputStream(vFile), "video/" + pFileFullPathName.substring(pFileFullPathName.lastIndexOf(".") + 1));
			
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
				VideoBrowser.mCurrUploadFileNum = 0;
				for (int fi = 0; fi < VideoBrowser.mUploadFileNameList.size(); ++fi) {
					String lFileFullPathName = VideoBrowser.mUploadFileNameList.get(fi);
					String lFileNameWithoutExt = lFileFullPathName.substring(lFileFullPathName.lastIndexOf("/") + 1, lFileFullPathName.lastIndexOf("."));
					Log.i("UploadFileTask-doInBackground", "upload file " + lFileFullPathName);
					uploadFile(lFileFullPathName);
					++VideoBrowser.mCurrUploadFileNum;
					this.publishProgress();
				}
	        } catch (Exception e) {
				Log.e("UploadFileTask-doInBackground", e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			VideoBrowser.updateUploadFilesProgress();
		}
		
		@Override
		protected void onPostExecute(Object res) {
			VideoBrowser.finishUploadFiles();
			stopService();		//finish the service
		}
	}

}
