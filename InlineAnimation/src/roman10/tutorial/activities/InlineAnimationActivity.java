package roman10.tutorial.activities;

import roman10.tutorial.bitmap.SimpleImageMemCache;
import roman10.tutorial.frameanimation.AnimationAssetsSet;
import roman10.tutorial.frameanimation.AnimationSettings;
import roman10.tutorial.inlineanimation.MyTextView;
import roman10.tutorial.inlineanimation.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;

public class InlineAnimationActivity extends Activity {
	private MyTextView mTextView;
	
	public int convertDpToPixel(Context contex, int dp) {
		Resources r = contex.getResources();
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inline_animation);
		
		SimpleImageMemCache mImageCache = new SimpleImageMemCache(0.20f, convertDpToPixel(this, 100), convertDpToPixel(this, 100));
		
		mTextView = (MyTextView) this.findViewById(R.id.textViewWithAnimation);
//		Logger.d(this, "onCreate " + mTextView.getUID() + ":" + mTextView.getId());
		mTextView.setImageCache(mImageCache);
		mTextView.appendText("inline animation one ");
		mTextView.appendAnimation(new AnimationAssetsSet(this, "1"), new AnimationSettings());
		mTextView.appendText("\ninline animation two ");
		mTextView.appendAnimation(new AnimationAssetsSet(this, "2"), new AnimationSettings());
		mTextView.finishAddingContent();
	}
	

	
	@Override
	public void onResume() {
		super.onResume();
//		Logger.d(this, "onResume " + mTextView.getUID());
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		Logger.d(this, "onPause");
	}
}
