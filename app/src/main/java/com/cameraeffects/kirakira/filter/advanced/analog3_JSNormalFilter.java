package com.cameraeffects.kirakira.filter.advanced;


import com.cameraeffects.kirakira.filter.base.analog3_GPUImageFilter;

public class analog3_JSNormalFilter extends analog3_GPUImageFilter {

    public static final String SHADER = "precision lowp float;\n" +
            " precision lowp float;\n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            "\n" +
            " void main()\n" +
            " {\n" +
            "     \n" +
            "     vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     \n" +
            "     gl_FragColor = vec4(texel, 1.0);\n" +
            " }";

    public analog3_JSNormalFilter() {
        super(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);//NO_FILTER_VERTEX_SHADER, SHADER);
    }

}

