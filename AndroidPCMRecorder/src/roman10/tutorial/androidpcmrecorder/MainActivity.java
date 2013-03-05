package roman10.tutorial.androidpcmrecorder;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Button btnControl, btnClear;
	private TextView textDisplay;
	private PcmAudioRecorder mRecorder;
	private static final String mRcordFilePath = Environment.getExternalStorageDirectory() + "/testpcm.pcm";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnControl = (Button) this.findViewById(R.id.btnControl);
		btnControl.setText("Start");
		mRecorder = PcmAudioRecorder.getInstanse();
		mRecorder.setOutputFile(mRcordFilePath);
		btnControl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (PcmAudioRecorder.State.INITIALIZING == mRecorder.getState()) {
					mRecorder.prepare();
					mRecorder.start();
					btnControl.setText("Stop");
				} else if (PcmAudioRecorder.State.ERROR == mRecorder.getState()) {
					mRecorder.release();
					mRecorder = PcmAudioRecorder.getInstanse();
					mRecorder.setOutputFile("/sdcard/testpcm.pcm");
					btnControl.setText("Start");
				} else {
					mRecorder.stop();
					mRecorder.reset();
					btnControl.setText("Start");
				}
			}
		});
		btnClear = (Button) this.findViewById(R.id.btnClear);
		btnClear.setText("Clear");
		btnClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File pcmFile = new File(mRcordFilePath);
				if (pcmFile.exists()) {
					pcmFile.delete();
				}
			}
		});
		textDisplay = (TextView) this.findViewById(R.id.Textdisplay);
		textDisplay.setText("recording saved to: " + mRcordFilePath);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != mRecorder) {
			mRecorder.release();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
