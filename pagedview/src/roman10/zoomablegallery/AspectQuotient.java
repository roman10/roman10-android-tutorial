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

import java.util.Observable;

/**
 * Class that holds the aspect quotient, defined as content aspect ratio divided
 * by view aspect ratio.
 */
public class AspectQuotient extends Observable {

    /**
     * Aspect quotient
     */
    private float mAspectQuotient;

    // Public methods

    /**
     * Gets aspect quotient
     * 
     * @return The aspect quotient
     */
    public float get() {
        return mAspectQuotient;
    }

    /**
     * Updates and recalculates aspect quotient based on supplied view and
     * content dimensions.
     * 
     * @param viewWidth Width of view
     * @param viewHeight Height of view
     * @param contentWidth Width of content
     * @param contentHeight Height of content
     */
    public void updateAspectQuotient(float viewWidth, float viewHeight, float contentWidth,
            float contentHeight) {
        final float aspectQuotient = (contentWidth / contentHeight) / (viewWidth / viewHeight);

        if (aspectQuotient != mAspectQuotient) {
            mAspectQuotient = aspectQuotient;
            setChanged();
        }
    }

}
