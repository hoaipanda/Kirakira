package com.seu.magicfilter.helper;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.seu.magicfilter.filter.advanced.MagicBeautyFilter;
import com.seu.magicfilter.filter.base.GPUImage;
import com.seu.magicfilter.filter.base.GPUImageFilter;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.filter.helper.FilterContext;

public class AsynRender extends AsyncTask<Void, Void, Bitmap> {
    GPUImageFilter filter;
    Bitmap bitmapreal;
    RenderCompleted mRenderCompleted;
    Context appctx;
    MagicBeautyFilter magicBeautyFilter;

    public AsynRender(Context appctx, MagicBeautyFilter magicBeautyFilter, GPUImageFilter filter, Bitmap bitmap, RenderCompleted mRenderCompleted) {
        this.filter = filter;
        this.magicBeautyFilter = magicBeautyFilter;
        this.mRenderCompleted = mRenderCompleted;
        this.bitmapreal = bitmap;
        this.appctx = appctx;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        GPUImage gpuImagess = new GPUImage(appctx);
        gpuImagess.setImage(bitmapreal);
        gpuImagess.setFilter(magicBeautyFilter);
        Bitmap bmBeauty = gpuImagess.getBitmapWithFilterApplied();

        gpuImagess.deleteImage();
        gpuImagess.setImage(bmBeauty);
        gpuImagess.setFilter(filter);
        Bitmap chuannhuleduanImage = gpuImagess.getBitmapWithFilterApplied();
        gpuImagess.deleteImage();
        return chuannhuleduanImage;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (mRenderCompleted != null) {
            mRenderCompleted.Done(result);
        }
    }

    public interface RenderCompleted {
        void Done(Bitmap mbitmap);
    }


}
