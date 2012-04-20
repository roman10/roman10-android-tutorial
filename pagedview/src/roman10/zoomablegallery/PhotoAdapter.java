package roman10.zoomablegallery;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import roman10.tutorial.ui.pagedview.PagedAdapter;
import roman10.tutorial.ui.pagedview.R;

public class PhotoAdapter extends PagedAdapter {
//	int mGalleryItemBackground;
	private Context mContext;
	private List<String> imageFiles = new ArrayList<String>(); 
	private List<Integer> imageOriList = new ArrayList<Integer>();
	public PhotoAdapter(Context _context) {
		// See res/values/attrs.xml for the <declare-styleable> that defines
        // Gallery1.
		mContext = _context;
        //TypedArray a = iContext.obtainStyledAttributes(R.styleable.Gallery2);
        //mGalleryItemBackground = a.getResourceId(
                //R.styleable.Gallery2_android_galleryItemBackground, 0);
//        mGalleryItemBackground = 0;	//no background
        //a.recycle();
	}
	
//	public void setListItem(List<String> _list) {
//		imageFiles = _list;
//	}
	
	public void setListItem(List<String> _list) {
		imageFiles = _list;
	}
	
	public void setImageOriList(List<Integer> _list) {
		imageOriList = _list;
	}
	
//	public void setIds(List<Integer> _list) {
//		Ids = _list;
//	}
	
	//this is a hack to let the gallery start from last to first.
	public int getCount() {
//		return Integer.MAX_VALUE;
		return imageFiles.size();
	}

	public int getRealCount() {
		return imageFiles.size();
	}
	
	public Object getItem(int position) {
		return imageFiles.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		arg0 = arg0%(imageFiles.size());
		ImageZoomView zoomView;
		if (arg1 != null) {
			zoomView = (ImageZoomView)arg1;
		} else {
			zoomView = new ImageZoomView(mContext, imageOriList.get(arg0), arg0);
		}
	    /** Decoded bitmap image */
	    Bitmap mBitmap = null;
	    //mBitmap = BitmapFactory.decodeResource(mContext.getResources(), imageDrawables.get(arg0));
        
	    InputStream is;
		try {
			 is = new FileInputStream(imageFiles.get(arg0));
		} catch (FileNotFoundException e) {
			//return null will cause crash, here we introduce an image indicating the image is in decryption...
			//return null;
			 is = mContext.getResources().openRawResource(R.drawable.android3);
		}
		//Toast.makeText(iContext, "photo " + arg0, Toast.LENGTH_LONG).show();
		
		try {
			boolean decode_stream_status = false;
			int sample_size = 1;
			BitmapFactory.Options options = new BitmapFactory.Options();
			while (true) {
				  try {
					  decode_stream_status = false;
					  options.inSampleSize = sample_size;
					  mBitmap = BitmapFactory.decodeStream(is, null, options);
					  decode_stream_status = true;
				  } catch (OutOfMemoryError e) {
					  Log.e("PhotoAdapter-getView", e.getMessage());
					  //System.gc();
					  sample_size *= 2;
					  try {
						  //TODO: cannot we use the old filestream???
						  is = new FileInputStream(imageFiles.get(arg0));
					  } catch (FileNotFoundException ex) {
						  is = mContext.getResources().openRawResource(R.drawable.android3);
					  }
				  } 
				  if (decode_stream_status) {
					  break;
				  }
			}
			//l_photo.setImageURI(l_photoUri);
			//l_photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
		} catch (OutOfMemoryError mem) {
			Log.e("PhotoAdapter-getView-2", mem.getMessage());
			//System.gc();
		}
        
//        Gallery.LayoutParams lParams = new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.FILL_PARENT);
//        zoomView.setLayoutParams(lParams);    
        if (mBitmap == null) {
        	while (true) {
			    try {
	        		mBitmap = ((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.android3)).getBitmap();
	        		break;
	        	} catch (OutOfMemoryError mem) {
	        		Log.e("PhotoAdapter-getView-3", mem.getMessage());
	        		//System.gc();
	        	}
        	}
        }
        zoomView.setImage(mBitmap);

        return zoomView;
	}
}
