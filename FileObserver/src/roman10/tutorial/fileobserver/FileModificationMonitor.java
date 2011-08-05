package roman10.tutorial.fileobserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//use asynctask background thread to detect file modification and then update the UI once there is 
//a new change detected
public class FileModificationMonitor extends Activity  {
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private TextView text1;
	//private boolean stop_capture;
	private static boolean started = false;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_modification_monitor);
        btn1 = (Button) findViewById(R.id.file_modification_monitor_btn_first);
        btn2 = (Button) findViewById(R.id.file_modification_monitor_btn_second);
        btn3 = (Button) findViewById(R.id.file_modification_monitor_btn_third);
        btn4 = (Button) findViewById(R.id.file_modification_monitor_btn_forth);
        int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        btn1.setWidth(screenWidth/4);
        text1 = (TextView) findViewById(R.id.file_modification_monitor_log);
        //stop_capture = false;
        if (!started) {
        	startService(new Intent(FileModificationMonitor.this.getApplicationContext(), FileModificationService.class));
			btn1.setEnabled(false);
			started = true;
        }
        btn1.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				startService(new Intent(FileModificationMonitor.this.getApplicationContext(), FileModificationService.class));
				btn1.setEnabled(false);
				started = true;
			}
		});
        btn2.setWidth(screenWidth/4);
        btn2.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				//stop_capture = true;
				stopService(new Intent(FileModificationMonitor.this.getApplicationContext(), FileModificationService.class));
				btn1.setEnabled(true);
				started = false;
			}
		});
        btn3.setWidth(screenWidth/4);
        btn3.setOnClickListener(new View.OnClickListener() {	
			//@Override
			public void onClick(View v) {
				text1.setText(FileAccessLogStatic.accessLogMsg);
			}
		});
        btn4.setWidth(screenWidth/4);
        btn4.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				clearLog();
			}
		});
        refreshLog();
	}
	
	
	public void refreshLog() {
		text1.setText(FileAccessLogStatic.accessLogMsg);
	}
	
	public void clearLog() {
		FileAccessLogStatic.accessLogMsg = "";
		text1.setText("");
	}
}
