package com.seu.magicfilter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.seu.magicfilter.camera.utils.CameraEngine;
import com.seu.magicfilter.camera.utils.CameraInfo;
import com.seu.magicfilter.encoder.video.TextureMovieEncoder;
import com.seu.magicfilter.filter.advanced.MagicBeautyFilter;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.filter.helper.FilterContext;
import com.seu.magicfilter.filter.helper.MagicFilterFactory;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.AsynRender;
import com.seu.magicfilter.helper.GetPictureTask;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.widget.base.MagicBaseView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraView extends MagicBaseView {

    private MagicCameraInputFilter cameraInputFilter;
    private MagicFilterType currentFilter;
    private SurfaceTexture surfaceTexture;

    private Camera mCamera;

    public CameraView(Context context) {
        this(context, null);
        this.getHolder().addCallback(this);
        outputFile = new File(MagicParams.videoPath, Calendar.getInstance().getTimeInMillis() + ".mp4");
        recordingStatus = -1;
        recordingEnabled = false;
        scaleType = ScaleType.CENTER_CROP;
        videoEncoder = new TextureMovieEncoder(context);
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

    public CameraView(Context context, AttributeSet attrs) {
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
        if (cameraInputFilter == null) {
            cameraInputFilter = new MagicCameraInputFilter();
        }
        cameraInputFilter.init();
        if (textureId == OpenGlUtils.NO_TEXTURE) {
            textureId = OpenGlUtils.getExternalOESTextureID();
            if (textureId != OpenGlUtils.NO_TEXTURE) {
                surfaceTexture = new SurfaceTexture(textureId);
                surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
            }
        }
        Log.d("datdb", "onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        cameraListener.onReady();
        Log.d("datdb", "onSurfaceChanged");
//        openCamera(false);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        cameraListener.onDestroy();
        Log.d("datdb", "surfaceDestroyed");
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

    public void openCamera(boolean isFront) {
        mCamera = CameraEngine.getCamera();
        CameraInfo info = CameraEngine.getCameraInfo();
        if (info.orientation == 90 || info.orientation == 270) {
            imageWidth = info.previewHeight;
            imageHeight = info.previewWidth;
        } else {
            imageWidth = info.previewWidth;
            imageHeight = info.previewHeight;
        }
        cameraInputFilter.onInputSizeChanged(imageWidth, imageHeight);
        adjustSize(0, false, true);
        if (surfaceTexture != null) {
            try {
                mCamera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                CameraEngine.stopPreview();
                Glide.with(FilterContext.context).load(data).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        final Bitmap bm = ((BitmapDrawable) resource).getBitmap();

                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                MagicBeautyFilter magicBeautyFilter = new MagicBeautyFilter();
                                magicBeautyFilter.onInit();
                                magicBeautyFilter.onInputSizeChanged(bm.getWidth(), bm.getHeight());
                                magicBeautyFilter.onOutputSizeChanged(bm.getWidth(), bm.getHeight());
                                new AsynRender(FilterContext.context, magicBeautyFilter, MagicFilterFactory.initFilters(currentFilter, FilterContext.context), bm, new AsynRender.RenderCompleted() {
                                    @Override
                                    public void Done(Bitmap mbitmap) {
                                        Bitmap result = rotateBitmap(mbitmap, CameraEngine.getCameraInfo().isFront, dgrees);
                                        getPictureTask.execute(result);
                                        CameraEngine.startPreview();
                                    }
                                }).execute();
                            }
                        });
                    }
                });
            }
        });
    }

//    private Bitmap drawPhoto(Bitmap bitmap, boolean isRotated) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//
//        int[] mFrameBuffers = new int[1];
//        int[] mFrameBufferTextures = new int[1];
//        if (beautyFilter == null)
//            beautyFilter = new MagicBeautyFilter();
//        beautyFilter.init();
//        beautyFilter.onDisplaySizeChanged(width, height);
//        beautyFilter.onInputSizeChanged(width, height);
//
//        if (filter != null) {
//            filter.onInputSizeChanged(width, height);
//            filter.onDisplaySizeChanged(width, height);
//        }
//        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
//        GLES20.glGenTextures(1, mFrameBufferTextures, 0);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
//                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
//        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
//                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
//
//        GLES20.glViewport(0, 0, width, height);
//        int textureId = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, true);
////        int textureId = loadTexture(bitmap);
//
//        FloatBuffer gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        FloatBuffer gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
//        if (isRotated)
//            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, false)).position(0);
//        else
//            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);
//
//        if (filter == null) {
//            beautyFilter.onDrawFrame(textureId, gLCubeBuffer, gLTextureBuffer);
//        } else {
//            beautyFilter.onDrawFrame(textureId);
//            filter.onDrawFrame(mFrameBufferTextures[0], gLCubeBuffer, gLTextureBuffer);
//        }
//        IntBuffer ib = IntBuffer.allocate(width * height);
//        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
//        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        result.copyPixelsFromBuffer(ib);
//
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//        GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
//        GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
//        GLES20.glDeleteTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);
//
//        beautyFilter.destroy();
//        beautyFilter = null;
//        if (filter != null) {
//            filter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
//            filter.onInputSizeChanged(imageWidth, imageHeight);
//        }
//        return result;
//    }

    public int loadTexture(Bitmap bitmap) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    private Bitmap rotateBitmap(Bitmap bitmap, boolean isFrontCam, int dgrees) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();

        if (isFrontCam) {
//            mtx.postRotate(270 + dgrees);
            mtx.postScale(-1, 1);
        } else {
//            if (dgrees > 0) {
//                mtx.postRotate(270 + dgrees);
//            } else {
//                mtx.postRotate(90 + dgrees);
//            }
        }
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public void touchFocusEvent(MotionEvent event) {
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

    private void touchFocus(final Rect tfocusRect) {

        //Convert from View's width and height to +/- 1000
        final Rect targetFocusRect = new Rect(
                tfocusRect.left * 2000 / getWidth() - 1000,
                tfocusRect.top * 2000 / getHeight() - 1000,
                tfocusRect.right * 2000 / getWidth() - 1000,
                tfocusRect.bottom * 2000 / getHeight() - 1000);

        final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
        Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
        focusList.add(focusArea);
        openCamera(false);
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

        void onDestroy();
    }

    public void setCameraListener(OnCameraListener listener) {
        this.cameraListener = listener;
    }
}
