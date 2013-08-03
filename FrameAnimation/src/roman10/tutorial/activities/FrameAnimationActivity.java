package roman10.tutorial.activities;

import roman10.tutorial.bitmap.SimpleImageMemCache;
import roman10.tutorial.frameanimation.AnimationAssetsSet;
import roman10.tutorial.frameanimation.AnimationSettings;
import roman10.tutorial.frameanimation.GifPreview;
import roman10.tutorial.frameanimation.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Menu;

public class FrameAnimationActivity extends Activity {
	private GifPreview mGifPreview;
	private SimpleImageMemCache mImageCache;
	private Context mContext;
	
	public int convertDpToPixel(int dp) {
		Resources r = mContext.getResources();
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_frame_animation);
		mGifPreview = (GifPreview) this.findViewById(R.id.gifPreviewOne);
		mImageCache = new SimpleImageMemCache(0.20f, convertDpToPixel(320), convertDpToPixel(200));
		mGifPreview.setImageCache(mImageCache);
		mGifPreview.setAnimationAssets(new AnimationAssetsSet(this, "1"));
	}

	@Override
	public void onResume() {
		super.onResume();
		mGifPreview.playGif(new AnimationSettings());
	}

	@Override
	public void onPause() {
		super.onPause();
		mGifPreview.stopRendering();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mGifPreview.clearBitmap();
		mImageCache.clearCache();
	}
}
