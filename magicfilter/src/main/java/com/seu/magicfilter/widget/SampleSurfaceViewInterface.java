package com.seu.magicfilter.widget;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public interface SampleSurfaceViewInterface {

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height);

    public void draw(Canvas canvas);

}
