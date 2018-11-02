package com.seu.magicfilter.detection;

import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;

public class GraphicTracker<T> extends Tracker<T> {
    private GraphicOverlay mOverlay;
    private TrackedGraphic<T> mGraphic;

    public GraphicOverlay getmOverlay() {
        return mOverlay;
    }

    public TrackedGraphic<T> getmGraphic() {
        return mGraphic;
    }

    GraphicTracker(GraphicOverlay overlay, TrackedGraphic<T> graphic) {
        mOverlay = overlay;
        mGraphic = graphic;
    }

    public void remove(){
        mOverlay.remove(mGraphic);
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    @Override
    public void onNewItem(int id, T item) {
        mGraphic.setId(id);
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    @Override
    public void onUpdate(Detector.Detections<T> detectionResults, T item) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(item);
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily, for example if the face was momentarily blocked from
     * view.
     */
    @Override
    public void onMissing(Detector.Detections<T> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }

}
