package roman10.tutorial.inlineanimation;

import roman10.tutorial.bitmap.SimpleImageMemCache;
import roman10.tutorial.frameanimation.AnimationAssetsSet;
import roman10.tutorial.frameanimation.AnimationSettings;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.style.DynamicDrawableSpan;

public class AnimatedImageSpan extends DynamicDrawableSpan {
	private AnimationAssetsSet mGifAssets;
	private int mCurrentFrameIdx;
	private Context mContext;
	
	private SimpleImageMemCache mImageCache;
	private AnimatedImageUpdateHandler mImageUpdater;
	
	private final Handler handler = new Handler();
	
	public AnimatedImageSpan(Context context) {
		mContext = context;
	}
	
	public void setImageCache(SimpleImageMemCache pImageCache) {
		mImageCache = pImageCache;
	}
	
	public void setAnimationAssets(AnimationAssetsSet pGifAssets) {
		mGifAssets = pGifAssets;
	}
	
	private Runnable mRunnable;
	private int mPlaybackTimes;
	private boolean mPlaying;
	public void playGif(final AnimationSettings pGifSettings, AnimatedImageUpdateHandler pListener) {
		mPlaying = true;
		mImageUpdater = pListener;
		mPlaybackTimes = 0;
		mRunnable = new Runnable() {
	        public void run() {
	        	mCurrentFrameIdx = (mCurrentFrameIdx + 1)%mGifAssets.getNumOfFrames();
//	        	Logger.d(this, "current frame " + mCurrentFrameIdx);
	        	handler.postDelayed(this, pGifSettings.mDelay);
	        	if (null != mImageUpdater) {
//	        		Logger.d(this, "update frame using listener " + mImageUpdater.getId());
	        		mImageUpdater.updateFrame();
	        	}
	        	if (mCurrentFrameIdx == mGifAssets.getNumOfFrames() - 1) {
	        		if (pGifSettings.mPlaybackTimes == 0) {
	        			//repeat forever
	        		} else {
	        			mPlaybackTimes++;
	        			if (mPlaybackTimes == pGifSettings.mPlaybackTimes) {
	        				stopRendering();
	        			}
	        		}
	        	}
	        }
		};
		handler.post(mRunnable);
	}
	
	public boolean isPlaying() {
		return mPlaying;
	}
	
	public void stopRendering() {
		handler.removeCallbacks(mRunnable);
		mPlaying = false;
	}
	
	@Override
	public Drawable getDrawable() {
		Bitmap bitmap = mImageCache.loadBitmap(mContext, mGifAssets.getGifFramePath(mCurrentFrameIdx));
		BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		drawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
		return drawable;
	}

	@Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
//		Logger.d(this, "draw " + mCurrentFrameIdx);
        Drawable b = getDrawable();
        canvas.save();

        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
