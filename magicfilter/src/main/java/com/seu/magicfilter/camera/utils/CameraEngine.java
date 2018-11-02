package com.seu.magicfilter.camera.utils;

import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.Log;

import com.seu.magicfilter.widget.MagicCameraView;

import java.io.IOException;
import java.util.List;

public class CameraEngine {
    private static Camera camera = null;
    private static int cameraID = 0;
    private static SurfaceTexture surfaceTexture;
    private static int ratiocamera = 0;
    private static int mPreviewWidth = 0, mPreviewHeight = 0;

    public static Camera getCamera() {
        return camera;
    }

    public static void setCamera(Camera camera) {
        CameraEngine.camera = camera;
    }

    public static void setCameraID(int cameraID) {
        CameraEngine.cameraID = cameraID;
    }

    public static boolean openCamera() {
        if (camera == null) {
            try {
                camera = Camera.open(cameraID);
                setDefaultParameters();
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean openCamera(int id) {
        if (camera == null) {
            try {
                camera = Camera.open(id);
                cameraID = id;
                setDefaultParameters();
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
        return false;
    }

    public static void releaseCamera() {
        if (camera != null) {
//            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void resumeCamera() {
        openCamera();
    }

    public void setParameters(Parameters parameters) {
        camera.setParameters(parameters);
    }

    public Parameters getParameters() {
        if (camera != null)
            camera.getParameters();
        return null;
    }

    public static void switchCamera() {
        releaseCamera();
        cameraID = cameraID == 0 ? 1 : 0;
        openCamera(cameraID);
        startPreview(surfaceTexture);
    }

    public static void switchFlashlight(int modeSelect) { // 0: off, 1: on, 2: auto, 3: torch
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            String mode = parameters.getFlashMode();
            if (mode != null) {
                if (modeSelect == 0) {
                    List<String> modes = parameters.getSupportedFlashModes();
                    if (modes != null) {
                        if (modes.contains(Parameters.FLASH_MODE_OFF)) {
                            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                        }
                    }
                } else if (modeSelect == 1) {
                    List<String> modes = parameters.getSupportedFlashModes();
                    if (modes != null) {
                        if (modes.contains(Parameters.FLASH_MODE_ON)) {
                            parameters.setFlashMode(Parameters.FLASH_MODE_ON);
                        }
                    }
                } else if (modeSelect == 2) {
                    List<String> modes = parameters.getSupportedFlashModes();
                    if (modes != null) {
                        if (modes.contains(Parameters.FLASH_MODE_AUTO)) {
                            parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
                        }
                    }
                } else if (modeSelect == 3) {
                    List<String> modes = parameters.getSupportedFlashModes();
                    if (modes != null) {
                        if (modes.contains(Parameters.FLASH_MODE_TORCH)) {
                            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        }
                    }
                }
                camera.setParameters(parameters);
            } else {
            }

        }
    }

    public static int getMaxLightCamera() {
        return camera.getParameters().getMaxExposureCompensation();

    }

    public static void setLightCamera(int param) {
        if (camera != null) {
            Parameters parameters = camera.getParameters();
            parameters.setExposureCompensation(param);
            camera.setParameters(parameters);
        }

    }

    private static void setDefaultParameters() {
        Parameters parameters = camera.getParameters();
        List<Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size result = null;
        List<Size> picsizes = parameters.getSupportedPictureSizes();
        Camera.Size picresult = null;

        for (int i = 0; i < sizes.size(); i++) {
            result = (Camera.Size) sizes.get(i);
            if ((float) result.width / result.height == (float) (4 / (float) 3) && result.width < 2048) {
                parameters.setPreviewSize(result.width, result.height);
                break;
            }
        }

        for (int i = 0; i < picsizes.size(); i++) {
            picresult = (Camera.Size) picsizes.get(i);
            if ((float) picresult.width / picresult.height == (float) (4 / (float) 3) && picresult.width < 2048) {
                parameters.setPictureSize(picresult.width, picresult.height);
                break;
            }
        }

        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        camera.setParameters(parameters);
    }

    private static Size getPreviewSize() {
        return camera.getParameters().getPreviewSize();
    }

    private static Size getPictureSize() {
        return camera.getParameters().getPictureSize();
    }

    public static void startPreview(SurfaceTexture surfaceTexture) {
        if (camera != null)
            try {
                camera.setPreviewTexture(surfaceTexture);
                CameraEngine.surfaceTexture = surfaceTexture;
                camera.startPreview();
//                startFaceDetecter();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void startPreview() {
        if (camera != null) {
            camera.startPreview();
//            startFaceDetecter();
        }
    }

    public static void stopPreview() {
        if (camera != null)
            camera.stopPreview();
    }

    public static void setRotation(int rotation) {
        Camera.Parameters params = camera.getParameters();
        params.setRotation(rotation);
        camera.setParameters(params);
    }

    public static void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback,
                                   Camera.PictureCallback jpegCallback) {
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    public static com.seu.magicfilter.camera.utils.CameraInfo getCameraInfo() {
        com.seu.magicfilter.camera.utils.CameraInfo info = new com.seu.magicfilter.camera.utils.CameraInfo();

        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraID, cameraInfo);

        Parameters parameters = camera.getParameters();
        List<Size> sizes = parameters.getSupportedPreviewSizes();
        List<Size> picsizes = parameters.getSupportedPictureSizes();

        Camera.Size result = null;
        Camera.Size picresult = null;
        for (int i = 0; i < sizes.size(); i++) {
            result = (Camera.Size) sizes.get(i);

            if ((float) result.width / result.height == ((float) 4 / 3) && result.width < 2048) {
                mPreviewWidth = result.width;
                mPreviewHeight = result.height;
                info.previewWidth = mPreviewWidth;
                info.previewHeight = mPreviewHeight;
                break;

            } else {
                mPreviewWidth = sizes.get(1).width;
                mPreviewHeight = sizes.get(1).width;
                info.previewWidth = mPreviewWidth;
                info.previewHeight = mPreviewHeight;
            }
        }

        for (int i = 0; i < picsizes.size(); i++) {
            picresult = (Camera.Size) picsizes.get(i);

            if ((float) picresult.width / picresult.height == ((float) 4 / 3) && picresult.width < 2048) {
                info.pictureWidth = picresult.width;
                info.pictureHeight = picresult.height;
                break;
            }
        }

        info.orientation = cameraInfo.orientation;
        info.isFront = cameraID == 1 ? true : false;
        return info;
    }

    public static CameraFaceDetector mCameraFaceDetector;
    public static Handler handler = new Handler();
    private static boolean enbFaceDetect = true;
    private static Rect curentRect;
    public static Runnable mRunasble;
    private static boolean isFocus = false;

    public static boolean isEnbFaceDetect() {
        return enbFaceDetect;
    }

    public static void setEnbFaceDetect(boolean enbFaceDetecta) {
        enbFaceDetect = enbFaceDetecta;
    }

    public static void startFaceDetecter() {
        camera.setFaceDetectionListener(mFaceDetectionListener);
        camera.startFaceDetection();
        Log.v("datdb", "startFaceDetection");
    }

    public static void stopFaceDetecter() {
        try {
            camera.stopFaceDetection();
            Log.v("datdb", "stopFaceDetection");
        } catch (Exception vailon) {
            Log.e("datdb", "" + vailon);
        }
    }

    public static void addFaceDetector(CameraFaceDetector vlmCameraFaceDetector) {
        mCameraFaceDetector = vlmCameraFaceDetector;
    }

    static Camera.FaceDetectionListener mFaceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
//            Log.d("datdb", "face length: " + faces.length);
            if (enbFaceDetect && faces.length > 0) {
                final Camera.Face mFace = faces[0];
//                Log.d("datdb",
//                        "rect: " + mFace.rect
//                );
                mCameraFaceDetector.onDetected(mFace.rect);
                mRunasble = new Runnable() {
                    @Override
                    public void run() {
                        isFocus = true;
                        mCameraFaceDetector.onFocus(mFace.rect);
                        Log.v("FACEDETETER", "HANDLER FOCUS CHUP");
                    }
                };
                if (curentRect != null) {


                    if (curentRect.contains(mFace.rect.centerX(), mFace.rect.centerY())) {

                        if (!isFocus) {
                            handler.removeCallbacksAndMessages(null);
                            handler = null;
                            handler = new Handler();
                            handler.postDelayed(mRunasble, 2100);
                            Log.v("FACEDETETER", "Phải đấy, set handler thôi ahihi");
                            isFocus = true;
                        }
                    } else {
                        isFocus = false;
                        Log.v("FACEDETETER", "Đéo phải");
                        Rect a = mFace.rect;
                        curentRect = new Rect(a.centerX() - 100, a.centerY() - 100, a.centerX() + 100, a.centerY() + 100);
                        handler.removeCallbacksAndMessages(null);
                        handler = null;
                        handler = new Handler();

                    }
                } else {
                    Rect a = mFace.rect;
                    curentRect = new Rect(a.centerX() - 69, a.centerY() - 69, a.centerX() + 69, a.centerY() + 69);
                }


            } else {
                handler.removeCallbacksAndMessages(null);
                handler = null;
                handler = new Handler();
                mCameraFaceDetector.onDetected(null);
            }
        }
    };

    public interface CameraFaceDetector {
        void onDetected(Rect faceRect);

        void onFocus(Rect faceRect);
    }
}