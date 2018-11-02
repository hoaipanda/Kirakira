package com.cameraeffects.kirakira.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cameraeffects.kirakira.AddBitMapFilter;
import com.cameraeffects.kirakira.AppController;
import com.cameraeffects.kirakira.AppUtil;
import com.cameraeffects.kirakira.Contains;
import com.cameraeffects.kirakira.R;
import com.cameraeffects.kirakira.adapter.FilterItemAdapter;
import com.cameraeffects.kirakira.data.FilterItem;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImage;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImageFilter;
import com.cameraeffects.kirakira.filter.helper.analog3_MagicFilterFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.hardware.Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT;

public class MainActivity extends AppCompatActivity implements FilterItemAdapter.onListenerFilterItem {

    private final int TYPE_3S = 3, TYPE_5S = 5, TYPE_7S = 7;
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera camera = null;
    private boolean inPreview = false;
    private boolean isRolate = false;
    private ProgressDialog dialog;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private RelativeLayout.LayoutParams paramsSurface;

    private RelativeLayout lyRatio, lyFlash, lyMore, lyCamera, lyCountTime, main, lyDefault, lyHasFilter;
    private ImageView imTg, imTakePhoto, imTakePhotoFilter;
    private LinearLayout lyDetailMore, lyAlbum, lyFilter, lyTimer, lyTouch;

    private boolean isShowMore = false;

    private TextView tvPhoto, tvVideo;

    private boolean isTouch = false;
    private ImageView imTouch, imTimer;
    private TextView tvTouch, tvTimer, tvCountTimer, tvCount;
    private int stateTimer = 0;

    private RecyclerView rvFilter;
    private ArrayList<FilterItem> listFilter = new ArrayList<>();
    private FilterItemAdapter filterItemAdapter;

    private ImageView imBlend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupRecyclerViewFilter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        releaseCameraAndPreview();
        checkRolate(isRolate);

    }

    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private void initView() {
        preview = (SurfaceView) findViewById(R.id.surface);
        setUpCamera();

        lyCamera = findViewById(R.id.lyCamera);
        lyFlash = findViewById(R.id.lyFlash);
        lyMore = findViewById(R.id.lyMore);
        lyRatio = findViewById(R.id.lyRatio);
        lyTimer = findViewById(R.id.lyTimer);
        lyTouch = findViewById(R.id.lyTouch);

        imTg = findViewById(R.id.imTG);
        lyDetailMore = findViewById(R.id.lyDetailMore);
        lyFilter = findViewById(R.id.lyFilter);
        lyAlbum = findViewById(R.id.lyAlbum);
        imTakePhoto = findViewById(R.id.imTakePhoto);
        tvPhoto = findViewById(R.id.tvPhoto);
        tvVideo = findViewById(R.id.tvVideo);
        imTakePhotoFilter = findViewById(R.id.imTakePhotoFilter);

        lyRatio.setOnClickListener(lsClick);
        lyMore.setOnClickListener(lsClick);
        lyFlash.setOnClickListener(lsClick);
        lyCamera.setOnClickListener(lsClick);
        lyFilter.setOnClickListener(lsClick);
        lyAlbum.setOnClickListener(lsClick);
        imTakePhoto.setOnClickListener(lsClick);
        lyTimer.setOnClickListener(lsClick);
        lyTouch.setOnClickListener(lsClick);
        imTakePhotoFilter.setOnClickListener(lsClick);

        imTouch = findViewById(R.id.imTouch);
        tvTouch = findViewById(R.id.tvTouch);
        imTimer = findViewById(R.id.imTimer);
        tvTimer = findViewById(R.id.tvTimer);
        tvCountTimer = findViewById(R.id.tvCountTimer);
        lyCountTime = findViewById(R.id.lyCountTimer);
        tvCount = findViewById(R.id.tvCount);
        main = findViewById(R.id.main);

        main.setOnClickListener(lsClick);

        lyDefault = findViewById(R.id.lyDefault);
        lyHasFilter = findViewById(R.id.lyHasFilter);
        rvFilter = findViewById(R.id.rvFilter);

        imBlend = findViewById(R.id.imBlend);

    }

    private void setupRecyclerViewFilter() {

        Bitmap bitmapResult = BitmapFactory.decodeResource(getResources(),
                R.drawable.thumd);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvFilter.setLayoutManager(linearLayoutManager);
        filterItemAdapter = new FilterItemAdapter(this, this, listFilter);
        rvFilter.setAdapter(filterItemAdapter);
        AddBitMapFilter addBitMapFilter = new AddBitMapFilter(new AddBitMapFilter.OnListenerFilter() {
            @Override
            public void onSuccess(ArrayList<FilterItem> arrayList) {
                updateRecyclerViewFilter(arrayList);
            }
        });
        float aspectRatio = bitmapResult.getWidth() / (float) bitmapResult.getHeight();
        int width = 200;
        int height = Math.round(width / aspectRatio);
        Bitmap yourSelectedImage = Bitmap.createScaledBitmap(bitmapResult, width, height, false);
        addBitMapFilter.execute(Bitmap.createScaledBitmap(yourSelectedImage, width, height, false));
    }

    private void updateRecyclerViewFilter(ArrayList<FilterItem> arrayList) {
        listFilter.clear();
        listFilter.addAll(arrayList);
        filterItemAdapter.notifyDataSetChanged();
    }


    private void setUpCamera() {
        paramsSurface = (RelativeLayout.LayoutParams) preview.getLayoutParams();
        paramsSurface.width = getResources().getDisplayMetrics().widthPixels;
        paramsSurface.height = paramsSurface.width * 4 / 3;
        preview.requestLayout();

        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        previewHolder.setFixedSize(getWindow().getWindowManager()
                .getDefaultDisplay().getWidth(), getWindow().getWindowManager()
                .getDefaultDisplay().getHeight());
    }

    private View.OnClickListener lsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main:
                    if (isTouch) {
                        takePhoto();
                    }
                    break;
                case R.id.lyCamera:
                    if (inPreview) {
                        camera.stopPreview();
                    }
                    isRolate = !isRolate;
                    checkRolate(isRolate);
                    break;
                case R.id.lyFlash:
                    break;
                case R.id.lyMore:
                    if (isShowMore) {
                        isShowMore = false;
                        imTg.setVisibility(View.GONE);
                        lyDetailMore.setVisibility(View.GONE);
                    } else {
                        isShowMore = true;
                        imTg.setVisibility(View.VISIBLE);
                        lyDetailMore.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.lyRatio:
                    break;
                case R.id.imTakePhoto:
                    takePhoto();
                    break;
                case R.id.lyAlbum:
                    startActivity(new Intent(MainActivity.this, AlbumActivity.class));
                    break;
                case R.id.lyFilter:
                    lyDefault.setVisibility(View.GONE);
                    lyHasFilter.setVisibility(View.VISIBLE);
                    break;
                case R.id.lyTimer:
                    switch (stateTimer) {
                        case 0:
                            stateTimer = TYPE_3S;
                            break;
                        case 3:
                            stateTimer = TYPE_5S;
                            break;
                        case 5:
                            stateTimer = TYPE_7S;
                            break;
                        case 7:
                            stateTimer = 0;
                            break;

                    }

                    if (stateTimer == 0) {
                        tvTimer.setTextColor(Color.WHITE);
                        imTimer.setColorFilter(Color.WHITE);
                        tvCountTimer.setVisibility(View.GONE);
                    } else {
                        tvTimer.setTextColor(Color.BLUE);
                        imTimer.setColorFilter(Color.BLUE);
                        tvCountTimer.setText(stateTimer + "");
                        tvCountTimer.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.lyTouch:
                    if (isTouch) {
                        isTouch = false;
                        imTouch.setColorFilter(Color.WHITE);
                        tvTouch.setTextColor(Color.WHITE);
                    } else {
                        isTouch = true;
                        imTouch.setColorFilter(Color.BLUE);
                        tvTouch.setTextColor(Color.BLUE);
                    }
                    break;
                case R.id.imTakePhotoFilter:
                    takePhoto();
                    break;
            }
        }
    };

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @SuppressLint("LongLogTag")
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback",
                        "Exception in setPreviewDisplay()", t);
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        }

        public void surfaceChanged(SurfaceHolder holder,
                                   int format, int width,
                                   int height) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = getBestPreviewSize(width, height,
                    parameters);

            if (size != null) {
                setDefaultParameters(camera, parameters);
                camera.startPreview();
                inPreview = true;
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    private static void setDefaultParameters(Camera camera, Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size result = null;
        List<Camera.Size> picsizes = parameters.getSupportedPictureSizes();
        Camera.Size picresult = null;

        for (int i = 0; i < sizes.size(); i++) {
            result = (Camera.Size) sizes.get(i);
            if ((float) result.width / result.height == (float) (4 / (float) 3) && result.width < 2048) {
                parameters.setPreviewSize(result.width, result.height);
                break;
            }
        }

        for (int i = 0; i < picsizes.size(); i++) {
            picresult = (Camera.Size) picsizes.get(i);
            if ((float) picresult.width / picresult.height == (float) (4 / (float) 3) && picresult.width < 2048) {
                parameters.setPictureSize(picresult.width, picresult.height);
                break;
            }
        }

        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        camera.setParameters(parameters);
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    float x = (float) size.width / (float) size.height;
                    if (x == (4 / 3)) {
                        return size;
                    }
                }
            }
        }
        return (result);
    }

    private CountDownTimer countDownTimer;

    private void takePhoto() {
        if (stateTimer != 0) {
            lyCountTime.setVisibility(View.VISIBLE);
            tvCount.setText(stateTimer + "");
            countDownTimer = new CountDownTimer((stateTimer + 1) * 1000, 1000) {

                @Override
                public void onTick(long l) {
                    tvCount.setText(l / 1000 + "");
                }

                @Override
                public void onFinish() {
                    camera.takePicture(null, null, photoCallback);
                    lyCountTime.setVisibility(View.GONE);
                }
            }.start();
        } else {
            camera.takePicture(null, null, photoCallback);
        }


    }


    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }


    }

    Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] data, final Camera camera) {
            dialog = ProgressDialog.show(MainActivity.this, "", "Saving Photo");
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                    }
                    onPictureTake(data);
                }
            }.start();
        }


    };


    public void onPictureTake(byte[] data) {
        Bitmap bmImage1 = AppUtil.decodeByteArray(data, AppUtil.getCameraDisplayOrientation(currentCameraId, MainActivity.this), currentCameraId);
        saveImageBitmap(bmImage1);
        dialog.dismiss();
    }

    private void checkRolate(boolean isRolate) {
        releaseCameraAndPreview();
        if (isRolate) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        AppUtil.setCameraDisplayOrientation(this, currentCameraId, camera);
        try {
            camera.setPreviewDisplay(previewHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }


    public void saveImageBitmap(Bitmap bitmap) {
        String root = getCacheDir().getAbsolutePath();
        File myDir = new File(root + "/Kirakira");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = Calendar.getInstance().getTimeInMillis() + "";
        File file = new File(myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            checkRolate(isRolate);

            Intent intent = new Intent(MainActivity.this, DetailImageActivity.class);
            intent.putExtra(Contains.PATH_IMAGE_CAMERA, fname);
            intent.putExtra(Contains.FILTER, mFilter);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FilterItem mFilter = null;

    @Override
    public void onClickItem(int position, FilterItem filterItem) {
        mFilter = filterItem;
        Bitmap bitmapResult = BitmapFactory.decodeResource(getResources(), R.drawable.none);
        analog3_GPUImage analog3GpuImagess = new analog3_GPUImage(AppController.getContext());
        analog3GpuImagess.setImage(bitmapResult);
        analog3_GPUImageFilter analog3GpuImageFilter = analog3_MagicFilterFactory.initFilters(filterItem.getType(), AppController.getContext());
        analog3GpuImagess.setFilter(analog3GpuImageFilter);

        Bitmap bm = analog3GpuImagess.getBitmapWithFilterApplied();
        imBlend.setImageBitmap(bm);
    }

    @Override
    public void onClick2Item(int position) {

    }
}
