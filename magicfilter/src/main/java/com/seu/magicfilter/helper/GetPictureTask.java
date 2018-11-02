package com.seu.magicfilter.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;

public class GetPictureTask extends AsyncTask<Bitmap, Integer, Bitmap> {

    private OnGetListener onGetListener;
    private File file;
    private Context context;
    private ProgressDialog dialog;
    private boolean isImageSquare = false; // anh vuong

    public GetPictureTask(OnGetListener listener) {
        this.onGetListener = listener;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap bitmap = Bitmap.createBitmap(params[0], 0, 0, params[0].getWidth(), params[0].getHeight());
        return bitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        dialog = new ProgressDialog(context);
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.show();
    }

    @Override
    protected void onPostExecute(final Bitmap result) {
//        dialog.dismiss();
//		if (result != null)
//			MediaScannerConnection.scanFile(MagicParams.context,
//					new String[]{result}, null,
//					new MediaScannerConnection.OnScanCompletedListener() {
//						@Override
//						public void onScanCompleted(final String path, final Uri uri) {
//							if (onPictureSaveListener != null) {
//								onPictureSaveListener.onSaved(result);
//							}
//
//						}
//					});
        if (onGetListener != null) {
            onGetListener.onGeted(result);
        }
    }


    public interface OnGetListener {
        void onGeted(Bitmap bitmap);
    }
}