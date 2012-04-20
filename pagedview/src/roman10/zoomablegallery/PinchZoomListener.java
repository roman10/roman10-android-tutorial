
package roman10.zoomablegallery;

import android.content.Context;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

public class PinchZoomListener implements View.OnTouchListener {
    /**
     * Enum defining listener modes. Before the view is touched the listener is
     * in the UNDEFINED mode. Once touch starts it can enter either one of the
     * other two modes: If the user scrolls over the view the listener will
     * enter PAN mode, if the user lets his finger rest and makes a long press
     * the listener will enter ZOOM mode.
     */
    private enum Mode {
        UNDEFINED, PAN, PINCHZOOM
    }

    private static final String TAG = "PinchZoomListener";

    /** Current listener mode */
    private Mode mMode = Mode.UNDEFINED;

    /** Zoom control to manipulate */
    private DynamicZoomControl mZoomControl;

    /** X-coordinate of previously handled touch event */
    private float mX;

    /** Y-coordinate of previously handled touch event */
    private float mY;

    /** X-coordinate of latest down event */
    private float mDownX;

    /** Y-coordinate of latest down event */
    private float mDownY;

    private PointF mMidPoint = new PointF();

    /** Velocity tracker for touch events */
    private VelocityTracker mVelocityTracker;

    /** Distance touch can wander before we think it's scrolling */
    private final int mScaledTouchSlop;

    /** Maximum velocity for fling */
    private final int mScaledMaximumFlingVelocity;

    /** Distance between fingers */
    private float oldDist = 1f;
    
    private long panAfterPinchTimeout = 0;

    public PinchZoomListener(Context context) {
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScaledMaximumFlingVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
    }

    /**
     * Sets the zoom control to manipulate
     * 
     * @param control Zoom control
     */
    public void setZoomControl(DynamicZoomControl control) {
        mZoomControl = control;
    }

    //[update]roman10: pan mode should only be enabled when zoom > 1.0 
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mZoomControl.stopFling();
                mDownX = x;
                mDownY = y;
                mX = x;
                mY = y;
                break;
                
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() > 1) {
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        midPoint(mMidPoint, event);
                        mMode = Mode.PINCHZOOM;
                    }
                }
                break;
                
            case MotionEvent.ACTION_UP:
                if (mMode == Mode.PAN) {
                    final long now = System.currentTimeMillis();
                    if((panAfterPinchTimeout < now) && (mZoomControl.getZoomState().getZoom() > 1.0)){
                        mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
                        mZoomControl.startFling(-mVelocityTracker.getXVelocity() / v.getWidth(),
                                -mVelocityTracker.getYVelocity() / v.getHeight());
                    }
                } else if((mMode != Mode.PINCHZOOM) && (mZoomControl.getZoomState().getZoom() > 1.0)) {
                    mZoomControl.startFling(0, 0);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;			//roman10: added
            case MotionEvent.ACTION_POINTER_UP:
                if(event.getPointerCount() > 1 &&  mMode == Mode.PINCHZOOM){
                    panAfterPinchTimeout = System.currentTimeMillis() + 100;
                }
                mMode = Mode.UNDEFINED;                
                break;
                
            case MotionEvent.ACTION_MOVE:
                final float dx = (x - mX) / v.getWidth();
                final float dy = (y - mY) / v.getHeight();
               
                if ((mMode == Mode.PAN) && (mZoomControl.getZoomState().getZoom() > 1.0)) {
                    mZoomControl.pan(-dx, -dy);
                } else if (mMode == Mode.PINCHZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        final float scale = newDist / oldDist;
                        final float xx = mMidPoint.x / v.getWidth();
                        final float yy = mMidPoint.y / v.getHeight();
                        mZoomControl.zoom(scale, xx, yy);
                        oldDist = newDist;
                    }
                } else {
                    final float scrollX = mDownX - x;
                    final float scrollY = mDownY - y;

                    final float dist = (float)Math.sqrt(scrollX * scrollX + scrollY * scrollY);

                    if (dist >= mScaledTouchSlop ){
                        mMode = Mode.PAN;
                    }
                }
                
                mX = x;
                mY = y;
                break;

            default:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mMode = Mode.UNDEFINED;
                break;
        }
        return true;
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
