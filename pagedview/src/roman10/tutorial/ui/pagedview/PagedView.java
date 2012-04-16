/*
 * Copyright (C) 2011 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package roman10.tutorial.ui.pagedview;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class PagedView extends ViewGroup {
    private static final String TAG = PagedView.class.getSimpleName();
    public interface OnPageViewChangeListener {
        void onPageChanged(PagedView pagedView, int previousPage, int newPage);
        void onStartTracking(PagedView pagedView);
        void onStopTracking(PagedView pagedView); 
    } 
    private static final int INVALID_PAGE = -1;
    private static final int MINIMUM_PAGE_CHANGE_VELOCITY = 500;
    private static final int VELOCITY_UNITS = 1000;
    //1000/60 = 16
    private static final int FRAME_RATE = 16;
    
    private final Handler mHandler = new Handler();
    private int mPageCount;
    private int mCurrentPage;
    private int mTargetPage = INVALID_PAGE;
    
    private int mPagingTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mPageSlop;

    private boolean mIsBeingDragged;

    private int mOffsetX;
    private int mStartMotionX;
    private int mStartOffsetX;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnPagedViewChangeListener mOnPageChangeListener;

    private PagedAdapter mAdapter;

    private SparseArray<View> mActiveViews = new SparseArray<View>();
    private Queue<View> mRecycler = new LinkedList<View>();

    public PagedView(Context context) {
        this(context, null);
    } 
    public PagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPagedView();
    } 
    private void initPagedView() {
        final Context context = getContext();
        mScroller = new Scroller(context, new DecelerateInterpolator());
        final ViewConfiguration conf = ViewConfiguration.get(context);
        //Distance a touch can wander before we think the user is scrolling in pixels
        mPagingTouchSlop = conf.getScaledTouchSlop()*2;
        //Maximum velocity to initiate a fling, as measured in pixels per second.
        mMaximumVelocity = conf.getScaledMaximumFlingVelocity();
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mMinimumVelocity = (int) (metrics.density * MINIMUM_PAGE_CHANGE_VELOCITY + 0.5f);
    }
    //Measure the view and its content to determine the measured width and the measured height.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       int widthMode = MeasureSpec.getMode(widthMeasureSpec);
       int heightMode = MeasureSpec.getMode(heightMeasureSpec);
       int widthSize = MeasureSpec.getSize(widthMeasureSpec);
       int heightSize = MeasureSpec.getSize(heightMeasureSpec);
       int childWidth = 0;
       int childHeight = 0;
        
       int itemCount = (mAdapter == null ? 0:mAdapter.getCount());
       if (itemCount > 0) {
           if (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED) {
               final View child = obtainView(mCurrentPage);
               //Ask one of the children of this view to measure itself, taking into account both the MeasureSpec requirements for this view and its padding.
               measureChild(child, widthMeasureSpec, heightMeasureSpec);
               childWidth = child.getMeasuredWidth();
               childHeight = child.getMeasuredHeight();
           }
           if (widthMode == MeasureSpec.UNSPECIFIED) {
               widthSize = childWidth;
           }
           if (heightMode == MeasureSpec.UNSPECIFIED) {
               heightSize = childHeight;
           }
       }
       setMeasuredDimension(widthSize, heightSize);
    }
    //This is called during layout when the size of this view has changed.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPageSlop = (int) (w*0.5);
        //Make sure the offset adapts itself to mCurrentPage
        mOffsetX = getOffsetForPage(mCurrentPage);
    }
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mPageCount <= 0) {
            return;
        }	
        final int startPage = getPageForOffset(mOffsetX);
        final int endPage = getPageForOffset(mOffsetX - getWidth() + 1);
        //save the useful views from recycle
        recycleViews(startPage, endPage);
        //check if the view is already exist, if not, then create the view
        for (int i = startPage; i <= endPage; ++i) {
            View child = mActiveViews.get(i);
            if (child == null) {
                child = obtainView(i);
            }
            setupView(child, i);
        }
	}
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    }
}
