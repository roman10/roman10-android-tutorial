package roman10.tutorial.propertyanimation.valueanimator;

import roman10.tutorial.propertyanimation.R;
import roman10.tutorial.propertyanimation.Utils;

import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.animation.AnimatorProxy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ValueAnimatorDemo extends Activity implements ValueAnimator.AnimatorUpdateListener {
	private AnimatorProxy mImageAnimatorProxy;
	private ImageView mImageView;
	private float mOriX, mOriY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_value_animator);
		
		mImageView = (ImageView) this.findViewById(R.id.image);
		mImageAnimatorProxy = AnimatorProxy.wrap(mImageView);
		
		final FrameLayout container = (FrameLayout) this.findViewById(R.id.container);
		
		final Button mBtnStart = (Button) this.findViewById(R.id.btnStart);
		mBtnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOriX = mImageAnimatorProxy.getX();
				mOriY = mImageAnimatorProxy.getY();
				PropertyValuesHolder widthPropertyHolder = PropertyValuesHolder.ofFloat("posX", mImageAnimatorProxy.getX(), container.getWidth() - mImageView.getWidth());
				PropertyValuesHolder heightPropertyHolder = PropertyValuesHolder.ofFloat("posY", mImageAnimatorProxy.getY(), 0);
				ValueAnimator mTranslationAnimator = ValueAnimator.ofPropertyValuesHolder(widthPropertyHolder, heightPropertyHolder);
				mTranslationAnimator.addUpdateListener(ValueAnimatorDemo.this);
				mTranslationAnimator.setDuration(1000);
				mTranslationAnimator.start();
				mBtnStart.setEnabled(false);
			}
		});
		Button mBtnReset = (Button) this.findViewById(R.id.btnReset);
		mBtnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mBtnStart.setEnabled(true);
				mImageAnimatorProxy.setX(mOriX);
				mImageAnimatorProxy.setY(mOriY);
			}
		});
	}

	@Override
	public void onAnimationUpdate(ValueAnimator arg0) {
		float posX = (Float) arg0.getAnimatedValue("posX");
		float posY = (Float) arg0.getAnimatedValue("posY");
		mImageAnimatorProxy.setX(posX);
		mImageAnimatorProxy.setY(posY);
	}
}
