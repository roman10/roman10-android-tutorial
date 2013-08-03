package roman10.tutorial.frameanimation;

import java.util.ArrayList;

import roman10.tutorial.bitmap.SimpleImageMemCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class GifPreview extends ImageView {
    private Bitmap mTmpBitmap;
    private final Handler mHandler = new Handler();
    private ArrayList<String> mGifFrames;
    private AnimationAssetsSet mGifAssets;
    private Context mContext;
    private MyThread mThread;
    private SimpleImageMemCache mImageCache;
    
    private GIF_ASSETS_LOAD_METHOD mLoadMethod;
    enum GIF_ASSETS_LOAD_METHOD {
    	ASSETS, RESOURCES, FILES
    }

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
    		if (mTmpBitmap != null && !mTmpBitmap.isRecycled()) {
    			GifPreview.this.setImageBitmap(mTmpBitmap);
    		}
        }
    };
    
    public GifPreview(Context context) {
		super(context);
		mContext = context;
	}
	
	public GifPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public GifPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	public void setImageCache(SimpleImageMemCache pImageCache) {
		mImageCache = pImageCache;
	}
	
	public void setGifFramePaths(ArrayList<String> pFramePaths) {
		mGifFrames = pFramePaths;
		mLoadMethod = GIF_ASSETS_LOAD_METHOD.FILES;
		
	}
	
	public void setAnimationAssets(AnimationAssetsSet pGifAssets) {
		mGifAssets = pGifAssets;
		mLoadMethod = GIF_ASSETS_LOAD_METHOD.ASSETS;
	}

    public void playGif(final AnimationSettings pGifSettings) {
    	if (null != mThread) {
    		mThread.mIsPlayingGif = false;
    	}
    	mThread = new MyThread(pGifSettings);
        mThread.start();
    }
    
    public void clearBitmap() {
    	if (null != mTmpBitmap && !mTmpBitmap.isRecycled()) {
    		mTmpBitmap.recycle();
    		mTmpBitmap = null;
    	}
    }
    
    class MyThread extends Thread {
    	boolean mIsPlayingGif;
    	AnimationSettings mGifSettings;
    	MyThread(AnimationSettings pGifSettings) {
    		mIsPlayingGif = true;
    		mGifSettings = AnimationSettings.newCopy(pGifSettings);
    	}
    	@Override
    	public void run() {
    		int repetitionCounter = 0;
            do {
                for (int i = 0; i < mGifAssets.getNumOfFrames(); ++i) {
                	if (!mIsPlayingGif) {
                		break;
                	}
                	Log.d(this.getName(), GifPreview.this.getWidth()
                			+ ":" + GifPreview.this.getHeight());
                	switch (mLoadMethod) {
                	case ASSETS:
                		mTmpBitmap = mImageCache.loadBitmap(mContext, mGifAssets.getGifFramePath(i));
                		break;
                	case FILES:
                		mTmpBitmap = mImageCache.loadBitmap(mGifFrames.get(i));
                		break;
                	case RESOURCES:
                		//TODO
                		break;
                	}
                    mHandler.post(mUpdateResults);
                    try {
                        Thread.sleep(mGifSettings.mDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(0 != mGifSettings.mPlaybackTimes) {
                    repetitionCounter++;
                }
            } while (mIsPlayingGif && repetitionCounter <= mGifSettings.mPlaybackTimes);
    	}
    }
    
    public void stopRendering() {
        if (null != mThread) {
        	mThread.mIsPlayingGif = false;
        	mThread.interrupt();
        }
    }
}