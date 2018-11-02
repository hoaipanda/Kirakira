package com.cameraeffects.kirakira.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cameraeffects.kirakira.AppController;
import com.cameraeffects.kirakira.AppUtil;
import com.cameraeffects.kirakira.Contains;
import com.cameraeffects.kirakira.R;
import com.cameraeffects.kirakira.data.FilterItem;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImage;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImageFilter;
import com.cameraeffects.kirakira.filter.helper.analog3_MagicFilterFactory;

import java.io.File;

public class DetailImageActivity extends AppCompatActivity {
    private ImageView imReview, imCancel, imDone;
    private Bitmap bm;
    private String filename = "";
    private FilterItem mFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);
        imReview = findViewById(R.id.imReview);
        imCancel = findViewById(R.id.imCancel);
        imDone = findViewById(R.id.imDone);

        imDone.setOnClickListener(lsClick);
        imCancel.setOnClickListener(lsClick);

        Intent intent = getIntent();
        filename = intent.getStringExtra(Contains.PATH_IMAGE_CAMERA);
        mFilter = (FilterItem) intent.getSerializableExtra(Contains.FILTER);
        bm = BitmapFactory.decodeFile(String.valueOf(getCacheDir().getAbsolutePath() + "/Kirakira/" + filename));
        if (mFilter != null) {
            analog3_GPUImage analog3GpuImagess = new analog3_GPUImage(AppController.getContext());
            analog3GpuImagess.setImage(bm);
            analog3_GPUImageFilter analog3GpuImageFilter = analog3_MagicFilterFactory.initFilters(mFilter.getType(), AppController.getContext());
            analog3GpuImagess.setFilter(analog3GpuImageFilter);

            Bitmap bm2 = analog3GpuImagess.getBitmapWithFilterApplied();
            imReview.setImageBitmap(bm2);
        } else {
            imReview.setImageBitmap(bm);
        }


    }

    private View.OnClickListener lsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imCancel:
                    finish();
                    break;
                case R.id.imDone:
                    AppUtil.createDirectoryAndSaveFile(DetailImageActivity.this, bm);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        File file = new File(getCacheDir().getAbsolutePath() + "/Kirakira/" + filename);
        file.delete();
    }
}
