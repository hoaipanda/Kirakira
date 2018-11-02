package com.seu.magicfilter.filter.advanced;

import android.annotation.SuppressLint;
import android.opengl.GLES20;

import com.seu.magicfilter.filter.base.GPUImageFilter;
import com.seu.magicfilter.filter.base.GPUImageRenderer;
import com.seu.magicfilter.filter.base.Rotation;
import com.seu.magicfilter.filter.base.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by MSI on 8/1/2017.
 */
//GPUImageRenderer.CUBE
public class GPUImageFilterGroup extends GPUImageFilter {

    protected List<GPUImageFilter> mFilters;
    private int[] mFrameBufferTextures;
    private int[] mFrameBuffers;
    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private final FloatBuffer mGLTextureFlipBuffer;
    protected List<GPUImageFilter> mMergedFilters;

    public GPUImageFilterGroup() {
        this(null);
    }

    public GPUImageFilterGroup(List<GPUImageFilter> list) {
        this.mFilters = list;
        if (this.mFilters == null) {
            this.mFilters = new ArrayList();
        } else {
            updateMergedFilters();
        }
        this.mGLCubeBuffer = ByteBuffer.allocateDirect(GPUImageRenderer.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mGLCubeBuffer.put(GPUImageRenderer.CUBE).position(0);
        this.mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        float[] rotation = TextureRotationUtil.getRotation(Rotation.NORMAL, false, true);
        this.mGLTextureFlipBuffer = ByteBuffer.allocateDirect(rotation.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mGLTextureFlipBuffer.put(rotation).position(0);
    }

    public void addFilter(GPUImageFilter gPUImageFilter) {
        if (gPUImageFilter != null) {
            this.mFilters.add(gPUImageFilter);
            updateMergedFilters();
        }
    }

    public void removeFilter(GPUImageFilter gPUImageFilter) {
        if (gPUImageFilter != null) {
            this.mFilters.remove(gPUImageFilter);
            updateMergedFilters();
        }
    }

    public void onInit() {
        super.onInit();
        for (GPUImageFilter init : this.mFilters) {
            init.init();
        }
    }

    public void onDestroy() {
        destroyFramebuffers();
        for (GPUImageFilter destroy : this.mFilters) {
            destroy.destroy();
        }
        super.onDestroy();
    }

    private void destroyFramebuffers() {
        if (this.mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(this.mFrameBufferTextures.length, this.mFrameBufferTextures, 0);
            this.mFrameBufferTextures = null;
        }
        if (this.mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(this.mFrameBuffers.length, this.mFrameBuffers, 0);
            this.mFrameBuffers = null;
        }
    }

    public void onOutputSizeChanged(int i, int i2) {
        super.onOutputSizeChanged(i, i2);
        if (this.mFrameBuffers != null) {
            destroyFramebuffers();
        }
        int size = this.mFilters.size();
        for (int i3 = 0; i3 < size; i3++) {
            ((GPUImageFilter) this.mFilters.get(i3)).onOutputSizeChanged(i, i2);
        }
        if (this.mMergedFilters != null && this.mMergedFilters.size() > 0) {
            int size2 = this.mMergedFilters.size();
            this.mFrameBuffers = new int[(size2 - 1)];
            this.mFrameBufferTextures = new int[(size2 - 1)];
            for (int i4 = 0; i4 < size2 - 1; i4++) {
                GLES20.glGenFramebuffers(1, this.mFrameBuffers, i4);
                GLES20.glGenTextures(1, this.mFrameBufferTextures, i4);
                GLES20.glBindTexture(3553, this.mFrameBufferTextures[i4]);
                GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, null);
                GLES20.glTexParameterf(3553, 10240, 9729.0f);
                GLES20.glTexParameterf(3553, 10241, 9729.0f);
                GLES20.glTexParameterf(3553, 10242, 33071.0f);
                GLES20.glTexParameterf(3553, 10243, 33071.0f);
                GLES20.glBindFramebuffer(36160, this.mFrameBuffers[i4]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.mFrameBufferTextures[i4], 0);
                GLES20.glBindTexture(3553, 0);
                GLES20.glBindFramebuffer(36160, 0);
            }
        }
    }

    @SuppressLint({"WrongCall"})
    public void onDraw(int i, FloatBuffer floatBuffer, FloatBuffer floatBuffer2) {
        runPendingOnDrawTasks();
        if (isInitialized() && this.mFrameBuffers != null && this.mFrameBufferTextures != null && this.mMergedFilters != null) {
            int size = this.mMergedFilters.size();
            int i2 = 0;
            int i3 = i;
            while (i2 < size) {
                boolean z;
                int i4;
                GPUImageFilter gPUImageFilter = (GPUImageFilter) this.mMergedFilters.get(i2);
                if (i2 < size - 1) {
                    z = true;
                } else {
                    z = false;
                }
                if (z) {
                    GLES20.glBindFramebuffer(36160, this.mFrameBuffers[i2]);
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                }
                if (i2 == 0) {
                    gPUImageFilter.onDraw(i3, floatBuffer, floatBuffer2);
                } else {
                    gPUImageFilter.onDraw(i3, this.mGLCubeBuffer, this.mGLTextureFlipBuffer);
                }
                if (z) {
                    GLES20.glBindFramebuffer(36160, 0);
                    i4 = this.mFrameBufferTextures[i2];
                } else {
                    i4 = i3;
                }
                i2++;
                i3 = i4;
            }
        }
    }

    public List<GPUImageFilter> getFilters() {
        return this.mFilters;
    }

    public int getSize() {
        if (mFilters != null) {
            return mFilters.size();
        } else {
            return 0;
        }
    }

    public List<GPUImageFilter> getMergedFilters() {
        return this.mMergedFilters;
    }

    public void updateMergedFilters() {
        if (this.mFilters != null) {
            if (this.mMergedFilters == null) {
                this.mMergedFilters = new ArrayList();
            } else {
                this.mMergedFilters.clear();
            }
            for (GPUImageFilter gPUImageFilter : this.mFilters) {
                if (gPUImageFilter instanceof GPUImageFilterGroup) {
                    ((GPUImageFilterGroup) gPUImageFilter).updateMergedFilters();
                    Collection mergedFilters = ((GPUImageFilterGroup) gPUImageFilter).getMergedFilters();
                    if (!(mergedFilters == null || mergedFilters.isEmpty())) {
                        this.mMergedFilters.addAll(mergedFilters);
                    }
                } else {
                    this.mMergedFilters.add(gPUImageFilter);
                }
            }
        }
    }

//    public void setRotation(Rotation rotation, boolean z, boolean z2) {
//        super.setRotation(rotation, z, z2);
//        if (this.mMergedFilters != null && this.mMergedFilters.size() > 0) {
//            int size = this.mMergedFilters.size();
//            for (int i = 0; i < size; i++) {
//                GPUImageFilter gPUImageFilter = (GPUImageFilter) this.mMergedFilters.get(i);
//                if (gPUImageFilter != null) {
//                    gPUImageFilter.setRotation(rotation, z, z2);
//                }
//            }
//        }
//    }
//
//    public void setFlipHorizontal(boolean z) {
//        super.setFlipHorizontal(z);
//        if (this.mMergedFilters != null && this.mMergedFilters.size() > 0) {
//            int size = this.mMergedFilters.size();
//            for (int i = 0; i < size; i++) {
//                GPUImageFilter gPUImageFilter = (GPUImageFilter) this.mMergedFilters.get(i);
//                if (gPUImageFilter != null) {
//                    gPUImageFilter.setFlipHorizontal(z);
//                }
//            }
//        }
//    }
//
//    public void setFlipVertical(boolean z) {
//        super.setFlipVertical(z);
//        if (this.mMergedFilters != null && this.mMergedFilters.size() > 0) {
//            int size = this.mMergedFilters.size();
//            for (int i = 0; i < size; i++) {
//                GPUImageFilter gPUImageFilter = (GPUImageFilter) this.mMergedFilters.get(i);
//                if (gPUImageFilter != null) {
//                    gPUImageFilter.setFlipVertical(z);
//                }
//            }
//        }
//    }
//
//    public void setRotation(Rotation rotation) {
//        super.setRotation(rotation);
//        if (this.mMergedFilters != null && this.mMergedFilters.size() > 0) {
//            int size = this.mMergedFilters.size();
//            for (int i = 0; i < size; i++) {
//                GPUImageFilter gPUImageFilter = (GPUImageFilter) this.mMergedFilters.get(i);
//                if (gPUImageFilter != null) {
//                    gPUImageFilter.setRotation(rotation);
//                }
//            }
//        }
//    }

    public void runOnDraw(Runnable runnable) {
        if (this.mMergedFilters != null && this.mMergedFilters.size() > 0) {
            ((GPUImageFilter) this.mMergedFilters.get(0)).runOnDraw(runnable);
        }
    }
}

