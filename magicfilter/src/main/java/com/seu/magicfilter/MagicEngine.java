package com.seu.magicfilter;

import android.content.Context;

import com.seu.magicfilter.camera.utils.CameraEngine;
import com.seu.magicfilter.filter.base.GPUImageFilter;
import com.seu.magicfilter.filter.helper.FilterContext;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.GetPictureTask;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.widget.MagicCameraView;
import com.seu.magicfilter.widget.base.MagicBaseView;

/**
 * Created by why8222 on 2016/2/25.
 */
public class MagicEngine {
    private static MagicEngine magicEngine;


    private MagicEngine(Builder builder) {
    }

    public void setFilter(MagicFilterType type, Context context) {
        MagicParams.magicBaseView.setFilter(type, context);
    }

    public void setFilter(GPUImageFilter filter){
        MagicParams.magicBaseView.setFilter(filter);
    }

    //    public void savePicture(Context context, File file, boolean isSquare, SavePictureTask.OnPictureSaveListener listener) {
//        SavePictureTask savePictureTask = new SavePictureTask(context, file, isSquare, listener);
//        MagicParams.magicBaseView.savePicture(savePictureTask);
//    }
    public void savePicture(GetPictureTask.OnGetListener getListener) {
        GetPictureTask getPictureTask1 = new GetPictureTask(getListener);
        MagicParams.magicBaseView.savePicture(getPictureTask1);
    }

    public void startRecord() {
        if (MagicParams.magicBaseView instanceof MagicCameraView)
            ((MagicCameraView) MagicParams.magicBaseView).changeRecordingState(true);
    }

    public void stopRecord() {
        if (MagicParams.magicBaseView instanceof MagicCameraView)
            ((MagicCameraView) MagicParams.magicBaseView).changeRecordingState(false);
    }

    public void setBeautyLevel(int level) {
        if (MagicParams.magicBaseView instanceof MagicCameraView && MagicParams.beautyLevel != level) {
            MagicParams.beautyLevel = level;
            ((MagicCameraView) MagicParams.magicBaseView).onBeautyLevelChanged();
        }
    }

    public void switchCamera() {
        CameraEngine.switchCamera();
    }

    public void switchFlashlight(int mode) {
        CameraEngine.switchFlashlight(mode);
    }

    public int getMaxLightCamera() {
        return CameraEngine.getMaxLightCamera();
    }

    public void setLightCamera(int light) {
        CameraEngine.setLightCamera(light);
    }

    public static class Builder {

        public MagicEngine build(MagicBaseView magicBaseView, Context context) {
            context = magicBaseView.getContext();
            MagicParams.magicBaseView = magicBaseView;
            FilterContext.context = context;
            MagicParams.context = context;
            return new MagicEngine(this);
        }

        public Builder setVideoPath(String path) {
            MagicParams.videoPath = path;
            return this;
        }

        public Builder setVideoName(String name) {
            MagicParams.videoName = name;
            return this;
        }

    }
}
