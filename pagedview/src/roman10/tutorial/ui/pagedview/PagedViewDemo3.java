package roman10.tutorial.ui.pagedview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import roman10.zoomablegallery.PhotoAdapter;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public class PagedViewDemo3 extends Activity {
	private static final String TAG = "PagedViewDemo3";
	public static final String TEST_DIR = "/sdcard/paged/";
//	private static final int PAGE_COUNT = 10000000;
//  private static final int PAGE_MAX_INDEX = PAGE_COUNT;
    
    private PhotoAdapter mAdapter;
    private List<String> mImageFiles = new ArrayList<String>(); 
	private List<Integer> mImageOriList = new ArrayList<Integer>();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paged_view_frame);
        final FrameLayout contentView = (FrameLayout) this.findViewById(R.id.content_view);
        LayoutInflater.from(this).inflate(R.layout.paged_view_demo3, contentView);
        
        final PagedView pagedView = (PagedView)findViewById(R.id.paged_view3);
        mAdapter = new PhotoAdapter(this.getApplicationContext());
        initImages();
        mAdapter.setListItem(mImageFiles);
        mAdapter.setImageOriList(mImageOriList);
        pagedView.setAdapter(mAdapter);
	}
    
	private void initImages() {
		initDirs();
		copyAssets();
		File f = new File(TEST_DIR);
		for (File aFile : f.listFiles()) {
			mImageFiles.add(aFile.getAbsolutePath());
			mImageOriList.add(0);
		}
	}
	
	private void copyAssets() {
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
              out = new FileOutputStream(TEST_DIR + filename);
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
    
    public static void initDirs() {
    	createDirIfNotExist(TEST_DIR);
    }

    public static void createDirIfNotExist(String _path) {
		File f = new File(_path);
		try {
			if (f.exists()) {
				//directory already exists
			} else {
				if (f.mkdirs()) {
					Log.v(TAG, "createDirIfNotExist created " + _path);
				} else {
					Log.v(TAG, "createDirIfNotExist failed to create " + _path);
				}
			}
		} catch (Exception e) {
			//create directory failed
			Log.v(TAG, "createDirIfNotExist failed to create " + _path);
		}
	}
}
