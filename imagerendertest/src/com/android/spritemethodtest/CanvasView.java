package com.android.spritemethodtest;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class CanvasView extends View {
	private Bitmap[] mBitmaps;
	private int renderCnt = 0;
	private final Paint mFramePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	final ProfileRecorder profiler;
	
	public CanvasView(Context context, Bitmap[] pBitmaps) {
		super(context);
        // Clear out any old profile results.
        ProfileRecorder.sSingleton.resetAll();
        profiler = ProfileRecorder.sSingleton;
        
        mBitmaps = pBitmaps;
        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
        Runtime r = Runtime.getRuntime();
        r.gc();
        invalidate();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		profiler.start(ProfileRecorder.PROFILE_FRAME);
    	if (renderCnt%2 == 0) {
    		canvas.drawBitmap(mBitmaps[0], 0, 0, mFramePaint);
    	} else {
    		canvas.drawBitmap(mBitmaps[1], 0, 0, mFramePaint);
    	}
    	++renderCnt;
		invalidate();
		profiler.stop(ProfileRecorder.PROFILE_FRAME);
        profiler.endFrame();
	}
}
