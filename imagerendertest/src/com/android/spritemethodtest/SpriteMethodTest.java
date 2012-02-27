/*
 * Copyright (C) 2008 The Android Open Source Project
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

/**
 * Main entry point for the SpriteMethodTest application.  This application
 * provides a simple interface for testing the relative speed of 2D rendering
 * systems available on Android, namely the Canvas system and OpenGL ES.  It
 * also serves as an example of how SurfaceHolders can be used to create an
 * efficient rendering thread for drawing.
 */
public class SpriteMethodTest extends Activity {
    private static final int ACTIVITY_TEST = 0;
    private static final int RESULTS_DIALOG = 0;
    
    private CheckBox canvasBox, openGLBox;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        canvasBox = (CheckBox) findViewById(R.id.methodCanvas);
        canvasBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox)v;
				if (cb.isChecked()) {
					openGLBox.setChecked(false);
					methodChange(METHOD_CANVAS);
				} else {
					openGLBox.setChecked(true);
					methodChange(METHOD_OPENGL);
				}
			}
		});
        openGLBox = (CheckBox) findViewById(R.id.methodOpenGL);
        openGLBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox)v;
				if (cb.isChecked()) {
					canvasBox.setChecked(false);
					methodChange(METHOD_OPENGL);
				} else {
					canvasBox.setChecked(true);
					methodChange(METHOD_CANVAS);
				}
			}
		});
        canvasBox.setChecked(true);
        // Sets up a click listener for the Run Test button.
        Button button;
        button = (Button) findViewById(R.id.runTest);
        button.setOnClickListener(mRunTestListener);

        // Turns on one item by default in our radio groups--as it should be!
        RadioGroup group = (RadioGroup)findViewById(R.id.canvasSettings);
        group.check(R.id.settingView);
        
        RadioGroup glSettings = (RadioGroup)findViewById(R.id.GLSettings);
        glSettings.check(R.id.settingVerts);
        
    }
    
    /** Passes preferences about the test via its intent. */
    protected void initializeIntent(Intent i) {
        //final CheckBox checkBox = (CheckBox) findViewById(R.id.animateSprites);
        //final boolean animate = checkBox.isChecked();
        //final EditText editText = (EditText) findViewById(R.id.spriteCount);
        //final String spriteCountText = editText.getText().toString(); 
        //final int stringCount = Integer.parseInt(spriteCountText);
        
        //i.putExtra("animate", animate);
        //i.putExtra("spriteCount", stringCount);
    }
    
    /** 
     * Responds to a click on the Run Test button by launching a new test 
     * activity.
     */
    View.OnClickListener mRunTestListener = new OnClickListener() {
        public void onClick(View v) {
            Intent i;
            if (canvasBox.isChecked()) {
            	RadioGroup canvasSettings = 
                        (RadioGroup)findViewById(R.id.canvasSettings);
                i = new Intent(v.getContext(), CanvasTestActivity.class);
                if (canvasSettings.getCheckedRadioButtonId() == R.id.settingView) {
                	i.putExtra("useView", true);
                } 
            } else {
                i = new Intent(v.getContext(), OpenGLTestActivity.class);
                RadioGroup glSettings = 
                    (RadioGroup)findViewById(R.id.GLSettings);
                if (glSettings.getCheckedRadioButtonId() == R.id.settingVerts) {
                    i.putExtra("useVerts", true);
                } else if (glSettings.getCheckedRadioButtonId() 
                        == R.id.settingVBO) {
                    i.putExtra("useVerts", true);
                    i.putExtra("useHardwareBuffers", true);
                }
            }
            initializeIntent(i);
            startActivityForResult(i, ACTIVITY_TEST);
        }
    };
    
    /**
     * Enables or disables OpenGL ES-specific settings controls when the render
     * method option changes. 
     */
    
    private static final int METHOD_CANVAS = 1;
    private static final int METHOD_OPENGL = 2;
    public void methodChange(int method) {
       if (method == METHOD_CANVAS) {
           findViewById(R.id.settingDrawTexture).setEnabled(false);
           findViewById(R.id.settingVerts).setEnabled(false);
           findViewById(R.id.settingVBO).setEnabled(false);
           findViewById(R.id.settingView).setEnabled(true);
           findViewById(R.id.settingSurface).setEnabled(true);
       } else {
    	   findViewById(R.id.settingView).setEnabled(false);
           findViewById(R.id.settingSurface).setEnabled(false);
           findViewById(R.id.settingDrawTexture).setEnabled(true);
           findViewById(R.id.settingVerts).setEnabled(true);
           findViewById(R.id.settingVBO).setEnabled(true);
       }
    }
  
    /** Creates the test results dialog and fills in a dummy message. */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        if (id == RESULTS_DIALOG) {
            
            String dummy = "No results yet.";
            CharSequence sequence = dummy.subSequence(0, dummy.length() -1);
            dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_ok, null)
                .setMessage(sequence)
                .create();
        }
        return dialog;
    }
    
    /** 
     * Replaces the dummy message in the test results dialog with a string that
     * describes the actual test results.
     */
    protected void onPrepareDialog (int id, Dialog dialog) {
        if (id == RESULTS_DIALOG) {
            // Extract final timing information from the profiler.
            final ProfileRecorder profiler = ProfileRecorder.sSingleton;
            final long frameTime = 
                profiler.getAverageTime(ProfileRecorder.PROFILE_FRAME);
            final long frameMin = 
                profiler.getMinTime(ProfileRecorder.PROFILE_FRAME);
            final long frameMax = 
                profiler.getMaxTime(ProfileRecorder.PROFILE_FRAME);
            
            final long drawTime = 
                    profiler.getAverageTime(ProfileRecorder.PROFILE_DRAW);
            final long drawMin = 
                    profiler.getMinTime(ProfileRecorder.PROFILE_DRAW);
            final long drawMax = 
                    profiler.getMaxTime(ProfileRecorder.PROFILE_DRAW);
            
            final long flipTime = 
                    profiler.getAverageTime(ProfileRecorder.PROFILE_PAGE_FLIP);
            final long flipMin = 
                    profiler.getMinTime(ProfileRecorder.PROFILE_PAGE_FLIP);
            final long flipMax = 
                    profiler.getMaxTime(ProfileRecorder.PROFILE_PAGE_FLIP);
            
            final long simTime = 
                    profiler.getAverageTime(ProfileRecorder.PROFILE_SIM);
            final long simMin = 
                    profiler.getMinTime(ProfileRecorder.PROFILE_SIM);
            final long simMax = 
                    profiler.getMaxTime(ProfileRecorder.PROFILE_SIM);
            
            
            final float fps = frameTime > 0 ? 1000.0f / frameTime : 0.0f;
            
            String result = "Frame: " +  frameTime + "ms (" + fps + " fps)\n"
                + "\t\tMin: " + frameMin + "ms\t\tMax: " + frameMax + "\n" 
                + "Draw: " + drawTime + "ms\n"
                + "\t\tMin: " + drawMin + "ms\t\tMax: " + drawMax + "\n"
                + "Page Flip: " + flipTime + "ms\n"
                + "\t\tMin: " + flipMin + "ms\t\tMax: " + flipMax + "\n"
                + "Sim: " + simTime + "ms\n"
                + "\t\tMin: " + simMin + "ms\t\tMax: " + simMax + "\n";
            CharSequence sequence = result.subSequence(0, result.length() -1);
            AlertDialog alertDialog = (AlertDialog)dialog;
            alertDialog.setMessage(sequence);
        }
    }
    
    /** Shows the results dialog when the test activity closes. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
            Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        showDialog(RESULTS_DIALOG);
             
    }
   
}