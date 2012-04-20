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
 * SpringDynamics is a Dynamics object that uses friction and spring physics to
 * snap to boundaries and give a natural and organic dynamic.
 */
public class SpringDynamics extends Dynamics {

    /** Friction factor */
    private float mFriction;

    /** Spring stiffness factor */
    private float mStiffness;

    /** Spring damping */
    private float mDamping;

    /**
     * Set friction parameter, friction physics are applied when inside of snap
     * bounds.
     * 
     * @param friction Friction factor
     */
    public void setFriction(float friction) {
        mFriction = friction;
    }

    /**
     * Set spring parameters, spring physics are applied when outside of snap
     * bounds.
     * 
     * @param stiffness Spring stiffness
     * @param dampingRatio Damping ratio, < 1 underdamped, > 1 overdamped
     */
    public void setSpring(float stiffness, float dampingRatio) {
        mStiffness = stiffness;
        mDamping = dampingRatio * 2 * (float)Math.sqrt(stiffness);
    }

    /**
     * Calculate acceleration at the current state
     * 
     * @return Current acceleration
     */
    private float calculateAcceleration() {
        float acceleration;

        final float distanceFromLimit = getDistanceToLimit();
        if (distanceFromLimit != 0) {
            acceleration = distanceFromLimit * mStiffness - mDamping * mVelocity;
        } else {
            acceleration = -mFriction * mVelocity;
        }

        return acceleration;
    }

    @Override
    protected void onUpdate(int dt) {
        // Update position and velocity using the Velocity verlet algorithm

        // Calculate dt in seconds as float
        final float fdt = dt / 1000f;

        // Calculate current acceleration
        final float at = calculateAcceleration();

        // Calculate next position based on current velocity and acceleration
        mPosition += mVelocity * fdt + .5f * at * fdt * fdt;

        // Calculate velocity at time t + dt/2
        // (that is velocity at half way to new time)
        mVelocity += .5f * at * fdt;

        // Calculate acceleration at new position,
        // will be used for calculating velocity at next position.
        final float atdt = calculateAcceleration();

        // Calculate velocity at time (t + dt/2) + dt/2 = t + dt
        // (that is velocity at the new time)
        mVelocity += .5f * atdt * fdt;
    }

}
