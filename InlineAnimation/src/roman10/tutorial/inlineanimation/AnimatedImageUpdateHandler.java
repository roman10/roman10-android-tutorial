package roman10.tutorial.inlineanimation;

import java.util.UUID;

/**
 * responsible for refresh MyTextView when a new animation frame should be displayed
 *
 */
public class AnimatedImageUpdateHandler {
	private MyTextView mView;
//	private String mId = UUID.randomUUID().toString();
	
//	public String getId() {
//		return mId;
//	}
	
	public AnimatedImageUpdateHandler(MyTextView pView) {
		mView = pView;
	}
	
	public void updateFrame() {
//		Logger.d(this, "updateFrame for view " + mView.getId() + ":" + mView.getUID());
		mView.postInvalidate();
	}
}
