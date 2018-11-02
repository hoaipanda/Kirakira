package com.cameraeffects.kirakira;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.view.Surface;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AppUtil {

    public static ArrayList<String> getListPicture(Context ctx) {
        ArrayList<String> list = new ArrayList<>();
        File file = new File(ctx.getCacheDir(), "Kirakira");
        if (!file.exists())
            return list;
        File[] listFile = file.listFiles();
        for (int i = 0; i < listFile.length; i++) {
            list.add(listFile[i].getAbsolutePath());
        }

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        return list;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public static Bitmap decodeByteArray(byte[] data, float rotationDegree, int currentID) {
        try {
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (rotationDegree != 0) {
                bm = createRotatedBitmap(bm, rotationDegree, currentID);
            }
            return bm;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap createRotatedBitmap(Bitmap bm, float rotation, int currentID) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation, bm.getWidth() / 2, bm.getHeight() / 2);
        if (currentID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            matrix.preScale(1.0f, -1.0f);
        }

        try {
            Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return result;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public static int getCameraDisplayOrientation(int cameraId, Activity activity) {
        int rotation = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + rotation) % 360;
            result = (360 - result) % 360 + 180;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - rotation + 360) % 360;
        }
        return result;
    }

    public static void deleteCache(ArrayList<String> listImage) {
        if (listImage.size() > 0)
            for (int i = 0; i < listImage.size(); i++) {
                File file = new File(listImage.get(i));
                file.delete();
            }

    }



    public static void createDirectoryAndSaveFile(Context context, Bitmap imageToSave) {
        long millis = System.currentTimeMillis();
        File direct = new File(Environment.getExternalStorageDirectory() + "/Kirakira/");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Kirakira/");
            wallpaperDirectory.mkdirs();
        }
        File file = new File(new File("/sdcard/Kirakira/"), "IMG" + millis + ".jpg");
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            refreshGallery(context, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void refreshGallery(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaScanIntent);
    }
}
