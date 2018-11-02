package com.cameraeffects.kirakira.filter.advanced.analog3_adjust;

import android.opengl.GLES20;

import com.cameraeffects.kirakira.filter.base.analog3_GPUImageFilter;


public class analog3_GPUImageContrastFilter extends analog3_GPUImageFilter {
    public static final String CONTRAST_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float contrast;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);\n" +
            " }";

    private int mContrastLocation;
    private float mContrast;

    public analog3_GPUImageContrastFilter() {
        this(1.2f);
    }

    public analog3_GPUImageContrastFilter(float contrast) {
        super(NO_FILTER_VERTEX_SHADER, CONTRAST_FRAGMENT_SHADER);
        mContrast = contrast;
    }

    @Override
    public void onInit() {
        super.onInit();
        mContrastLocation = GLES20.glGetUniformLocation(getProgram(), "contrast");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setContrast(mContrast);
    }

    public void setContrast(final float contrast) {
        mContrast = contrast;
        setFloat(mContrastLocation, mContrast);
    }
}

