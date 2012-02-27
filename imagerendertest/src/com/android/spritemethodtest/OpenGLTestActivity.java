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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * Activity for testing OpenGL ES drawing speed.  This activity sets up sprites 
 * and passes them off to an OpenGLSurfaceView for rendering and movement.
 */
public class OpenGLTestActivity extends Activity {
    private final static int SPRITE_WIDTH = 64;
    private final static int SPRITE_HEIGHT = 64;
    
    
    private GLSurfaceView mGLSurfaceView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        SimpleGLRenderer spriteRenderer = new SimpleGLRenderer(this);
       
        // Clear out any old profile results.
        ProfileRecorder.sSingleton.resetAll();
        
        final Intent callingIntent = getIntent();
        // Allocate our sprites and add them to an array.
        final int robotCount = callingIntent.getIntExtra("spriteCount", 0);
        final boolean animate = callingIntent.getBooleanExtra("animate", false);
        final boolean useVerts = 
            callingIntent.getBooleanExtra("useVerts", false);
        final boolean useHardwareBuffers = 
            callingIntent.getBooleanExtra("useHardwareBuffers", false);
        
        // Allocate space for the robot sprites + one background sprite.
        GLSprite[] spriteArray = new GLSprite[robotCount + 1];    
        
        // We need to know the width and height of the display pretty soon,
        // so grab the information now.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        GLSprite background = new GLSprite(R.drawable.background);
        BitmapDrawable backgroundImage = (BitmapDrawable)getResources().getDrawable(R.drawable.background);
        Bitmap backgoundBitmap = backgroundImage.getBitmap();
        background.width = backgoundBitmap.getWidth();
        background.height = backgoundBitmap.getHeight();
        if (useVerts) {
            // Setup the background grid.  This is just a quad.
            Grid backgroundGrid = new Grid(2, 2, false);
            backgroundGrid.set(0, 0,  0.0f, 0.0f, 0.0f, 0.0f, 1.0f, null);
            backgroundGrid.set(1, 0, background.width, 0.0f, 0.0f, 1.0f, 1.0f, null);
            backgroundGrid.set(0, 1, 0.0f, background.height, 0.0f, 0.0f, 0.0f, null);
            backgroundGrid.set(1, 1, background.width, background.height, 0.0f, 
                    1.0f, 0.0f, null );
            background.setGrid(backgroundGrid);
        }
        spriteArray[0] = background;
        
        
        Grid spriteGrid = null;
        if (useVerts) {
            // Setup a quad for the sprites to use.  All sprites will use the
            // same sprite grid intance.
            spriteGrid = new Grid(2, 2, false);
            spriteGrid.set(0, 0,  0.0f, 0.0f, 0.0f, 0.0f , 1.0f, null);
            spriteGrid.set(1, 0, SPRITE_WIDTH, 0.0f, 0.0f, 1.0f, 1.0f, null);
            spriteGrid.set(0, 1, 0.0f, SPRITE_HEIGHT, 0.0f, 0.0f, 0.0f, null);
            spriteGrid.set(1, 1, SPRITE_WIDTH, SPRITE_HEIGHT, 0.0f, 1.0f, 0.0f, null);
        }
        
        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
        Runtime r = Runtime.getRuntime();
        r.gc();
        
        spriteRenderer.setSprites(spriteArray);
        spriteRenderer.setVertMode(useVerts, useHardwareBuffers);
        
        mGLSurfaceView.setRenderer(spriteRenderer);
        
        setContentView(mGLSurfaceView);
    }
}
