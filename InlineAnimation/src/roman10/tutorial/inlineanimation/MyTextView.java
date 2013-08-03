package roman10.tutorial.inlineanimation;

import java.util.ArrayList;
import java.util.UUID;

import roman10.tutorial.bitmap.SimpleImageMemCache;
import roman10.tutorial.frameanimation.AnimationAssetsSet;
import roman10.tutorial.frameanimation.AnimationSettings;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MyTextView extends TextView {
//	private String mId = UUID.randomUUID().toString();
	private SpannableStringBuilder mSb = new SpannableStringBuilder();
	private String dummyText = "dummy " + System.currentTimeMillis();
	private Context mContext;
	private SimpleImageMemCache mImageCache;
	private ArrayList<AnimatedImageSpan> mAnimatedImages = new ArrayList<AnimatedImageSpan>();

//	public String getUID() {
//		return mId;
//	}
	
	public MyTextView(Context context) {
		super(context);
		mContext = context;
	}
	
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	public void setImageCache(SimpleImageMemCache pImageCache) {
		mImageCache = pImageCache;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.d(this.getClass().getName(), "onDetachedFromWindow ");
		for (AnimatedImageSpan ais : mAnimatedImages) {
			Log.d(this.getClass().getName(), "animation playing " + ais.isPlaying());
			if (ais.isPlaying()) {
				ais.stopRendering();
			}
		}
		mAnimatedImages.clear();
		mSb.clearSpans();
		mSb.clear();
	}
	
	public void appendText(String pStr) {
		mSb.append(pStr);
	}
	
	public void appendAnimation(AnimationAssetsSet pAsset, AnimationSettings pSettings) {
		mSb.append(dummyText);
		AnimatedImageSpan ais = new AnimatedImageSpan(mContext);
		ais.setImageCache(mImageCache);
		ais.setAnimationAssets(pAsset);
		mSb.setSpan(ais, mSb.length() - dummyText.length(), mSb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		AnimationClickableSpan clickSpan = new AnimationClickableSpan(this, ais, pSettings);
//		Log.d(this.getClass().getName(), "clickSpan " + clickSpan.mlUUID);
		mSb.setSpan(clickSpan, mSb.length() - dummyText.length(), mSb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mAnimatedImages.add(ais);
	}
	
	public void finishAddingContent() {
		this.setText(mSb);
		this.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	private static class AnimationClickableSpan extends ClickableSpan {
		AnimatedImageSpan mAnimatedImage;
		AnimationSettings mSettings;
		AnimatedImageUpdateHandler mHandler;
//		String mlUUID;
		AnimationClickableSpan(MyTextView pView, AnimatedImageSpan pSpan, AnimationSettings pSettings) {
			mAnimatedImage = pSpan;
			mSettings = pSettings;
			mHandler = new AnimatedImageUpdateHandler(pView);
//			mlUUID = UUID.randomUUID().toString();
//			Logger.d(this, "listener " + mHandler.getId() + " created using view " + pView.getUID() + ":" + pView.getId());
		}
		
		@Override
		public void onClick(View widget) {
			MyTextView view = (MyTextView) widget;
//			Log.d(this.getClass().getName(), "animation clicked " + view.getUID() + " triggered by spannable :" + mlUUID);
			if (mAnimatedImage.isPlaying()) {
				mAnimatedImage.stopRendering();
			} else {
				mAnimatedImage.playGif(mSettings, mHandler);
			}
		}
	}
}


