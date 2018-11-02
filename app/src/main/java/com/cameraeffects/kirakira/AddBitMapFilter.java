package com.cameraeffects.kirakira;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;


import com.cameraeffects.kirakira.data.FilterItem;
import com.cameraeffects.kirakira.filter.FilterTypeHelper;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImage;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImageFilter;
import com.cameraeffects.kirakira.filter.helper.analog3_MagicFilterFactory;
import com.cameraeffects.kirakira.filter.helper.analog3_MagicFilterType;

import java.util.ArrayList;

public class AddBitMapFilter extends AsyncTask<Bitmap, String, ArrayList<FilterItem>> {
    private OnListenerFilter onListenerFilter;

    public AddBitMapFilter(OnListenerFilter onListenerFilter) {
        this.onListenerFilter = onListenerFilter;
    }

    @Override
    protected ArrayList<FilterItem> doInBackground(Bitmap... params) {
        ArrayList<FilterItem> arrayList = new ArrayList<>();
        Context context = AppController.getContext();
        FilterItem di = new FilterItem(params[0], analog3_MagicFilterType.NONE, context.getResources().getString(FilterTypeHelper.FilterType2Name(analog3_MagicFilterType.NONE)));
        arrayList.add(di);
        analog3_MagicFilterType[] types = FilterTypeHelper.analog3_types;
        for (int k = 1; k < types.length; k++) {
            analog3_MagicFilterType type = types[k];
            FilterItem analogjapan1FilterItem = new FilterItem(type);
            analog3_GPUImage analog3GpuImagess = new analog3_GPUImage(AppController.getContext());
            analog3GpuImagess.setImage(params[0]);
            analog3_GPUImageFilter analog3GpuImageFilter = analog3_MagicFilterFactory.initFilters(type, AppController.getContext());
            analog3GpuImagess.setFilter(analog3GpuImageFilter);
            Bitmap bitmapFilter = analog3GpuImagess.getBitmapWithFilterApplied();
            analog3GpuImagess.deleteImage();
            analogjapan1FilterItem.setBitmap(bitmapFilter);
            analogjapan1FilterItem.setName(AppController.getContext().getResources().getString(FilterTypeHelper.FilterType2Name(type)));
            arrayList.add(analogjapan1FilterItem);
        }
        return arrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<FilterItem> bitmap) {
        super.onPostExecute(bitmap);
        onListenerFilter.onSuccess(bitmap);
    }

    public interface OnListenerFilter {
        void onSuccess(ArrayList<FilterItem> arrayList);
    }
}
