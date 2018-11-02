package com.seu.magicfilter.utils;

import android.content.Context;
import android.os.Environment;

import com.seu.magicfilter.widget.base.MagicBaseView;

import java.util.Calendar;

/**
 * Created by why8222 on 2016/2/26.
 */
public class MagicParams {
    public static Context context;
    public static MagicBaseView magicBaseView;

    public static String videoPath = Environment.getExternalStorageDirectory().getPath();
    public static String videoName = Calendar.getInstance().getTimeInMillis() + ".mp4";

    public static int beautyLevel = 5;

    public MagicParams() {

    }
}
