package roman10.tutorial.androidpcmrecorder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

/**
 * Note: Sample vs Frame
 * For encodings like PCM, a frame consists of the set of samples for all channels at a given point in time, in other words,
 * the number of frames in a second is equal to sample rate. 
 * Therefore the size of a frame (in bytes) is always equal to the size of a sample (in bytes) times the number of channels. 
 * However, with some other sorts of encodings a frame can contain a bundle of compressed data for a whole series of samples, 
 * as well as additional, non-sample data. For such encodings, 
 * the sample rate and sample size refer to the data after it is decoded into PCM, 
 * and so they are completely different from the frame rate and frame size.
 */

public class PcmAudioRecorder {
	private final static int[] sampleRates = {44100, 22050, 11025, 8000};
	private int mNumOfChannels = 1;
	
	public static PcmAudioRecorder getInstanse() {
		PcmAudioRecorder result = null;
		int i = 0;
		do {
			result = new PcmAudioRecorder(AudioSource.MIC, 
				sampleRates[i], 
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);		
		} while((++i<sampleRates.length) & !(result.getState() == PcmAudioRecorder.State.INITIALIZING));
		return result;
	}
	
	/**
	* INITIALIZING : recorder is initializing;
	* READY : recorder has been initialized, recorder not yet started
	* RECORDING : recording
	* ERROR : reconstruction needed
	* STOPPED: reset needed
	*/
	public enum State {INITIALIZING, READY, RECORDING, ERROR, STOPPED};
	
	public static final boolean RECORDING_UNCOMPRESSED = true;
	public static final boolean RECORDING_COMPRESSED = false;
	
	// The interval in which the recorded samples are output to the file
	// Used only in uncompressed mode, in milliseconds
	private static final int TIMER_INTERVAL = 120;			
	
	// Recorder used for uncompressed recording
	private AudioRecord     audioRecorder = null;

	// Output file path
	private String          filePath = null;
	
	// Recorder state; see State
	private State          	state;
	
	// File output stream
	private BufferedOutputStream mOutputStream;
		    
	//audio settings
	private int mAudioSource;
	private int mSampleRate;
	private int mChannelConfig;
	private int mAudioFormat;
	
	private short                    mBitsPersample;
	private int                      mBufferSize;
	
	// Number of frames/samples written to file on each output(only in uncompressed mode)
	private int                      mPeriodInFrames;
	
	// Buffer for output(only in uncompressed mode)
	private byte[]                   buffer;
	
	/**
	*
	* Returns the state of the recorder in a WavAudioRecorder.State typed object.
	* Useful, as no exceptions are thrown.
	*
	* @return recorder state
	*/
	public State getState() {
		return state;
	}
	
	private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() {
		//	periodic updates on the progress of the record head
		public void onPeriodicNotification(AudioRecord recorder) {
			if (State.STOPPED == state) {
				Log.d(PcmAudioRecorder.this.getClass().getName(), "recorder stopped");
				return;
			}
			audioRecorder.read(buffer, 0, buffer.length); // read audio data to buffer
			try { 
				mOutputStream.write(buffer); 		  // write audio data to file
			} catch (IOException e) {
				Log.e(PcmAudioRecorder.class.getName(), "Error occured in updateListener, recording is aborted");
				e.printStackTrace();
			}
		}
		//	reached a notification marker set by setNotificationMarkerPosition(int)
		public void onMarkerReached(AudioRecord recorder) {
		}
	};
	/** 
	 * 
	 * 
	 * Default constructor
	 * 
	 * Instantiates a new recorder 
	 * In case of errors, no exception is thrown, but the state is set to ERROR
	 * 
	 */ 
	public PcmAudioRecorder(int audioSource, int sampleRate, int channelConfig, int audioFormat) {
		try {
			mAudioSource = audioSource;
			mSampleRate = sampleRate;
			mChannelConfig = channelConfig;
			mAudioFormat = audioFormat;
			if (channelConfig == AudioFormat.CHANNEL_IN_MONO) {
				mNumOfChannels = 1;
			} else {
				mNumOfChannels = 2;
			}
			if (AudioFormat.ENCODING_PCM_16BIT == mAudioFormat) {
				mBitsPersample = 16;
			} else {
				mBitsPersample = 8;
			}
			mPeriodInFrames = sampleRate * TIMER_INTERVAL / 1000;		//num of frames in a second is same as sample rate
			//refer to android/4.1.1/frameworks/av/media/libmedia/AudioRecord.cpp, AudioRecord::getMinFrameCount method
			//we times 2 for ping pong use of record buffer
			mBufferSize = mPeriodInFrames * 2  * mNumOfChannels * mBitsPersample / 8;		
			if (mBufferSize < AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) { 
				// Check to make sure buffer size is not smaller than the smallest allowed one 
				mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
				// Set frame period and timer interval accordingly
				mPeriodInFrames = mBufferSize / ( 2 * mBitsPersample * mNumOfChannels / 8 );
				Log.w(PcmAudioRecorder.class.getName(), "Increasing buffer size to " + Integer.toString(mBufferSize));
			}
			
			audioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, mBufferSize);
			if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
				throw new Exception("AudioRecord initialization failed");
			}
			audioRecorder.setRecordPositionUpdateListener(updateListener);
			audioRecorder.setPositionNotificationPeriod(mPeriodInFrames);
			filePath = null;
			state = State.INITIALIZING;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				Log.e(PcmAudioRecorder.class.getName(), e.getMessage());
			} else {
				Log.e(PcmAudioRecorder.class.getName(), "Unknown error occured while initializing recording");
			}
			state = State.ERROR;
		}
	}
	
	/**
	 * Sets output file path, call directly after construction/reset.
	 *  
	 * @param output file path
	 * 
	 */
	public void setOutputFile(String argPath) {
		try {
			if (state == State.INITIALIZING) {
				filePath = argPath;
			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				Log.e(PcmAudioRecorder.class.getName(), e.getMessage());
			} else {
				Log.e(PcmAudioRecorder.class.getName(), "Unknown error occured while setting output path");
			}
			state = State.ERROR;
		}
	}
	

	/**
	 * 
	* Prepares the recorder for recording, in case the recorder is not in the INITIALIZING state and the file path was not set
	* the recorder is set to the ERROR state, which makes a reconstruction necessary.
	* In case uncompressed recording is toggled, the header of the wave file is written.
	* In case of an exception, the state is changed to ERROR
	* 	 
	*/
	public void prepare() {
		try {
			if (state == State.INITIALIZING) {
				if ((audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) & (filePath != null)) {
					mOutputStream = new BufferedOutputStream(new FileOutputStream(filePath));
					buffer = new byte[mPeriodInFrames*mBitsPersample/8*mNumOfChannels];
					state = State.READY;
				} else {
					Log.e(PcmAudioRecorder.class.getName(), "prepare() method called on uninitialized recorder");
					state = State.ERROR;
				}
			} else {
				Log.e(PcmAudioRecorder.class.getName(), "prepare() method called on illegal state");
				release();
				state = State.ERROR;
			}
		} catch(Exception e) {
			if (e.getMessage() != null) {
				Log.e(PcmAudioRecorder.class.getName(), e.getMessage());
			} else {
				Log.e(PcmAudioRecorder.class.getName(), "Unknown error occured in prepare()");
			}
			state = State.ERROR;
		}
	}
	
	/**
	 * 
	 * 
	 *  Releases the resources associated with this class, and removes the unnecessary files, when necessary
	 *  
	 */
	public void release() {
		if (state == State.RECORDING) {
			stop();
		} else {
			if (state == State.READY){
				try {
					mOutputStream.close(); // Remove prepared file
				} catch (IOException e) {
					Log.e(PcmAudioRecorder.class.getName(), "I/O exception occured while closing output file");
				}
				(new File(filePath)).delete();
			}
		}
		if (audioRecorder != null) {
			audioRecorder.release();
		}
	}
	
	/**
	 * 
	 * 
	 * Resets the recorder to the INITIALIZING state, as if it was just created.
	 * In case the class was in RECORDING state, the recording is stopped.
	 * In case of exceptions the class is set to the ERROR state.
	 * 
	 */
	public void reset() {
		try {
			if (state != State.ERROR) {
				release();
				audioRecorder = new AudioRecord(mAudioSource, mSampleRate, mChannelConfig, mAudioFormat, mBufferSize);
				if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
					throw new Exception("AudioRecord initialization failed");
				}
				audioRecorder.setRecordPositionUpdateListener(updateListener);
				audioRecorder.setPositionNotificationPeriod(mPeriodInFrames);
				state = State.INITIALIZING;
			}
		} catch (Exception e) {
			Log.e(PcmAudioRecorder.class.getName(), e.getMessage());
			state = State.ERROR;
		}
	}
	
	/**
	 * 
	 * 
	 * Starts the recording, and sets the state to RECORDING.
	 * Call after prepare().
	 * 
	 */
	public void start() {
		if (state == State.READY) {
			audioRecorder.startRecording();
			audioRecorder.read(buffer, 0, buffer.length);	//[TODO: is this necessary]read the existing data in audio hardware, but don't do anything
			state = State.RECORDING;
		} else {
			Log.e(PcmAudioRecorder.class.getName(), "start() called on illegal state");
			state = State.ERROR;
		}
	}
	
	/**
	 * 
	 * 
	 *  Stops the recording, and sets the state to STOPPED.
	 * In case of further usage, a reset is needed.
	 * Also finalizes the wave file in case of uncompressed recording.
	 * 
	 */
	public void stop() {
		if (state == State.RECORDING) {
			audioRecorder.stop();
			try {
				mOutputStream.close();
			} catch(IOException e) {
				Log.e(PcmAudioRecorder.class.getName(), "I/O exception occured while closing output file");
				state = State.ERROR;
			}
			state = State.STOPPED;
		} else {
			Log.e(PcmAudioRecorder.class.getName(), "stop() called on illegal state");
			state = State.ERROR;
		}
	}
}