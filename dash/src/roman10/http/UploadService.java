package roman10.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;


import roman10.media.dash.VideoBrowser;
import roman10.utils.FileUtilsStatic;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
			
			HttpPost httpPost = new HttpPost("http://cervino.ddns.comp.nus.edu.sg/~a0075306/receivefinal.php");
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
	
	public int requestFileStatus(String filePath) {
		int status = 0;
		// Create a new HttpClient 
	    HttpClient httpclient = new DefaultHttpClient();
		try {
			//Post Header
		    HttpPost httppost = new HttpPost("http://cervino.ddns.comp.nus.edu.sg/~a0075306/startupload.php");
//			HttpGet httpget = new HttpGet("http://cervino.ddns.comp.nus.edu.sg/~a0075306/startupload.php");
	        // Add data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			String lFileName = filePath.substring(filePath.lastIndexOf("/")+1);
        	nameValuePairs.add(new BasicNameValuePair("filename[" + 0 + "]", lFileName));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//	        HttpParams params = new BasicHttpParams();
//	        params.setParameter(name, value);
//	        httpget.setParams(params);
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) { 
                Log.e("queryHttp","Response Status line code:"+ response.getStatusLine()); 
		    }
			HttpEntity resEntity = response.getEntity(); 
            if (resEntity == null) { 
                Log.e("queryHttp", "No Response!"); 
            } else {
            	BufferedReader br = new BufferedReader(new InputStreamReader(resEntity.getContent()));
            	String line = br.readLine();
                Log.i("http reply", line);
                status = Integer.valueOf(line);
                br.close();
            }
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    } finally {
	    	httpclient.getConnectionManager().shutdown(); 
	    }
		return status;
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
				//VideoBrowser.mCurrUploadFileNum = 0;
				for (int fi = 0; fi < VideoBrowser.mUploadFileNameList.size(); ++fi) {
					String lFileFullPathName = topUploadFile();
					Log.i("UploadFileTask-doInBackground", "upload file " + lFileFullPathName);
					int status = requestFileStatus(lFileFullPathName);
					Log.i("status", status + ":");
					if (status == 1) {
						//TODO: file already uploaded, we still send, as server 
						//side might not have done transcoding and other stuff yet
						//this should be improved in future release
						uploadFile(lFileFullPathName);
					} else if (status == 0) {
						//file not uploaded, upload it
						uploadFile(lFileFullPathName);
					} else {
						//file partially uploaded, upload the rest
						InputStream myInput = new FileInputStream(lFileFullPathName);
						String tmpFile = FileUtilsStatic.DEFAULT_TMP_DIR + lFileFullPathName.substring(lFileFullPathName.lastIndexOf("/")+1);
						FileOutputStream myOutput = new FileOutputStream(tmpFile);
						byte[] buffer = new byte[1024];
						int length;
						myInput.skip(status);
						while ((length = myInput.read(buffer)) > 0) {
							myOutput.write(buffer, 0, length);
						}
						myOutput.flush();
						myOutput.close();
						myInput.close();
						uploadFile(tmpFile);
						Log.i("file size", new File(lFileFullPathName).length() + ":" + new File(tmpFile).length() + ":" + status);
					}
					removeUploadFile();		//after upload is done, we then remove the item from the queue
					++VideoBrowser.mCurrUploadFileNum;
					this.publishProgress();
				}
	        } catch (Exception e) {
				Log.i("UploadFileTask-doInBackground", "exception");
				if (!mUploadFileQueue.isEmpty()) {
					Log.i("UploadServer", "queue not empty, check if we can resume upload every 10 seconds");
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_ATTACH_DATA);
					intent.putExtra("action", VideoBrowser.upladInterruptedReceiver_ACTION);
					mContext.sendBroadcast(intent);
				}
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
			uploadFileTask = null;
			stopService();		//finish the service
		}
	}

}
