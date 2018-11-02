package com.cameraeffects.kirakira;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MethodUtils {
    public static Bitmap analog3_getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static ArrayList<String> analog3_getAllFileAssets(Context context, String nameFolder) {
        ArrayList<String> listStrImage = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        String[] files = new String[0];
        try {
            files = assetManager.list(nameFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Arrays.asList(files).size(); i++) {
            listStrImage.add(Arrays.asList(files).get(i));
        }
        return listStrImage;
    }

    public static void analog3_shareApp(Context context, String text) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", context.getString(R.string.app_name));
        intent.putExtra("android.intent.extra.TEXT", text);
        context.startActivity(Intent.createChooser(intent, "Share App"));
    }

    public static void analog3_saveImage(final Context context, Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + context.getString(R.string.nameFolder));
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Saving...");
        dialog.show();
        String fname = Calendar.getInstance().getTime().getTime() + ".png";
        File file = new File(myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            MethodUtils.analog3_refreshGallery(context, file);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(context, (context.getString(R.string.saved)), Toast.LENGTH_SHORT).show();
                }
            }, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void analog3_refreshGallery(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaScanIntent);
    }

    public static int analog3_dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }
}
