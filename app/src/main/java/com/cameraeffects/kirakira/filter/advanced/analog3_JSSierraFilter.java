package com.cameraeffects.kirakira.filter.advanced;


import android.content.Context;
import android.opengl.GLES20;

import com.cameraeffects.kirakira.R;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImageFilter;
import com.cameraeffects.kirakira.filter.base.analog3_OpenGlUtils;
import com.cameraeffects.kirakira.filter.helper.analog3_FilterContext;


public class analog3_JSSierraFilter extends analog3_GPUImageFilter {
    private int[] inputTextureHandles = {-1,-1,-1};
    private int[] inputTextureUniformLocations = {-1,-1,-1};
    private int mGLStrengthLocation;
    private Context context;

    public analog3_JSSierraFilter(){
        super(NO_FILTER_VERTEX_SHADER, analog3_OpenGlUtils.readShaderFromRawResource(R.raw.sierra));
        this.context = analog3_FilterContext.context;
    }

    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(inputTextureHandles.length, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++)
            inputTextureHandles[i] = -1;
    }

    protected void onDrawArraysAfter(){
        for(int i = 0; i < inputTextureHandles.length
                && inputTextureHandles[i] != analog3_OpenGlUtils.NO_TEXTURE; i++){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        }
    }

    protected void onDrawArraysPre(){
        for(int i = 0; i < inputTextureHandles.length
                && inputTextureHandles[i] != analog3_OpenGlUtils.NO_TEXTURE; i++){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
            GLES20.glUniform1i(inputTextureUniformLocations[i], (i+3));
        }
    }

    public void onInit(){
        super.onInit();
        for(int i = 0; i < inputTextureUniformLocations.length; i++)
            inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture"+(2+i));
        mGLStrengthLocation = GLES20.glGetUniformLocation(mGLProgId,
                "strength");
    }

    public void onInitialized(){
        super.onInitialized();
        setFloat(mGLStrengthLocation, 1.0f);
        runOnDraw(new Runnable(){
            public void run(){
                inputTextureHandles[0] = analog3_OpenGlUtils.loadTexture(context, "filter/sierravignette.png");
                inputTextureHandles[1] = analog3_OpenGlUtils.loadTexture(context, "filter/overlaymap.png");
                inputTextureHandles[2] = analog3_OpenGlUtils.loadTexture(context, "filter/sierramap.png");
            }
        });
    }
}
