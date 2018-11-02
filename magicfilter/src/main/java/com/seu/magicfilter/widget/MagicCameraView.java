package com.seu.magicfilter.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.seu.magicfilter.camera.utils.CameraEngine;
import com.seu.magicfilter.camera.utils.CameraInfo;
import com.seu.magicfilter.encoder.video.TextureMovieEncoder;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.filter.helper.MagicFilterFactory;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.AsynRender;
import com.seu.magicfilter.helper.GetPictureTask;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.widget.base.MagicBaseView;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by why8222 on 2016/2/25.
 */
public class MagicCameraView extends MagicBaseView {

    private MagicCameraInputFilter cameraInputFilter;
    private MagicFilterType currentFilter;

    private SurfaceTexture surfaceTexture;

    private Camera mCamera;

    public MagicCameraView(Context context) {
        this(context, null);
    }

    private boolean recordingEnabled;
    private int recordingStatus;

    private static final int RECORDING_OFF = 0;
    private static final int RECORDING_ON = 1;
    private static final int RECORDING_RESUMED = 2;
    private TextureMovieEncoder videoEncoder;

    private File outputFile;
    private int dgrees = 0;

    public void setFileOutputVideo(File fileOutputVideo) {
        this.outputFile = fileOutputVideo;
    }

    public MagicCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
        outputFile = new File(MagicParams.videoPath, Calendar.getInstance().getTimeInMillis() + ".mp4");
        recordingStatus = -1;
        recordingEnabled = false;
        scaleType = ScaleType.CENTER_CROP;
        videoEncoder = new TextureMovieEncoder(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        recordingEnabled = videoEncoder.isRecording();
        if (recordingEnabled)
            recordingStatus = RECORDING_RESUMED;
        else
            recordingStatus = RECORDING_OFF;
        if (cameraInputFilter == null)
            cameraInputFilter = new MagicCameraInputFilter();
        cameraInputFilter.init();
        if (textureId == OpenGlUtils.NO_TEXTURE) {
            textureId = OpenGlUtils.getExternalOESTextureID();
            if (textureId != OpenGlUtils.NO_TEXTURE) {
                surfaceTexture = new SurfaceTexture(textureId);
                surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        openCamera();
        cameraListener.onReady();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (surfaceTexture == null)
            return;
        surfaceTexture.updateTexImage();
        if (recordingEnabled) {
            switch (recordingStatus) {
                case RECORDING_OFF:
                    CameraInfo info = CameraEngine.getCameraInfo();
                    videoEncoder.setPreviewSize(info.previewWidth, info.pictureHeight);
                    videoEncoder.setTextureBuffer(gLTextureBuffer);
                    videoEncoder.setCubeBuffer(gLCubeBuffer);
                    videoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                            outputFile, info.previewHeight, info.previewWidth,
                            1800000, EGL14.eglGetCurrentContext(),
                            info));
                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_RESUMED:
                    videoEncoder.updateSharedContext(EGL14.eglGetCurrentContext());
                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_ON:
                    break;
                default:
                    throw new RuntimeException("unknown status " + recordingStatus);
            }
        } else {
            switch (recordingStatus) {
                case RECORDING_ON:
                case RECORDING_RESUMED:
                    videoEncoder.stopRecording();
                    recordingStatus = RECORDING_OFF;
                    break;
                case RECORDING_OFF:
                    break;
                default:
                    throw new RuntimeException("unknown status " + recordingStatus);
            }
        }
        float[] mtx = new float[16];
        surfaceTexture.getTransformMatrix(mtx);
        cameraInputFilter.setTextureTransformMatrix(mtx);
        int id = textureId;
        if (filter == null) {
            cameraInputFilter.onDrawFrame(textureId, gLCubeBuffer, gLTextureBuffer);
        } else {
            id = cameraInputFilter.onDrawToTexture(textureId);
            filter.onDrawFrame(id, gLCubeBuffer, gLTextureBuffer);
        }
        videoEncoder.setTextureId(id);
        videoEncoder.frameAvailable(surfaceTexture);
    }

    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            requestRender();
        }
    };

    @Override
    public void setFilter(MagicFilterType type, Context context) {
        super.setFilter(type, context);
        this.currentFilter = type;
        videoEncoder.setFilter(type);
    }

    private void openCamera() {
        if (CameraEngine.getCamera() == null) {
            CameraEngine.openCamera();
            CameraInfo info = CameraEngine.getCameraInfo();
            if (info.orientation == 90 || info.orientation == 270) {
                imageWidth = info.previewHeight;
                imageHeight = info.previewWidth;
            } else {
                imageWidth = info.previewWidth;
                imageHeight = info.previewHeight;
            }
            cameraInputFilter.onInputSizeChanged(imageWidth, imageHeight);
            adjustSize(info.orientation, info.isFront, true);
            if (surfaceTexture != null)
                CameraEngine.startPreview(surfaceTexture);
        }
        mCamera = CameraEngine.getCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        CameraEngine.releaseCamera();
    }

    public void changeRecordingState(boolean isRecording) {
        recordingEnabled = isRecording;
    }

    protected void onFilterChanged() {
        super.onFilterChanged();
        cameraInputFilter.onOutputSizeChanged(surfaceWidth, surfaceHeight);
        if (filter != null)
            cameraInputFilter.initCameraFrameBuffer(imageWidth, imageHeight);
        else
            cameraInputFilter.destroyFramebuffers();
    }

    @Override
    public void savePicture(final GetPictureTask getPictureTask) {
        CameraEngine.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
//                CameraEngine.stopPreview();
//                AsynRender mAsynRender = new AsynRender(getContext(), MagicFilterFactory.initFilters(currentFilter, getContext()), data, new AsynRender.RenderCompleted() {
//                    @Override
//                    public void Done(Bitmap mbitmap) {
//                        Bitmap bm = rotateBitmap(mbitmap, CameraEngine.getCameraInfo().isFront, dgrees);
//                        getPictureTask.execute(bm);
//                        CameraEngine.startPreview();
//                    }
//                });
//                mAsynRender.execute();
            }
        });
    }

    private Bitmap rotateBitmap(Bitmap bitmap, boolean isFrontCam, int dgrees) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();

        if (isFrontCam) {
            mtx.postRotate(270 + dgrees);
            mtx.postScale(-1, 1);
        } else {
            if (dgrees > 0) {
                mtx.postRotate(270 + dgrees);
            } else {
                mtx.postRotate(90 + dgrees);
            }
        }
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    public void TouchFocusEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();

//        if (touchMajor == 0 || touchMinor == 0) {
//            touchMajor = touchMinor = this.getWidth() / 5;
//        }
        touchMajor = touchMinor = this.getWidth() / 5;

        Rect touchRect = new Rect(
                (int) (x - touchMajor / 2),
                (int) (y - touchMinor / 2),
                (int) (x + touchMajor / 2),
                (int) (y + touchMinor / 2));
        this.touchFocus(touchRect);
    }

    public void touchFocus(final Rect tfocusRect) {

        //Convert from View's width and height to +/- 1000
        final Rect targetFocusRect = new Rect(
                tfocusRect.left * 2000 / getWidth() - 1000,
                tfocusRect.top * 2000 / getHeight() - 1000,
                tfocusRect.right * 2000 / getWidth() - 1000,
                tfocusRect.bottom * 2000 / getHeight() - 1000);

        final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
        Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
        focusList.add(focusArea);
        openCamera();
        if (mCamera.getParameters() != null) {
            try {
                Camera.Parameters para = mCamera.getParameters();
                para.setFocusAreas(focusList);
                para.setMeteringAreas(focusList);
                mCamera.setParameters(para);
                // mCamera.autoFocus(myAutoFocusCallback);
            } catch (Exception e) {
            }
        }
    }


    public void setDgrees(int dgrees) {
        this.dgrees = dgrees;
    }

    public void onBeautyLevelChanged() {
        cameraInputFilter.onBeautyLevelChanged();
    }

    private OnCameraListener cameraListener;

    public interface OnCameraListener {
        void onReady();
    }

    public void setCameraListener(OnCameraListener listener) {
        this.cameraListener = listener;
    }
}
