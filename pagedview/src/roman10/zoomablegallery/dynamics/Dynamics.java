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

package roman10.zoomablegallery.dynamics;

/**
 * Utility class used to handle flinging within a specified limit.
 */
public abstract class Dynamics {
    /**
     * The maximum delta time, in milliseconds, between two updates
     */
    private static final int MAX_TIMESTEP = 50;

    /** The current position */
    protected float mPosition;

    /** The current velocity */
    protected float mVelocity;

    /** The current maximum position */
    protected float mMaxPosition = Float.MAX_VALUE;

    /** The current minimum position */
    protected float mMinPosition = -Float.MAX_VALUE;

    /** The time of the last update */
    protected long mLastTime = 0;

    /**
     * Sets the state of the dynamics object. Should be called before starting
     * to call update.
     * 
     * @param position The current position.
     * @param velocity The current velocity in pixels per second.
     * @param now The current time
     */
    public void setState(final float position, final float velocity, final long now) {
        mVelocity = velocity;
        mPosition = position;
        mLastTime = now;
    }

    /**
     * Returns the current position. Normally used after a call to update() in
     * order to get the updated position.
     * 
     * @return The current position
     */
    public float getPosition() {
        return mPosition;
    }

    /**
     * Gets the velocity. Unit is in pixels per second.
     * 
     * @return The velocity in pixels per second
     */
    public float getVelocity() {
        return mVelocity;
    }

    /**
     * Used to find out if the list is at rest, that is, has no velocity and is
     * inside the the limits. Normally used to know if more calls to update are
     * needed.
     * 
     * @param velocityTolerance Velocity is regarded as 0 if less than
     *            velocityTolerance
     * @param positionTolerance Position is regarded as inside the limits even
     *            if positionTolerance above or below
     * 
     * @return true if list is at rest, false otherwise
     */
    public boolean isAtRest(final float velocityTolerance, final float positionTolerance) {
        final boolean standingStill = Math.abs(mVelocity) < velocityTolerance;
        final boolean withinLimits = mPosition - positionTolerance < mMaxPosition
                && mPosition + positionTolerance > mMinPosition;
        return standingStill && withinLimits;
    }

    /**
     * Sets the maximum position.
     * 
     * @param maxPosition The maximum value of the position
     */
    public void setMaxPosition(final float maxPosition) {
        mMaxPosition = maxPosition;
    }

    /**
     * Sets the minimum position.
     * 
     * @param minPosition The minimum value of the position
     */
    public void setMinPosition(final float minPosition) {
        mMinPosition = minPosition;
    }

    /**
     * Updates the position and velocity.
     * 
     * @param now The current time
     */
    public void update(final long now) {
        int dt = (int)(now - mLastTime);
        if (dt > MAX_TIMESTEP) {
            dt = MAX_TIMESTEP;
        }

        onUpdate(dt);

        mLastTime = now;
    }

    /**
     * Gets the distance to the closest limit (max and min position).
     * 
     * @return If position is more than max position: distance to max position. If
     *         position is less than min position: distance to min position. If
     *         within limits: 0
     */
    protected float getDistanceToLimit() {
        float distanceToLimit = 0;

        if (mPosition > mMaxPosition) {
            distanceToLimit = mMaxPosition - mPosition;
        } else if (mPosition < mMinPosition) {
            distanceToLimit = mMinPosition - mPosition;
        }

        return distanceToLimit;
    }

    /**
     * Updates the position and velocity.
     * 
     * @param dt The delta time since last time
     */
    abstract protected void onUpdate(int dt);
}
