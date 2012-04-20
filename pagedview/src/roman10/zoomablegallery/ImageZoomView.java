/*
 * Copyright 2010 Sony Ericsson Mobile Communications AB
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package roman10.zoomablegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

/**
 * View capable of drawing an image at different zoom state levels
 */
public class ImageZoomView extends View implements Observer {

    /** Paint object used when drawing bitmap. */
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    /** Rectangle used (and re-used) for cropping source image. */
    private final Rect mRectSrc = new Rect();

    /** Rectangle used (and re-used) for specifying drawing area on canvas. */
    private final Rect mRectDst = new Rect();

    /** Object holding aspect quotient */
    private final AspectQuotient mAspectQuotient = new AspectQuotient();

    /** The bitmap that we're zooming in, and drawing on the screen. */
    private Bitmap mBitmap;

    /** State of the zoom. */
    private ZoomState mState;
    
    private int mRotate = 0;
    
    public void cleanUp() {
    	//called when we want to free resources for ImageZoomView
    	mBitmap.recycle();
    	mBitmap = null;
    }
    
    public void setRotate(int rotate) {
    	if (rotate!=mRotate) {
    		mRotate = rotate;
    		mAccuRotate += mRotate;
        	mAccuRotate %= 360;
            //calculateAspectQuotient();
    	}
    }
    
    public void setZoom(float zoomF) {
    	mZoomControl.setZoom(mState.getZoom()*zoomF);
    	if (mState.getZoom() - 1.0f < 0.00001f) {
    		resetZoomState();
    	}
    }
    
    private static int mAccuRotate = 0;
    // Public methods

    /**
     * Constructor
     */
    public ImageZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    private int mIndex;
    public int getIndex() {
    	return mIndex;
    }
    
    /** Zoom control */
    private DynamicZoomControl mZoomControl;
    public ImageZoomView(Context context, Integer ori, int _indexId) {
        super(context);
        mIndex = _indexId;
       	Log.i("rotate", String.valueOf(ori) + mIndex);
        setRotate(ori*90);
	    /** On touch listener for zoom view */
//	    LongPressZoomListener mZoomListener;
	    
	    PinchZoomListener mPinchZoomListener;
//	    boolean longpressZoom = false;
	    
		mZoomControl = new DynamicZoomControl();
		
		setZoomState(mZoomControl.getZoomState());

//        mZoomListener = new LongPressZoomListener(mContext.getApplicationContext());
//        mZoomListener.setZoomControl(mZoomControl);
        
        mPinchZoomListener = new PinchZoomListener(context.getApplicationContext());
        mPinchZoomListener.setZoomControl(mZoomControl);
        
        mZoomControl.setAspectQuotient(getAspectQuotient());

        resetZoomState();
        
        setOnTouchListener(mPinchZoomListener);
    }
    
    /**
     * Reset zoom state and notify observers
     */
    public void resetZoomState() {
    	mZoomControl.getZoomState().setPanX(0.5f);
    	mZoomControl.getZoomState().setPanY(0.5f);
    	mZoomControl.getZoomState().setZoom(1f);
    	mZoomControl.getZoomState().notifyObservers();
    }

    /**
     * Set image bitmap
     * 
     * @param bitmap The bitmap to view and zoom into
     */
    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;

        calculateAspectQuotient();

        invalidate();
    }

    /**
     * Set object holding the zoom state that should be used
     * 
     * @param state The zoom state
     */
    public void setZoomState(ZoomState state) {
        if (mState != null) {
            mState.deleteObserver(this);
        }

        mState = state;
        mState.addObserver(this);

        invalidate();
    }

    /**
     * Gets reference to object holding aspect quotient
     * 
     * @return Object holding aspect quotient
     */
    public AspectQuotient getAspectQuotient() {
        return mAspectQuotient;
    }
    
    private void calculateAspectQuotient() {
        if (mBitmap != null) {
        	if ((mAccuRotate == 90) || (mAccuRotate == 270)) {
        		mAspectQuotient.updateAspectQuotient(getHeight(), getWidth(), mBitmap.getWidth(), mBitmap.getHeight());
//        		mAspectQuotient = (((float)mBitmap.getWidth()) / mBitmap.getHeight())
//                / (((float)getHeight()) / getWidth());
        	} else {
        		mAspectQuotient.updateAspectQuotient(getWidth(), getHeight(), mBitmap.getWidth(), mBitmap.getHeight());
//        		mAspectQuotient = (((float)mBitmap.getWidth()) / mBitmap.getHeight())
//                / (((float)getWidth()) / getHeight());
        	}
        }
//        Log.e("ImageZoomView-calculateAspectQuotient", getWidth() + ":" + getHeight() + ";" + mBitmap.getWidth() + ":" + mBitmap.getHeight());
        mAspectQuotient.notifyObservers();
    }

    private void calculateDimension() {
        final int viewWidth = getWidth();
        final int viewHeight = getHeight();
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();

        if (mRotate!=0) {
        	Matrix matrix = new Matrix();
        	matrix.postRotate(mRotate);
        	
			//Bitmap l_bitmap = mBitmap;
        	Bitmap l_bitmap = mBitmap;
        	int tryCnt = 0;
        	int roateBitmapWidth = bitmapWidth;
        	int rotateBitmapHeight = bitmapHeight;
        	while (true) {
	        	try {
	        		if (tryCnt >= 5) {
	        			break;	//if tried too many times break
	        		}
	        		if (tryCnt > 0) {
	        			//if we failed before, we scale down first
	        			l_bitmap = Bitmap.createScaledBitmap(mBitmap, roateBitmapWidth, rotateBitmapHeight, true);
	        		}
		        	mBitmap = Bitmap.createBitmap(l_bitmap, 0, 0, roateBitmapWidth, rotateBitmapHeight, matrix, true);
		        	if (l_bitmap!=null) {
		        		l_bitmap.recycle();
		        		l_bitmap = null;
		    		}
		        	break;
	        	} catch (OutOfMemoryError e) {
	        		++tryCnt;
	        		roateBitmapWidth /= 2;
	        		rotateBitmapHeight /= 2;
	        		//System.gc();
	        	}
        	}
        	bitmapWidth = mBitmap.getWidth();
        	bitmapHeight = mBitmap.getHeight();
        	mRotate = 0;
        }
        
        calculateAspectQuotient();
        final float aspectQuotient = mAspectQuotient.get();

        final float panX = mState.getPanX();
        final float panY = mState.getPanY();
        final float zoomX;
        final float zoomY;        
        if ((mAccuRotate== 90) || (mAccuRotate == 270)) {
            zoomX = mState.getZoomX(aspectQuotient) * viewHeight / bitmapWidth;
            zoomY = mState.getZoomY(aspectQuotient) * viewWidth / bitmapHeight;
        } else {
        	zoomX = mState.getZoomX(aspectQuotient) * viewWidth / bitmapWidth;
        	zoomY = mState.getZoomY(aspectQuotient) * viewHeight / bitmapHeight;
        }
        // Setup source and destination rectangles
        mRectSrc.left = (int)(panX * bitmapWidth - viewWidth / (zoomX * 2));
        mRectSrc.top = (int)(panY * bitmapHeight - viewHeight / (zoomY * 2));
        mRectSrc.right = (int)(mRectSrc.left + viewWidth / zoomX);
        mRectSrc.bottom = (int)(mRectSrc.top + viewHeight / zoomY);
        mRectDst.left = getLeft();
        mRectDst.top = getTop();
        mRectDst.right = getRight();
        mRectDst.bottom = getBottom();

        // Adjust source rectangle so that it fits within the source image.
        if (mRectSrc.left < 0) {
            mRectDst.left += -mRectSrc.left * zoomX;
            mRectSrc.left = 0;
        }
        if (mRectSrc.right > bitmapWidth) {
            mRectDst.right -= (mRectSrc.right - bitmapWidth) * zoomX;
            mRectSrc.right = bitmapWidth;
        }
        if (mRectSrc.top < 0) {
            mRectDst.top += -mRectSrc.top * zoomY;
            mRectSrc.top = 0;
        }
        if (mRectSrc.bottom > bitmapHeight) {
            mRectDst.bottom -= (mRectSrc.bottom - bitmapHeight) * zoomY;
            mRectSrc.bottom = bitmapHeight;
        }
//        Log.e("ImageZoomView-onDraw", panX + ":" + panY + ";" + mRectSrc.left + ":" + mRectSrc.right + ":" + mRectSrc.top + ":" + mRectSrc.bottom + ";" + mRectDst.left + ":" + mRectDst.right + ":" + mRectDst.top + ":" + mRectDst.bottom);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null && mState != null) {
        	//mId = mId;		//for debug
        	//onMeasure(480,800);	//for test
        	int tryCnt = 0;
        	while (true) {
	        	try {
	        		if (tryCnt >= 8) {
	        			//if tried too many times break
	        			break;
	        		}
	        		calculateDimension();
	        		canvas.drawBitmap(mBitmap, mRectSrc, mRectDst, mPaint);
	        		break;	//if no error, break
	        	} catch (OutOfMemoryError e) {
	        		++tryCnt;
	        		Log.e("ImageZoomView", "onDraw.drawBitmap cause memory error: " + tryCnt);
	        		//System.gc();
	        	}
        	}
        }
    }
    
    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
    	//mId = mId;		//for debug
    	//calculateDimension();
    	//setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        if ((mAccuRotate == 90) || (mAccuRotate == 270)) {
//        	mAspectQuotient.updateAspectQuotient(bottom - top, right - left, mBitmap.getWidth(), mBitmap.getHeight());
//    	} else {
//    		mAspectQuotient.updateAspectQuotient(right - left, bottom - top, mBitmap.getWidth(),
//                    mBitmap.getHeight());
//    	}
//        
//        //Log.w("ImageZoomView-onLayout", String.valueOf(right-left) + "," + String.valueOf(bottom-top) + "," + mBitmap.getWidth() + "," + mBitmap.getHeight());
//        mAspectQuotient.notifyObservers();
        calculateAspectQuotient();
    }

    // implements Observer
    public void update(Observable observable, Object data) {
    	//onMeasure(mBitmap.getWidth(), mBitmap.getHeight());	//roman10: added
    	//if (mState.getZoom() > 1.0) {
    		//requestLayout();	//added by roman10: this works partially
    	//}
    	if (mState.getZoom() - 1.0f < 0.00001f) {
    		mState.setPanX(0.5f);
    		mState.setPanY(0.5f);
    	}
        invalidate();
    }

	public ZoomState getZoomState() {
		return mState;
	}

}
