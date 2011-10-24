package roman10.media.dash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import roman10.utils.FileUtilsStatic;

import com.coremedia.iso.IsoBufferWrapper;
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


//CONTINUE from here:
//DefaultMP4Builder movieBoxChildren.add(createTrackBox(track, movie)); cause crash

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
		Log.i("onStart", "start generating streamlet");
		generateStreamletTask genStreamletTask = new generateStreamletTask();
		genStreamletTask.execute(null);
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
		private double getVideoLengthInSeconds(String pFileName) {
			try {
				//IsoFile lIsoFile = new IsoFile(new IsoBufferWrapperImpl(readFully(new FileInputStream(pFileName))));
				IsoFile lIsoFile = new IsoFile(new IsoBufferWrapperImpl(new File(pFileName)));
				lIsoFile.parse();
				return (double)lIsoFile.getMovieBox().getMovieHeaderBox().getDuration() /(double)
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
	    
	    private void copyFile(String file, String outFileName) {
	    	InputStream input;
			try {
				input = new FileInputStream(file);
				OutputStream myOutput = new FileOutputStream(outFileName);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = input.read(buffer))>0){
					myOutput.write(buffer, 0, length);
				}
				myOutput.flush();
				myOutput.close();
				input.close();
			} catch (FileNotFoundException e) {
				Log.e("copyFile", e.getMessage());
			} catch (IOException e) {
				Log.e("copyFile", e.getMessage());
			}
	    }
		@Override
		protected Object doInBackground(Object... arg0) {
			 try {
				//0. calculate roughly how many streamlets we'll generate, this is for report the progress
				VideoBrowser.mTotalNumStreamlets = 0;
				VideoBrowser.mCurrProcessStreamletNum = 0;
				for (int fi = 0; fi < VideoBrowser.displayEntries.size(); ++fi) {
					if (VideoBrowser.mSelected.get(fi)) {
						String lFileFullPathName = VideoBrowser.displayEntries.get(fi).getText();
						String lFileNameWithoutExt = lFileFullPathName.substring(lFileFullPathName.lastIndexOf("/") + 1, lFileFullPathName.lastIndexOf("."));
						double lVideoLen = getVideoLengthInSeconds(lFileFullPathName);
						VideoBrowser.mTotalNumStreamlets = (int)lVideoLen/STREAMLET_INTERVAL + 1;
						if (lVideoLen <= STREAMLET_INTERVAL) {
							//the video is less than 10 seconds, we simple rename and copy it to streamlet folder
							copyFile(lFileFullPathName, String.format(FileUtilsStatic.DEFAULT_STREAMLET_DIR + "%s-%.2f-%.2f.mp4", lFileNameWithoutExt, 0.0, lVideoLen));
							VideoBrowser.mSelected.set(fi, false);
							VideoBrowser.mCurrProcessStreamletNum++;
						}
					}
				}
				//1. generate the streamlets 
				for (int fi = 0; fi < VideoBrowser.displayEntries.size(); ++fi) {
					if (VideoBrowser.mSelected.get(fi)) {
						String lFileFullPathName = VideoBrowser.displayEntries.get(fi).getText();
						String lFileNameWithoutExt = lFileFullPathName.substring(lFileFullPathName.lastIndexOf("/") + 1, lFileFullPathName.lastIndexOf("."));
						Movie movie = new MovieCreator().build(new IsoBufferWrapperImpl(new File(VideoBrowser.displayEntries.get(fi).getText())));

						List<Track> tracks = movie.getTracks();
						Log.i("Streamlet-Service", "number of tracks: " + tracks.size());
						movie.setTracks(new LinkedList<Track>());//remove all tracks we will create new tracks from the old

						//TODO: need to pay attention to memory usage, if the video is big, this method might
						//not be able to handle
						long currentSample = 0;
						double startTime = 0;
						double currentTime = 0;
						List<StreamletRecord> streamletsRec = new ArrayList<StreamletRecord>();
						//1.0 find the time from the track that contains sync samples, should be the video track
						for (Track track : tracks) {
							if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
								//found the track that contains sync samples
								for (int i = 0; i < track.getDecodingTimeEntries().size(); ++i) {
						    		TimeToSampleBox.Entry lEntry = track.getDecodingTimeEntries().get(i);
						    		for (int j = 0; j < lEntry.getCount(); ++j) {
						    			//check if the current sample is one of the sync sample, if so, put it into timeOfSyncSamples
						    			currentTime += (double) lEntry.getDelta() / (double) track.getTrackMetaData().getTimescale();
						    			currentSample++;
						    			if (currentTime - startTime > STREAMLET_INTERVAL) {
						    				//check if the next sample is a sync sample, if so, we crop the track
						    				if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
						    					streamletsRec.add(new StreamletRecord(startTime, currentTime));
						    					startTime = currentTime;
						    				}
						    			}
						    		}
								}
								if (currentTime > startTime) {
									streamletsRec.add(new StreamletRecord(startTime, currentTime));
								}
							}
						}
						//1.1 crop the video according to the time found
						long[] currentSampleForAllTracks = new long[tracks.size()];
						for (int i = 0; i < currentSampleForAllTracks.length; ++i) {
							currentSampleForAllTracks[i] = 1;
						}
						for (int cnt = 0; cnt < tracks.size(); ++cnt) {
							Track track = tracks.get(cnt);
							long[] tt = track.getSyncSamples();
							if (tt != null) {
								for (int x = 0; x < tt.length; ++x) {
									Log.e("Streamlet-service", "sync sample: " + tt[x]);
								}
							}
						}
						double[] currentTimeForAllTracks = new double[tracks.size()];
						int[] js = new int[tracks.size()];
						int[] ks = new int[tracks.size()];
						for (int i = 0; i < streamletsRec.size(); ++i) {
							for (int cnt = 0; cnt < tracks.size(); ++cnt) {
								Track track = tracks.get(cnt);
								long startSample = currentSampleForAllTracks[cnt], endSample = currentSampleForAllTracks[cnt];
								boolean foundEnd = false;
								for (; js[cnt] < track.getDecodingTimeEntries().size(); ++js[cnt]) {
									TimeToSampleBox.Entry lEntry = track.getDecodingTimeEntries().get(js[cnt]);
									for (; ks[cnt] < lEntry.getCount(); ++ks[cnt]) {
										 currentTimeForAllTracks[cnt] += (double) lEntry.getDelta() / (double) track.getTrackMetaData().getTimescale();
										 currentSampleForAllTracks[cnt]++;
										 if (currentTimeForAllTracks[cnt] >= streamletsRec.get(i).endTime) {
											 endSample = currentSampleForAllTracks[cnt];
											 foundEnd = true;
											 break;
										 }
									}
									if (ks[cnt] == lEntry.getCount()) {ks[cnt] = 0;}
									if (foundEnd) break;
								}
								//as CroppedTrack is [Start, end)
								Log.e("StreamletService", "track " + cnt + ": " + startSample + "-" + (endSample));
								movie.addTrack(new CroppedTrack(track, startSample, endSample));
							}
							//dump all segmented tracks to file
	    					IsoFile out = new DefaultMp4Builder().build(movie);
							FileOutputStream fos = new FileOutputStream(new File(String.format(FileUtilsStatic.DEFAULT_STREAMLET_DIR + "%s-%.2f-%.2f.mp4", lFileNameWithoutExt, startTime, currentTime)));
							Log.i("StreamletService", String.format(FileUtilsStatic.DEFAULT_STREAMLET_DIR + "%s-%.2f-%.2f.mp4", lFileNameWithoutExt, streamletsRec.get(i).startTime, streamletsRec.get(i).endTime));
							out.getBox(new IsoOutputStream(fos));
							fos.close();
							movie.setTracks(new LinkedList<Track>());//remove all tracks we will create new tracks from the old
							++VideoBrowser.mCurrProcessStreamletNum;
							this.publishProgress();
						}
					}
				}
	        } catch (IOException e) {
				Log.e("VideoBrowser-onCreate", e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			VideoBrowser.updateGeneratingStreamletProgress();
		}
		
		@Override
		protected void onPostExecute(Object res) {
			VideoBrowser.finishGeneratingStreamlet();
			stopService();		//finish the service
		}
	}
}
