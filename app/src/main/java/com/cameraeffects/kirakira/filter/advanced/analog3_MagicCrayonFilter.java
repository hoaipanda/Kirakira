package com.cameraeffects.kirakira.filter.advanced;


import android.opengl.GLES20;

import com.cameraeffects.kirakira.R;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImageFilter;
import com.cameraeffects.kirakira.filter.base.analog3_OpenGlUtils;


public class analog3_MagicCrayonFilter extends analog3_GPUImageFilter {

    private int mSingleStepOffsetLocation;
    //1.0 - 5.0
    private int mStrengthLocation;

    public analog3_MagicCrayonFilter(){
        super(NO_FILTER_VERTEX_SHADER, analog3_OpenGlUtils.readShaderFromRawResource(R.raw.crayon));
    }

    public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrengthLocation = GLES20.glGetUniformLocation(getProgram(), "strength");
        setFloat(mStrengthLocation, 2.0f);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onInitialized(){
        super.onInitialized();
        setFloat(mStrengthLocation, 0.5f);
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
    }

    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}

