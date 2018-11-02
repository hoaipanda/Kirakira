package com.cameraeffects.kirakira.filter.advanced;


import android.opengl.GLES20;

import com.cameraeffects.kirakira.R;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImageFilter;
import com.cameraeffects.kirakira.filter.base.analog3_OpenGlUtils;


public class analog3_MagicSketchFilter extends analog3_GPUImageFilter {

    private int mSingleStepOffsetLocation;
    //0.0 - 1.0
    private int mStrengthLocation;

    public analog3_MagicSketchFilter() {
        super(NO_FILTER_VERTEX_SHADER, analog3_OpenGlUtils.readShaderFromRawResource(R.raw.sketch));
    }

    public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrengthLocation = GLES20.glGetUniformLocation(getProgram(), "strength");
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(mSingleStepOffsetLocation, new float[]{1.0f / w, 1.0f / h});
    }

    public void onInitialized() {
        super.onInitialized();
        setFloat(mStrengthLocation, 0.5f);
    }

    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
