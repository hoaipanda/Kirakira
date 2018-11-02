package com.cameraeffects.kirakira.filter.base;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;


public class analog3_GPUImage {
    private final Context mContext;
    private final analog3_GPUImageRenderer mRenderer;
    private GLSurfaceView mGlSurfaceView;
    private analog3_GPUImageFilter mFilter;
    private Bitmap mCurrentBitmap;
    private ScaleType mScaleType = ScaleType.CENTER_CROP;

    public analog3_GPUImage(final Context context) {
        if (!supportsOpenGLES2(context)) {
            throw new IllegalStateException("OpenGL ES 2.0 is not supported on this phone.");
        }

        mContext = context;
        mFilter = new analog3_GPUImageFilter();
        mRenderer = new analog3_GPUImageRenderer(mFilter);
    }

    private boolean supportsOpenGLES2(final Context context) {
        final ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    public void setGLSurfaceView(final GLSurfaceView view) {
        mGlSurfaceView = view;
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGlSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGlSurfaceView.setRenderer(mRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGlSurfaceView.requestRender();
    }

    public void setBackgroundColor(float red, float green, float blue) {
        mRenderer.setBackgroundColor(red, green, blue);
    }
    public void requestRender() {
        if (mGlSurfaceView != null) {
            mGlSurfaceView.requestRender();
        }
    }

    public void setUpCamera(final Camera camera) {
        setUpCamera(camera, 0, false, false);
    }

    public void setUpCamera(final Camera camera, final int degrees, final boolean flipHorizontal,
                            final boolean flipVertical) {
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            setUpCameraGingerbread(camera);
        } else {
            camera.setPreviewCallback(mRenderer);
            camera.startPreview();
        }
        analog3_Rotation filter2Rotation = analog3_Rotation.NORMAL;
        switch (degrees) {
            case 90:
                filter2Rotation = analog3_Rotation.ROTATION_90;
                break;
            case 180:
                filter2Rotation = analog3_Rotation.ROTATION_180;
                break;
            case 270:
                filter2Rotation = analog3_Rotation.ROTATION_270;
                break;
        }
        mRenderer.setRotationCamera(filter2Rotation, flipHorizontal, flipVertical);
    }

    @TargetApi(11)
    private void setUpCameraGingerbread(final Camera camera) {
        mRenderer.setUpSurfaceTexture(camera);
    }

    public void setFilter(final analog3_GPUImageFilter filter) {
        mFilter = filter;
        mRenderer.setFilter(mFilter);
        requestRender();
    }

    public void setImage(final Bitmap bitmap) {
        mCurrentBitmap = bitmap;
        mRenderer.setImageBitmap(bitmap, false);
        requestRender();
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
        mRenderer.setScaleType(scaleType);
        mRenderer.deleteImage();
        mCurrentBitmap = null;
        requestRender();
    }

    public void setRotation(analog3_Rotation filter2Rotation) {
        mRenderer.setRotation(filter2Rotation);
    }

    public void setRotation(analog3_Rotation filter2Rotation, boolean flipHorizontal, boolean flipVertical) {
        mRenderer.setRotation(filter2Rotation, flipHorizontal, flipVertical);
    }

    public void deleteImage() {
        mRenderer.deleteImage();
        mCurrentBitmap = null;
        requestRender();
    }
    public void setImage(final Uri uri) {
        new LoadImageUriTask(this, uri).execute();
    }

    public void setImage(final File file) {
        new LoadImageFileTask(this, file).execute();
    }

    private String getPath(final Uri uri) {
        String[] projection = {
                MediaStore.Images.Media.DATA,
        };
        Cursor cursor = mContext.getContentResolver()
                .query(uri, projection, null, null, null);
        int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String path = null;
        if (cursor.moveToFirst()) {
            path = cursor.getString(pathIndex);
        }
        cursor.close();
        return path;
    }

    public Bitmap getBitmapWithFilterApplied() {
        return getBitmapWithFilterApplied(mCurrentBitmap);
    }

    public Bitmap getBitmapWithFilterApplied(final Bitmap bitmap) {
        if (mGlSurfaceView != null) {
            mRenderer.deleteImage();
            mRenderer.runOnDraw(new Runnable() {

                @Override
                public void run() {
                    synchronized(mFilter) {
                        mFilter.destroy();
                        mFilter.notify();
                    }
                }
            });
            synchronized(mFilter) {
                requestRender();
                try {
                    mFilter.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        analog3_GPUImageRenderer renderer = new analog3_GPUImageRenderer(mFilter);
        renderer.setRotation(analog3_Rotation.NORMAL,
                mRenderer.isFlippedHorizontally(), mRenderer.isFlippedVertically());
        renderer.setScaleType(mScaleType);
        analog3_PixelBuffer buffer = new analog3_PixelBuffer(bitmap.getWidth(), bitmap.getHeight());
        buffer.setRenderer(renderer);
        renderer.setImageBitmap(bitmap, false);
        Bitmap result = buffer.getBitmap();
        mFilter.destroy();
        renderer.deleteImage();
        buffer.destroy();

        mRenderer.setFilter(mFilter);
        if (mCurrentBitmap != null) {
            mRenderer.setImageBitmap(mCurrentBitmap, false);
        }
        requestRender();

        return result;
    }

    public static void getBitmapForMultipleFilters(final Bitmap bitmap,
                                                   final List<analog3_GPUImageFilter> filters, final ResponseListener<Bitmap> listener) {
        if (filters.isEmpty()) {
            return;
        }
        analog3_GPUImageRenderer renderer = new analog3_GPUImageRenderer(filters.get(0));
        renderer.setImageBitmap(bitmap, false);
        analog3_PixelBuffer buffer = new analog3_PixelBuffer(bitmap.getWidth(), bitmap.getHeight());
        buffer.setRenderer(renderer);

        for (analog3_GPUImageFilter filter : filters) {
            renderer.setFilter(filter);
            listener.response(buffer.getBitmap());
            filter.destroy();
        }
        renderer.deleteImage();
        buffer.destroy();
    }

    @Deprecated
    public void saveToPictures(final String folderName, final String fileName,
                               final OnPictureSavedListener listener) {
        saveToPictures(mCurrentBitmap, folderName, fileName, listener);
    }

    @Deprecated
    public void saveToPictures(final Bitmap bitmap, final String folderName, final String fileName,
                               final OnPictureSavedListener listener) {
        new SaveTask(bitmap, folderName, fileName, listener).execute();
    }

    void runOnGLThread(Runnable runnable) {
        mRenderer.runOnDrawEnd(runnable);
    }

    private int getOutputWidth() {
        if (mRenderer != null && mRenderer.getFrameWidth() != 0) {
            return mRenderer.getFrameWidth();
        } else if (mCurrentBitmap != null) {
            return mCurrentBitmap.getWidth();
        } else {
            WindowManager windowManager =
                    (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            return display.getWidth();
        }
    }

    private int getOutputHeight() {
        if (mRenderer != null && mRenderer.getFrameHeight() != 0) {
            return mRenderer.getFrameHeight();
        } else if (mCurrentBitmap != null) {
            return mCurrentBitmap.getHeight();
        } else {
            WindowManager windowManager =
                    (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            return display.getHeight();
        }
    }

    @Deprecated
    private class SaveTask extends AsyncTask<Void, Void, Void> {

        private final Bitmap mBitmap;
        private final String mFolderName;
        private final String mFileName;
        private final OnPictureSavedListener mListener;
        private final Handler mHandler;

        public SaveTask(final Bitmap bitmap, final String folderName, final String fileName,
                        final OnPictureSavedListener listener) {
            mBitmap = bitmap;
            mFolderName = folderName;
            mFileName = fileName;
            mListener = listener;
            mHandler = new Handler();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            Bitmap result = getBitmapWithFilterApplied(mBitmap);
            analogjapan1_saveImage(mFolderName, mFileName, result);
            return null;
        }

        private void analogjapan1_saveImage(final String folderName, final String fileName, final Bitmap image) {
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, folderName + "/" + fileName);
            try {
                file.getParentFile().mkdirs();
                image.compress(CompressFormat.JPEG, 80, new FileOutputStream(file));
                MediaScannerConnection.scanFile(mContext,
                        new String[] {
                                file.toString()
                        }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(final String path, final Uri uri) {
                                if (mListener != null) {
                                    mHandler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            mListener.onPictureSaved(uri);
                                        }
                                    });
                                }
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnPictureSavedListener {
        void onPictureSaved(Uri uri);
    }

    private class LoadImageUriTask extends LoadImageTask {

        private final Uri mUri;

        public LoadImageUriTask(analog3_GPUImage filter2GpuImage, Uri uri) {
            super(filter2GpuImage);
            mUri = uri;
        }

        @Override
        protected Bitmap decode(BitmapFactory.Options options) {
            try {
                InputStream inputStream;
                if (mUri.getScheme().startsWith("http") || mUri.getScheme().startsWith("https")) {
                    inputStream = new URL(mUri.toString()).openStream();
                } else {
                    inputStream = mContext.getContentResolver().openInputStream(mUri);
                }
                return BitmapFactory.decodeStream(inputStream, null, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected int getImageOrientation() throws IOException {
            Cursor cursor = mContext.getContentResolver().query(mUri,
                    new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

            if (cursor == null || cursor.getCount() != 1) {
                return 0;
            }

            cursor.moveToFirst();
            int orientation = cursor.getInt(0);
            cursor.close();
            return orientation;
        }
    }

    private class LoadImageFileTask extends LoadImageTask {

        private final File mImageFile;

        public LoadImageFileTask(analog3_GPUImage filter2GpuImage, File file) {
            super(filter2GpuImage);
            mImageFile = file;
        }

        @Override
        protected Bitmap decode(BitmapFactory.Options options) {
            return BitmapFactory.decodeFile(mImageFile.getAbsolutePath(), options);
        }

        @Override
        protected int getImageOrientation() throws IOException {
            ExifInterface exif = new ExifInterface(mImageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return 0;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        }
    }

    private abstract class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {

        private final analog3_GPUImage mFilter2GPUImage;
        private int mOutputWidth;
        private int mOutputHeight;

        @SuppressWarnings("deprecation")
        public LoadImageTask(final analog3_GPUImage filter2GpuImage) {
            mFilter2GPUImage = filter2GpuImage;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (mRenderer != null && mRenderer.getFrameWidth() == 0) {
                try {
                    synchronized (mRenderer.mSurfaceChangedWaiter) {
                        mRenderer.mSurfaceChangedWaiter.wait(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mOutputWidth = getOutputWidth();
            mOutputHeight = getOutputHeight();
            return loadResizedImage();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mFilter2GPUImage.deleteImage();
            mFilter2GPUImage.setImage(bitmap);
        }

        protected abstract Bitmap decode(BitmapFactory.Options options);

        private Bitmap loadResizedImage() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            decode(options);
            int scale = 1;
            while (checkSize(options.outWidth / scale > mOutputWidth, options.outHeight / scale > mOutputHeight)) {
                scale++;
            }

            scale--;
            if (scale < 1) {
                scale = 1;
            }
            options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inTempStorage = new byte[32 * 1024];
            Bitmap bitmap = decode(options);
            if (bitmap == null) {
                return null;
            }
            bitmap = rotateImage(bitmap);
            bitmap = scaleBitmap(bitmap);
            return bitmap;
        }

        private Bitmap scaleBitmap(Bitmap bitmap) {
            // resize to desired dimensions
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] newSize = getScaleSize(width, height);
            Bitmap workBitmap = Bitmap.createScaledBitmap(bitmap, newSize[0], newSize[1], true);
            if (workBitmap != bitmap) {
                bitmap.recycle();
                bitmap = workBitmap;
                System.gc();
            }

            if (mScaleType == ScaleType.CENTER_CROP) {
                // Crop it
                int diffWidth = newSize[0] - mOutputWidth;
                int diffHeight = newSize[1] - mOutputHeight;
                workBitmap = Bitmap.createBitmap(bitmap, diffWidth / 2, diffHeight / 2,
                        newSize[0] - diffWidth, newSize[1] - diffHeight);
                if (workBitmap != bitmap) {
                    bitmap.recycle();
                    bitmap = workBitmap;
                }
            }

            return bitmap;
        }

        private int[] getScaleSize(int width, int height) {
            float newWidth;
            float newHeight;

            float withRatio = (float) width / mOutputWidth;
            float heightRatio = (float) height / mOutputHeight;

            boolean adjustWidth = mScaleType == ScaleType.CENTER_CROP
                    ? withRatio > heightRatio : withRatio < heightRatio;

            if (adjustWidth) {
                newHeight = mOutputHeight;
                newWidth = (newHeight / height) * width;
            } else {
                newWidth = mOutputWidth;
                newHeight = (newWidth / width) * height;
            }
            return new int[]{Math.round(newWidth), Math.round(newHeight)};
        }

        private boolean checkSize(boolean widthBigger, boolean heightBigger) {
            if (mScaleType == ScaleType.CENTER_CROP) {
                return widthBigger && heightBigger;
            } else {
                return widthBigger || heightBigger;
            }
        }

        private Bitmap rotateImage(final Bitmap bitmap) {
            if (bitmap == null) {
                return null;
            }
            Bitmap rotatedBitmap = bitmap;
            try {
                int orientation = getImageOrientation();
                if (orientation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);
                    rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rotatedBitmap;
        }

        protected abstract int getImageOrientation() throws IOException;
    }

    public interface ResponseListener<T> {
        void response(T item);
    }

    public enum ScaleType { CENTER_INSIDE, CENTER_CROP }
}

