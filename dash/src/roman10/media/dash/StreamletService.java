package roman10.media.dash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.coremedia.iso.IsoBufferWrapperImpl;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoOutputStream;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class StreamletService extends Service {
	private Context mContext;
	private PowerManager.WakeLock wl;	//lock used when generateStreamletTask is on-going  
	private static final int STREAMLET_INTERVAL = 10;

	@Override
	public void onCreate() {
		mContext = this.getApplicationContext();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
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

	private class generateStreamletTask extends AsyncTask<Object, Integer, Object> {
		@Override
		protected void onPreExecute() {
			/*acquire PARTIAL_WAKE_LOCK WakeLock, so that the processing can continue even when screen is off*/
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, 
					"generateStreamletTask");
			wl.acquire();
		}
		
		private byte[] readFully(InputStream pIs) throws IOException {
			ByteArrayOutputStream lBaos = new ByteArrayOutputStream();
			byte[] lBuf = new byte[2048];
			int ln = 0;
			while (-1 != (ln = pIs.read(lBuf))) {
				lBaos.write(lBuf, 0, ln);
			}
			return lBaos.toByteArray();
		}
		private long getVideoLengthInSeconds(String pFileName) {
			try {
				IsoFile lIsoFile = new IsoFile(new IsoBufferWrapperImpl(readFully(new FileInputStream(pFileName))));
				lIsoFile.parse();
				return lIsoFile.getMovieBox().getMovieHeaderBox().getDuration() /
				lIsoFile.getMovieBox().getMovieHeaderBox().getTimescale();
			} catch (IOException e) {
				Log.e("streamletService-getVideoLengthInSeconds", e.getMessage());
			}
			return 0;
		}
		private double[] timeOfSyncSamples;
	    private void getDecodingTimeForSyncSamples(Track track) {
	    	timeOfSyncSamples = new double[track.getSyncSamples().length];
	    	long lCurSample = 0;
	    	double lCurTime = 0;
	    	for (int i = 0; i < track.getDecodingTimeEntries().size(); ++i) {
	    		TimeToSampleBox.Entry lEntry = track.getDecodingTimeEntries().get(i);
	    		for (int j = 0; j < lEntry.getCount(); ++j) {
	    			//check if the current sample is one of the sync sample, if so, put it into timeOfSyncSamples
	    			int lIndex = Arrays.binarySearch(track.getSyncSamples(), lCurSample + 1);
	    			if (lIndex >= 0) {
	    				timeOfSyncSamples[lIndex] = lCurTime;
	    			}
	    			lCurTime += (double) lEntry.getDelta() / (double) track.getTrackMetaData().getTimescale();
	    			lCurSample++;
	    		}
	    	}
	    }
	    private double correctTimeToNextSyncSample(double cutHere) {
	    	for (double timeOfSyncSample : timeOfSyncSamples) {
	    		if (timeOfSyncSample > cutHere) {
	    			return timeOfSyncSample;
	    		}
	    	}
	    	//the cutHere time is after the end of the video, we return the end of the video
	    	return timeOfSyncSamples[timeOfSyncSamples.length - 1];
	    }
		@Override
		protected Object doInBackground(Object... arg0) {
			 try {
				//0. calculate roughly how many streamlets we'll generate, this is for report the progress
				Main.mTotalNumStreamlets = 0;
				Main.mCurrProcessStreamletNum = 0;
				for (int fi = 0; fi < Main.displayEntries.size(); ++fi) {
					if (Main.mSelected.get(fi)) {
						Main.mTotalNumStreamlets = getVideoLengthInSeconds(Main.displayEntries.get(fi).getText())/STREAMLET_INTERVAL + 1;
					}
				}
				//1. generate the streamlets 
				for (int fi = 0; fi < Main.displayEntries.size(); ++fi) {
					if (Main.mSelected.get(fi)) {
						Movie movie = new MovieCreator().build(new IsoBufferWrapperImpl(new File(Main.displayEntries.get(fi).getText())));

						List<Track> tracks = movie.getTracks();
						movie.setTracks(new LinkedList<Track>());//remove all tracks we will create new tracks from the old
						
						double startTime = 15.000;
						double endTime = 45.000;
						boolean timeCorrected = false;
						
						//here we try to find a track that has sync samples. Since we can only start decoding
						//at such a sample we SHOULD make sure that the start of the new fragment is exactly 
						//such a frame
						for (Track track : tracks) {
							long[] syncSamples = track.getSyncSamples();
							if (syncSamples != null && syncSamples.length > 0) {
								for (int i = 0; i < syncSamples.length; ++i) {
									Log.i("dash-main-syncSample", syncSamples[i] + ":");
								}
								getDecodingTimeForSyncSamples(track);
								startTime = correctTimeToNextSyncSample(startTime);
								endTime = correctTimeToNextSyncSample(endTime);
								timeCorrected = true;
							}
						}
						for (Track track : tracks) {
							long currentSample = 0;
							double currentTime = 0;
							long startSample = -1;
							long endSample = -1;
							for (int i = 0; i < track.getDecodingTimeEntries().size(); ++i) {
								TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
								for (int j = 0; j < entry.getCount(); ++j) {
									//entry.getDelta() is the amount of time the current sample covers
									if (currentTime <= startTime) {
										//current sample is still before the new starttime
										startSample = currentSample;
									}
									if (currentTime <= endTime) {
										//current sample is after the new start time and still before the 
										//new endtime
										endSample = currentSample;
									} else {
										//current sample is after the end of the cropped video
										break;
									}
									currentTime += (double) entry.getDelta() / (double)track.getTrackMetaData().getTimescale();
									currentSample++;
								}
							}
							movie.addTrack(new CroppedTrack(track, startSample, endSample));
						}
						IsoFile out = new DefaultMp4Builder().build(movie);
						FileOutputStream fos = new FileOutputStream(new File(String.format("/sdcard/r10videocam/output-%f-%f.mp4", startTime, endTime)));
						out.getBox(new IsoOutputStream(fos));
						fos.close();
					}
				}
	        } catch (IOException e) {
				Log.e("Main-onCreate", e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			
		}
		
		@Override
		protected void onPostExecute(Object res) {
			stopService();		//finish the service
		}
	}
}
