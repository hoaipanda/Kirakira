package com.seu.magicfilter.detection;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.seu.magicfilter.camera.utils.CameraEngine;
import com.seu.magicfilter.filter.advanced.MagicBeautyFilter;
import com.seu.magicfilter.filter.base.GPUImage;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.widget.CameraView;

import java.io.File;
import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {
    private static final String TAG = "CameraSourcePreview";

    private Context mContext;
    private CameraView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSource mCameraSource;
    private int mFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private GraphicOverlay mOverlay;
    private boolean isCreated;

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        mSurfaceView = new CameraView(context);
        addView(mSurfaceView);
        mSurfaceView.setCameraListener(new CameraView.OnCameraListener() {
            @Override
            public void onReady() {
                startCamera();
            }

            @Override
            public void onDestroy() {
                isCreated = false;
            }
        });
    }

    private void startCamera() {
        isCreated = true;
        try {
            start(mCameraSource);
            mSurfaceView.openCamera(false);
            if (listener != null) {
                listener.onCameraReady();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void focusCamera(MotionEvent event) {
        mSurfaceView.touchFocusEvent(event);
    }

    public int getMaxLightCamera() {
        return mCameraSource.getMaxLightCamera();
    }

    public void setLightCamera(int lightCamera) {
        mCameraSource.setLightCamera(lightCamera);
    }

    public void setFilter(MagicFilterType filter, Context context) {
        mSurfaceView.setFilter(filter, context);
    }

    public CameraView getmSurfaceView() {
        return mSurfaceView;
    }

    public void releaseCame() {
        mCameraSource.release();
    }

    public void switchCamera() {
        mCameraSource.release1();
        int facing = mFacing == 0 ? 1 : 0;
        try {
            mCameraSource.start(mSurfaceView.getHolder(), facing);
            mFacing = facing;
            CameraEngine.setCamera(mCameraSource.getCamera());
            CameraEngine.setCameraID(mCameraSource.getCameraFacing());
            mSurfaceView.openCamera(mFacing == 0 ? false : true);
            Size size = mCameraSource.getPreviewSize();
            int min = Math.min(size.getWidth(), size.getHeight());
            int max = Math.max(size.getWidth(), size.getHeight());
            mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
            mSurfaceView.openCamera(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mFacing = mFacing == 0 ? 1 : 0;
//        try {
//            mCameraSource.start(mSurfaceView.getHolder(), mFacing);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void switchFlashlight(int mode) {
        mCameraSource.setFlashMode(mode);
    }

    public void startRecord(File fileOutput) {
        mSurfaceView.setFileOutputVideo(fileOutput);
        mSurfaceView.changeRecordingState(true);
    }

    public void stopRecord() {
        mSurfaceView.changeRecordingState(false);
    }

    public void start(CameraSource cameraSource) throws IOException {
        if (cameraSource == null) {
            stop();
        }

        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady(mFacing);
        }
    }

    public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
        mOverlay = overlay;
        mCameraSource = cameraSource;
        if (isCreated) {
            startCamera();
        }
    }


    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    private void startIfReady(int mFacing) throws IOException {
//        if (mStartRequested && mSurfaceAvailable) {
        mCameraSource.start(mSurfaceView.getHolder(), mFacing);
        if (mOverlay != null) {
            Size size = mCameraSource.getPreviewSize();
            int min = Math.min(size.getWidth(), size.getHeight());
            int max = Math.max(size.getWidth(), size.getHeight());
            if (isPortraitMode()) {
                // Swap width and height sizes when in portrait, since it will be rotated by
                // 90 degrees
                mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
            } else {
                mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
            }
            mOverlay.clear();
        }
        CameraEngine.setCamera(mCameraSource.getCamera());
        CameraEngine.setCameraID(mCameraSource.getCameraFacing());
        mStartRequested = false;
//        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            mSurfaceAvailable = true;
            try {
                startIfReady(mFacing);
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 640;
        int height = 480;
        if (mCameraSource != null) {
            Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;

        // Computes height and width for potentially doing fit width.
        int childWidth = layoutWidth;
        int childHeight = (int) (((float) layoutWidth / (float) width) * height);

        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight;
            childWidth = (int) (((float) layoutHeight / (float) height) * width);
        }

        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(0, 0, childWidth, childHeight);
        }

        try {
            startIfReady(mFacing);
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        return false;
    }

    private OnCameraSourcePreviewListener listener;

    public void setListener(OnCameraSourcePreviewListener listener) {
        this.listener = listener;
    }

    public interface OnCameraSourcePreviewListener {
        void onCameraReady();
    }
}
