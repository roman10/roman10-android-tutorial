package roman10.tutorial.mmap;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

public class Mmap extends Activity {
    private ByteBuffer bf;
    private TextView display;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bf = (ByteBuffer)naMap();
        
        display = (TextView) findViewById(R.id.display);
        display.setText("");
        byte[] buf = new byte[bf.capacity()];
        Log.i("buf capacity", bf.capacity() + ":");
        bf.get(buf);
        StringBuffer strBuf = new StringBuffer();
        for (int j = 0; j < buf.length; ++j) {
        	Log.i("", buf[j] + ":");
        	strBuf.append(String.valueOf(buf[j]));
        }
        display.append(strBuf.toString() + "\n");
        startUpdateService();
        for (int i = 0; i < 3; ++i) {
        	bf.rewind();
        	bf.get(buf);
        	strBuf.setLength(0);
            for (int j = 0; j < buf.length; ++j) {
            	Log.i("", buf[j] + ":");
            	strBuf.append(String.valueOf(buf[j]));
            }
        	display.append(strBuf.toString() + "\n");
        	SystemClock.sleep(2000);
        }
        stopUpdateService();
        naUnmap();
    }
    
    private void startUpdateService() {
    	Intent lIntent = new Intent(this.getApplicationContext(), roman10.tutorial.mmap.UpdateMemoryService.class);
    	lIntent.putExtra(UpdateMemoryService.START_STOP, 0);
    	startService(lIntent);
    }
    
    private void stopUpdateService() {
    	Intent lIntent = new Intent(this.getApplicationContext(), roman10.tutorial.mmap.UpdateMemoryService.class);
    	lIntent.putExtra(UpdateMemoryService.START_STOP, 1);
    	startService(lIntent);
    }
    
    private static native Object naMap();
    private static native void naUnmap();
    
    //load the native library
    static {
    	System.loadLibrary("mmap");
    	}
}