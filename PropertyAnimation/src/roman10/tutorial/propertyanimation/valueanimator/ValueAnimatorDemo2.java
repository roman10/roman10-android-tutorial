package roman10.tutorial.propertyanimation.valueanimator;

import roman10.tutorial.propertyanimation.R;
import roman10.tutorial.propertyanimation.Utils;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.animation.AnimatorProxy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ValueAnimatorDemo2 extends Activity implements ValueAnimator.AnimatorUpdateListener {
	private AnimatorProxy mImageAnimatorProxy;
	private float mOriX, mOriY;
	
	private class Position {
		private float posX;
		private float posY;
		
		public float getPosX() {
			return posX;
		}
		
		public float getPosY() {
			return posY;
		}
		
		Position(float pPosX, float pPosY) {
			posX = pPosX;
			posY = pPosY;
		}
	}
	
	private class PositionTypeEvaluator implements TypeEvaluator<Position> {
		@Override
		public Position evaluate(float fraction, Position startValue, Position endValue) {
			float posX = startValue.getPosX() + (endValue.getPosX() - startValue.getPosX()) * fraction;
			float posY = startValue.getPosY() + (endValue.getPosY() - startValue.getPosY()) * fraction;
			return new Position(posX, posY);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_value_animator);
		
		final ImageView mImageView = (ImageView) this.findViewById(R.id.image);
		mImageAnimatorProxy = AnimatorProxy.wrap(mImageView);
		
		final FrameLayout container = (FrameLayout) this.findViewById(R.id.container);

		final Button mBtnStart = (Button) this.findViewById(R.id.btnStart);
		mBtnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOriX = mImageAnimatorProxy.getX();
				mOriY = mImageAnimatorProxy.getY();
				ValueAnimator mAnimator = ValueAnimator.ofObject(new PositionTypeEvaluator(), new Position(mOriX, mOriY),
						new Position(container.getWidth() - mImageView.getWidth(), 0));
				mAnimator.addUpdateListener(ValueAnimatorDemo2.this);
				mAnimator.setDuration(1000);
				mAnimator.start();
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
	public void onAnimationUpdate(ValueAnimator pAnimator) {
		Position currentPos = (Position) pAnimator.getAnimatedValue();
		mImageAnimatorProxy.setX(currentPos.getPosX());
		mImageAnimatorProxy.setY(currentPos.getPosY());
	}
}
