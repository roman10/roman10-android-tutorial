package roman10.zoomablegallery;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;
import android.widget.Toast;

public class MyGallery extends Gallery {
	private Context mContext;
	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public MyGallery(Context context) {
		super(context);
		mContext = context;
	}
	
	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2){
	    return e2.getX() > e1.getX();
	}
	
//	public void showPrevPhoto(int _style) {
//		int lSelectionId = this.getSelectedItemPosition() - 1;
//		if (lSelectionId < 0) {
//			lSelectionId = 0;
//		}
//		this.setSelection(lSelectionId%this.getCount(), true);
//	}
	
	private BooleanArrayList mShowStatus = new BooleanArrayList();
	List<Integer> mUnshownList = new ArrayList<Integer>();
	public void showNextPhoto(int _style) {
		PhotoAdapter lAdapter = (PhotoAdapter)this.getAdapter();
		int lSelectionId;
		if (_style == 0) {
			lSelectionId = (this.getSelectedItemPosition()+1)%lAdapter.getRealCount();
		} else if (_style == 1) {
			int lCnt = lAdapter.getRealCount();
			//get number of decrypted photots
			//int lCnt = 
			//append the booleans 
			int lShownCnt = mShowStatus.size();
			for (int i = 0; i < lCnt - lShownCnt; ++i) {
				mShowStatus.add(false);
			}
			//if all items are shown, we need to clear the list
			boolean allShown = true;
			for (int i = 0; i < mShowStatus.size(); ++i) {
				if (!mShowStatus.get(i)) {
					allShown = false;
					break;
				}
			}
			if (allShown == true) {
				for (int i = 0; i < mShowStatus.size(); ++i) {
					mShowStatus.set(i, false);
				}
			}
			//get the unshown indices
			mUnshownList.clear();
			for (int i = 0; i < lCnt; ++i) {
				if (!mShowStatus.get(i)) {
					mUnshownList.add(i);
				}
			}
			Random lr = new Random();
			lSelectionId = mUnshownList.get(lr.nextInt(mUnshownList.size()));
			mShowStatus.set(lSelectionId, true);
		} else {
			lSelectionId = (this.getSelectedItemPosition()+1)%this.getCount();
		}
		//Log.e("MyGallery - show", lSelectionId + ":" + this.getCount());
		if ((lSelectionId > this.getCount() - 1) && (this.getCount() < lAdapter.getRealCount())) {
			//we want to show a position not in current adapter; and there's more data
			lAdapter.notifyDataSetChanged();
		}
		this.setSelection(lSelectionId, true);
		//Log.e("show", this.getSelectedItemPosition() + ":" + (this.getCount()) + ":" + (this.getAdapter().getCount())) ;
	}
	
	public void rotateLeft() {
		ImageZoomView izv = (ImageZoomView) this.getSelectedView();
		izv.setRotate(-90);
		izv.invalidate();
	}
	
	public void rotateRight() {
		ImageZoomView izv = (ImageZoomView) this.getSelectedView();
		izv.setRotate(90);
		izv.invalidate();
	}
	
	public void zoomIn() {
		ImageZoomView izv = (ImageZoomView) this.getSelectedView();
		izv.setZoom(1.2f);
		izv.invalidate();
	}
	
	public void zoomOut() {
		ImageZoomView izv = (ImageZoomView) this.getSelectedView();
		izv.setZoom(0.8f);
		izv.invalidate();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		//Toast.makeText(mContext, "gallery onFling", Toast.LENGTH_SHORT).show();
		//Log.e("MyGallery-onFling", velocityX + ":" + velocityY);
		if (mDisableGalleryFling) {
			//since user want to fling, we display the reset button
//			Log.e("fling", e1.getX() + ":" + e1.getY() + ":" + e2.getX() + ":" + e2.getY());
//			Log.e("fling-vel", velocityX + ":" + velocityY);
			//TODO: there're improvements might be made so that we can handle the fling automatically
			//currently the child view is not rendered, so we cannot fling
			if (Math.abs(e1.getX() - e2.getX()) > 150) {
				//show the reset btn
				Viewer2.self.enableReset();
				mTempEnableGalleryFling = true;
//				mLastSelectedViewPos = this.getSelectedItemPosition();
				//Log.e("onFling", e1.getX() + ":" + e1.getY() + ":" + e2.getX() + ":" + e2.getY() + ";" + velocityX + ":" + velocityY);
				onScroll(e1, e2, e1.getX() - e2.getX(), e1.getY() - e2.getY());
				mTempEnableGalleryFling = false;
				//showNextPhoto();
//				return true;
				//mDisableGalleryFling = false;
			} else {
				return true;
			}
		}
		int kEvent;
		if(isScrollingLeft(e1, e2)){ //Check if scrolling left
//			if (this.getSelectedItemPosition() == 0) {
//				//TODO: still needs to fine-tune: disable first
//				this.setSelection(this.getCount() - 1, true);
//			} else {
				kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
				onKeyDown(kEvent, null);
//			}
		} else{ //Otherwise scrolling right
			//here we notify the adapter that it should refresh the list
			//Log.e("count", this.getSelectedItemPosition() + ":" + (this.getCount()) + ":" + (this.getAdapter().getCount())) ;
			PhotoAdapter lAdapter = (PhotoAdapter)this.getAdapter();
			int lPos = this.getSelectedItemPosition() + 1 < lAdapter.getRealCount() ? this.getSelectedItemPosition() + 1:lAdapter.getRealCount()-1;
			if ((lPos > this.getCount() - 1) && (this.getCount() < lAdapter.getRealCount())) {
				//we want to show a position not in current adapter; and there's more data
				lAdapter.notifyDataSetChanged();
				//do this in order to make the next Key Down event function
				kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
				onKeyDown(kEvent, null);
			}
//			if (this.getSelectedItemPosition() == this.getCount()-1) {
//				//TODO: still needs to fine-tune
//				this.setSelection(0, true);
//			} else {
				kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
				onKeyDown(kEvent, null);
//			}
		}
		return true; 
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//		Log.e("MyGallery-onScroll", e1.getX() + ":" + e1.getY() + ":" + e2.getX() + ":" + e2.getY() + ";" + distanceX + ":" + distanceY);
		if (mDisableGalleryFling && mTempEnableGalleryFling == false) {
			return true;
		}
		//this.getSelectedView().invalidate();
		return super.onScroll(e1, e2, distanceX, distanceY);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
	public void resetCurrentPhoto() {
		ImageZoomView izv = (ImageZoomView) this.getSelectedView();
		izv.resetZoomState();
	}
	
	private boolean mDisableGalleryFling = false;
	private boolean mTempEnableGalleryFling = false;
//	private int mLastSelectedViewPos = 0;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//when return true, the underlying ZoomView won't get the event=>Gallery takes control
		//when return false, ZoomView get the event=>zoomView takes control
		//return true;
		//return false;
		//here we send a copy of the MotionEvent to Gallery and also let the zoomview to handle 
		//it
		//TouchImageView izv = (TouchImageView) this.getSelectedView();
		ImageZoomView izv = (ImageZoomView) this.getSelectedView();
		if (izv.getZoomState().getZoom() > 1.0f) {
			//disable the gallery event
			mDisableGalleryFling = true;
		}  else {
			mDisableGalleryFling = false;
		}
//		if (this.getSelectedItemPosition() != mLastSelectedViewPos) {
//			mTempEnableGalleryFling = false;
//		}
		onTouchEvent(ev);
		int lVisisbleViewCount = this.getChildCount();
		//Log.e("MyGallery-childcount", lVisisbleViewCount + "");
		if (lVisisbleViewCount > 2) {
			//disable child view touch event if the gallery is scrolling or flinging
			return true;
		} else {
			return super.onInterceptTouchEvent(ev);
		}
	}
	
	@Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
