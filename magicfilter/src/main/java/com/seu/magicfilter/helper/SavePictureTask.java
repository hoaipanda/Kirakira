package com.seu.magicfilter.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SavePictureTask extends AsyncTask<Bitmap, Integer, String> {

	private OnPictureSaveListener onPictureSaveListener;
	private File file;
	private Context context;
	private ProgressDialog dialog;
	private boolean isImageSquare = false; // anh vuong

	public SavePictureTask(Context context, File file, boolean isSquare, OnPictureSaveListener listener) {
		this.onPictureSaveListener = listener;
		this.file = file;
		this.context = context;
		this.isImageSquare = isSquare;
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
	protected void onPostExecute(final String result) {
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
		if (onPictureSaveListener != null) {
			onPictureSaveListener.onSaved(result);
		}
	}

	@Override
	protected String doInBackground(Bitmap... params) {
		if (file == null)
			return null;
		return saveBitmap(cropBitmap(params[0]));
	}

	private String saveBitmap(Bitmap bitmap) {
		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			refreshGallery(context, file);
			bitmap.recycle();
			return file.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Bitmap cropBitmap(Bitmap bitmap) {
		if (isImageSquare) {
			if (bitmap.getHeight() > bitmap.getWidth()) {
				int top = (bitmap.getHeight() - bitmap.getWidth()) / 2;
				int bot = top + bitmap.getWidth();
				Bitmap square = Bitmap.createBitmap(bitmap, 0, top, bitmap.getWidth(), bot);
				return square;
			} else {
				int left = (bitmap.getWidth() - bitmap.getHeight()) / 2;
				int right = left + bitmap.getHeight();
				Bitmap square = Bitmap.createBitmap(bitmap, left, 0, right, bitmap.getHeight());
				return square;
			}
		}
		return bitmap;
	}

	private void refreshGallery(Context context, File file) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		context.sendBroadcast(mediaScanIntent);
	}

	public interface OnPictureSaveListener {
		void onSaved(String result);
	}
}