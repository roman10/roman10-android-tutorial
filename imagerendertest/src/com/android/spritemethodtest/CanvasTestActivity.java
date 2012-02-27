/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.android.spritemethodtest;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * Activity for testing Canvas drawing speed.  This activity sets up sprites and
 * passes them off to a CanvasSurfaceView for rendering and movement.  It is
 * very similar to OpenGLTestActivity.  Note that Bitmap objects come out of a
 * pool and must be explicitly recycled on shutdown.  See onDestroy().
 */
public class CanvasTestActivity extends Activity {
    private CanvasSurfaceView mCanvasSurfaceView;
    // Describes the image format our bitmaps should be converted to.
    private static BitmapFactory.Options sBitmapOptions 
        = new BitmapFactory.Options();
    private Bitmap[] mBitmaps;
    
    private boolean useView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Clear out any old profile results.
        ProfileRecorder.sSingleton.resetAll();

        final Intent callingIntent = getIntent();
        useView = callingIntent.getBooleanExtra("useView", false);
        
     // Sets our preferred image format to 16-bit, 565 format.
        //sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        
        mBitmaps = new Bitmap[2];
        mBitmaps[0] = loadBitmap(this, R.drawable.background);
        mBitmaps[1] = loadBitmap(this, R.drawable.test);
        if (useView) {
        	 // Now's a good time to run the GC.  Since we won't do any explicit
             // allocation during the test, the GC should stay dormant and not
             // influence our results.
             Runtime r = Runtime.getRuntime();
             r.gc();
             
             setContentView(new CanvasView(this.getApplicationContext(), mBitmaps));
        } else {
        	 mCanvasSurfaceView = new CanvasSurfaceView(this);
             SimpleCanvasRenderer spriteRenderer = new SimpleCanvasRenderer();
             
             // Now's a good time to run the GC.  Since we won't do any explicit
             // allocation during the test, the GC should stay dormant and not
             // influence our results.
             Runtime r = Runtime.getRuntime();
             r.gc();
             
             spriteRenderer.setBitmaps(mBitmaps);
             mCanvasSurfaceView.setRenderer(spriteRenderer);

             setContentView(mCanvasSurfaceView);
        }
    }
    
    
    /** Recycles all of the bitmaps loaded in onCreate(). */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!useView) {
	        mCanvasSurfaceView.clearEvent();
	        mCanvasSurfaceView.stopDrawing();
        }

        for (int i = 0; i < mBitmaps.length; ++i) {
        	mBitmaps[i].recycle();
        	mBitmaps[i] = null;
        }
    }


    /**
     * Loads a bitmap from a resource and converts it to a bitmap.  This is
     * a much-simplified version of the loadBitmap() that appears in
     * SimpleGLRenderer.
     * @param context  The application context.
     * @param resourceId  The id of the resource to load.
     * @return  A bitmap containing the image contents of the resource, or null
     *     if there was an error.
     */
    protected Bitmap loadBitmap(Context context, int resourceId) {
        Bitmap bitmap = null;
        if (context != null) {
          
            InputStream is = context.getResources().openRawResource(resourceId);
            try {
                bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore.
                }
            }
        }

        return bitmap;
    }
}
