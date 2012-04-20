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
 * A ZoomState holds zoom and pan values and allows the user to read and listen
 * to changes. Clients that modify ZoomState should call notifyObservers()
 */
public class ZoomState extends Observable {
    /**
     * Zoom level A value of 1.0 means the content fits the view.
     */
    private float mZoom;

    /**
     * Pan position x-coordinate X-coordinate of zoom window center position,
     * relative to the width of the content.
     */
    private float mPanX;

    /**
     * Pan position y-coordinate Y-coordinate of zoom window center position,
     * relative to the height of the content.
     */
    private float mPanY;

    // Public methods

    /**
     * Get current x-pan
     * 
     * @return current x-pan
     */
    public float getPanX() {
        return mPanX;
    }

    /**
     * Get current y-pan
     * 
     * @return Current y-pan
     */
    public float getPanY() {
        return mPanY;
    }

    /**
     * Get current zoom value
     * 
     * @return Current zoom value
     */
    public float getZoom() {
        return mZoom;
    }

    /**
     * Help function for calculating current zoom value in x-dimension
     * 
     * @param aspectQuotient (Aspect ratio content) / (Aspect ratio view)
     * @return Current zoom value in x-dimension
     */
    public float getZoomX(float aspectQuotient) {
        return Math.min(mZoom, mZoom * aspectQuotient);
    }

    /**
     * Help function for calculating current zoom value in y-dimension
     * 
     * @param aspectQuotient (Aspect ratio content) / (Aspect ratio view)
     * @return Current zoom value in y-dimension
     */
    public float getZoomY(float aspectQuotient) {
        return Math.min(mZoom, mZoom / aspectQuotient);
    }

    /**
     * Set pan-x
     * 
     * @param panX Pan-x value to set
     */
    public void setPanX(float panX) {
        if (panX != mPanX) {
            mPanX = panX;
            setChanged();
        }
    }

    /**
     * Set pan-y
     * 
     * @param panY Pan-y value to set
     */
    public void setPanY(float panY) {
        if (panY != mPanY) {
            mPanY = panY;
            setChanged();
        }
    }

    /**
     * Set zoom
     * 
     * @param zoom Zoom value to set
     */
    public void setZoom(float zoom) {
        if (zoom != mZoom) {
            mZoom = zoom;
            setChanged();
        }
    }

}
