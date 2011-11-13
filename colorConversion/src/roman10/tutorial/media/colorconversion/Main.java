package roman10.tutorial.media.colorconversion;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class Main extends Activity {
	private RenderView prRenderView;
	private Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        /*force 32-bit display for better display quality*/
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    	lp.copyFrom(getWindow().getAttributes());
    	lp.format = PixelFormat.RGBA_8888;
        getWindow().setBackgroundDrawable(new ColorDrawable(0xff000000));
        getWindow().setAttributes(lp);
        /*copy the test yuv file*/
        CopyAssets();
        String lTestFilePath = "/sdcard/test.yuv";
        int lTestFileWidth = 672;
        int lTestFileHeight = 336;
        prRenderView = new RenderView(this, lTestFilePath, lTestFileWidth, lTestFileHeight);
        setContentView(prRenderView);
    }
    
	private void CopyAssets() {
	    AssetManager assetManager = getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list("");
	    } catch (IOException e) {
	        Log.e("tag", e.getMessage());
	    }
	    for(String filename : files) {
	        InputStream in = null;
	        OutputStream out = null;
	        try {
	          in = assetManager.open(filename);
	          out = new FileOutputStream("/sdcard/" + filename);
	          copyFile(in, out);
	          in.close();
	          in = null;
	          out.flush();
	          out.close();
	          out = null;
	        } catch(Exception e) {
	            Log.e("tag", e.getMessage());
	        }       
	    }
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	private void handleMoreApps() {
    	Intent l_intent = new Intent();
		l_intent.setClass(mContext, AppsListDialog.class);
		startActivity(l_intent);
    }
	
	int showMoreAppsCount = 0;
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (showMoreAppsCount == 0) {
        		showMoreAppsCount++;
        		handleMoreApps();
        		Toast.makeText(mContext, "Press back key twice to exit!", Toast.LENGTH_SHORT).show();
        		return true;
        	} else {
    			this.finish();
    		}
        	
        }
        return super.onKeyDown(keyCode, event);
    }
    
    //load the native library
    static {
    	System.loadLibrary("yuv2rgb");}
}