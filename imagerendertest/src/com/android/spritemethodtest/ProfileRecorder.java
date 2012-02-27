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

import android.os.SystemClock;

/** 
 * Implements a simple runtime profiler.  The profiler records start and stop
 * times for several different types of profiles and can then return min, max
 * and average execution times per type.  Profile types are independent and may
 * be nested in calling code. This object is a singleton for convenience.
 */
public class ProfileRecorder {
    // A type for recording actual draw command time.
    public static final int PROFILE_DRAW = 0;
    // A type for recording the time it takes to display the scene.
    public static final int PROFILE_PAGE_FLIP = 1;
    // A type for recording the time it takes to run a single simulation step.
    public static final int PROFILE_SIM = 2;
    // A type for recording the total amount of time spent rendering a frame.
    public static final int PROFILE_FRAME = 3;
    private static final int PROFILE_COUNT = PROFILE_FRAME + 1;
    
    private ProfileRecord[] mProfiles;
    private int mFrameCount;
    
    public static ProfileRecorder sSingleton = new ProfileRecorder();
    
    public ProfileRecorder() {
        mProfiles = new ProfileRecord[PROFILE_COUNT];
        for (int x = 0; x < PROFILE_COUNT; x++) {
            mProfiles[x] = new ProfileRecord();
        }
    }
    
    /** Starts recording execution time for a specific profile type.*/
    public void start(int profileType) {
        if (profileType < PROFILE_COUNT) {
            mProfiles[profileType].start(SystemClock.uptimeMillis());
        }
    }
    
    /** Stops recording time for this profile type. */
    public void stop(int profileType) {
        if (profileType < PROFILE_COUNT) {
            mProfiles[profileType].stop(SystemClock.uptimeMillis());
        }
    }
    
    /** Indicates the end of the frame.*/
    public void endFrame() {
        mFrameCount++;
    }
    
    /* Flushes all recorded timings from the profiler. */
    public void resetAll() {
        for (int x = 0; x < PROFILE_COUNT; x++) {
            mProfiles[x].reset();
        }
        mFrameCount = 0;
    }
    
    /* Returns the average execution time, in milliseconds, for a given type. */
    public long getAverageTime(int profileType) {
        long time = 0;
        if (profileType < PROFILE_COUNT) {
            time = mProfiles[profileType].getAverageTime(mFrameCount);
        }
        return time;
    }
    
    /* Returns the minimum execution time in milliseconds for a given type. */
    public long getMinTime(int profileType) {
        long time = 0;
        if (profileType < PROFILE_COUNT) {
            time = mProfiles[profileType].getMinTime();
        }
        return time;
    }
    
    /* Returns the maximum execution time in milliseconds for a given type. */
    public long getMaxTime(int profileType) {
        long time = 0;
        if (profileType < PROFILE_COUNT) {
            time = mProfiles[profileType].getMaxTime();
        }
        return time;
    }
    
    /** 
     * A simple class for storing timing information about a single profile
     * type.
     */
    protected class ProfileRecord {
        private long mStartTime;
        private long mTotalTime;
        private long mMinTime;
        private long mMaxTime;
        
        public void start(long time) {
            mStartTime = time;
        }
        
        public void stop(long time) {
            final long timeDelta = time - mStartTime;
            mTotalTime += timeDelta;
            if (mMinTime == 0 || timeDelta < mMinTime) {
                mMinTime = timeDelta;
            }
            if (mMaxTime == 0 || timeDelta > mMaxTime) {
                mMaxTime = timeDelta;
            }
        }
        
        public long getAverageTime(int frameCount) {
            long time = frameCount > 0 ? mTotalTime / frameCount : 0;
            return time;
        }
        
        public long getMinTime() {
            return mMinTime;
        }
        
        public long getMaxTime() {
            return mMaxTime;
        }
        
        public void startNewProfilePeriod() {
            mTotalTime = 0;
        }
        
        public void reset() {
            mTotalTime = 0;
            mStartTime = 0;
            mMinTime = 0;
            mMaxTime = 0;
        }
    }
}
