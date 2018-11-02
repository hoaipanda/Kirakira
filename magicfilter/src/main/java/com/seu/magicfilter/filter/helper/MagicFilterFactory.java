package com.seu.magicfilter.filter.helper;


import android.content.Context;
import android.graphics.PointF;

import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.advanced.GPUImageVignetteFilter;
import com.seu.magicfilter.filter.advanced.JSAmaroFilter;
import com.seu.magicfilter.filter.advanced.JSAntiqueFilter;
import com.seu.magicfilter.filter.advanced.JSNormalFilter;
import com.seu.magicfilter.filter.advanced.JSSierraFilter;
import com.seu.magicfilter.filter.advanced.JSSunsetFilter;
import com.seu.magicfilter.filter.advanced.JSTenderFilter;
import com.seu.magicfilter.filter.advanced.JSToneCurved;
import com.seu.magicfilter.filter.advanced.MagicBlackCatFilter;
import com.seu.magicfilter.filter.advanced.MagicBrannanFilter;
import com.seu.magicfilter.filter.advanced.MagicBrooklynFilter;
import com.seu.magicfilter.filter.advanced.MagicCalmFilter;
import com.seu.magicfilter.filter.advanced.MagicCoolFilter;
import com.seu.magicfilter.filter.advanced.MagicCrayonFilter;
import com.seu.magicfilter.filter.advanced.MagicEarlyBirdFilter;
import com.seu.magicfilter.filter.advanced.MagicEmeraldFilter;
import com.seu.magicfilter.filter.advanced.MagicEvergreenFilter;
import com.seu.magicfilter.filter.advanced.MagicFairytaleFilter;
import com.seu.magicfilter.filter.advanced.MagicFreudFilter;
import com.seu.magicfilter.filter.advanced.MagicHealthyFilter;
import com.seu.magicfilter.filter.advanced.MagicHefeFilter;
import com.seu.magicfilter.filter.advanced.MagicHudsonFilter;
import com.seu.magicfilter.filter.advanced.MagicInkwellFilter;
import com.seu.magicfilter.filter.advanced.MagicKevinFilter;
import com.seu.magicfilter.filter.advanced.MagicLatteFilter;
import com.seu.magicfilter.filter.advanced.MagicN1977Filter;
import com.seu.magicfilter.filter.advanced.MagicNashvilleFilter;
import com.seu.magicfilter.filter.advanced.MagicNostalgiaFilter;
import com.seu.magicfilter.filter.advanced.MagicPixarFilter;
import com.seu.magicfilter.filter.advanced.MagicRiseFilter;
import com.seu.magicfilter.filter.advanced.MagicRomanceFilter;
import com.seu.magicfilter.filter.advanced.MagicSakuraFilter;
import com.seu.magicfilter.filter.advanced.MagicSketchFilter;
import com.seu.magicfilter.filter.advanced.MagicSkinWhitenFilter;
import com.seu.magicfilter.filter.advanced.MagicSunriseFilter;
import com.seu.magicfilter.filter.advanced.MagicSutroFilter;
import com.seu.magicfilter.filter.advanced.MagicSweetsFilter;
import com.seu.magicfilter.filter.advanced.MagicToasterFilter;
import com.seu.magicfilter.filter.advanced.MagicValenciaFilter;
import com.seu.magicfilter.filter.advanced.MagicWaldenFilter;
import com.seu.magicfilter.filter.advanced.MagicWarmFilter;
import com.seu.magicfilter.filter.advanced.MagicWhiteCatFilter;
import com.seu.magicfilter.filter.advanced.MagicXproIIFilter;
import com.seu.magicfilter.filter.advanced.adjust.GPUImageBrightnessFilter;
import com.seu.magicfilter.filter.advanced.adjust.GPUImageContrastFilter;
import com.seu.magicfilter.filter.advanced.adjust.GPUImageExposureFilter;
import com.seu.magicfilter.filter.advanced.adjust.GPUImageHueFilter;
import com.seu.magicfilter.filter.advanced.adjust.GPUImageSaturationFilter;
import com.seu.magicfilter.filter.advanced.adjust.GPUImageSharpenFilter;
import com.seu.magicfilter.filter.base.GPUImageFilter;

public class MagicFilterFactory {

    private static MagicFilterType filterType = MagicFilterType.NONE;

    public static GPUImageFilter initFilters(MagicFilterType type, Context appctx) {
        if (type == null) {
            return new JSNormalFilter(); // null;
        } else {
            filterType = type;
            switch (type) {
                case FREUD:
                    return new MagicFreudFilter();
                case SKETCH:
                    return new MagicSketchFilter();
                case CRAYON:
                    return new MagicCrayonFilter();
                case NOSTALGIA:
                    return new MagicNostalgiaFilter();
                case SAKURA:
                    return new MagicSakuraFilter();
                case SKINWHITEN:
                    return new MagicSkinWhitenFilter();
                case HEALTHY:
                    return new MagicHealthyFilter();
                case WHITECAT:
                    return new MagicWhiteCatFilter();
                case BLACKCAT:
                    return new MagicBlackCatFilter();
                case ROMANCE:
                    return new MagicRomanceFilter();
                case AMARO:
                    return new JSAmaroFilter();
                case WALDEN:
                    return new MagicWaldenFilter();
                case ANTIQUE:
                    return new JSAntiqueFilter();
                case CALM:
                    return new MagicCalmFilter();
                case BRANNAN:
                    return new MagicBrannanFilter();
                case BROOKLYN:
                    return new MagicBrooklynFilter();
                case EARLYBIRD:
                    return new MagicEarlyBirdFilter();
                case HEFE:
                    return new MagicHefeFilter();
                case HUDSON:
                    return new MagicHudsonFilter();
                case INKWELL:
                    return new MagicInkwellFilter();
                case KEVIN:
                    return new MagicKevinFilter();
                case N1977:
                    return new MagicN1977Filter();
                case NASHVILLE:
                    return new MagicNashvilleFilter();
                case PIXAR:
                    return new MagicPixarFilter();
                case RISE:
                    return new MagicRiseFilter();
                case SIERRA:
                    return new JSSierraFilter();
                case SUTRO:
                    return new MagicSutroFilter();
                case TOASTER2:
                    return new MagicToasterFilter();
                case VALENCIA:
                    return new MagicValenciaFilter();
                case XPROII:
                    return new MagicXproIIFilter();
                case EVERGREEN:
                    return new MagicEvergreenFilter();
                case COOL:
                    return new MagicCoolFilter();
                case EMERALD:
                    return new MagicEmeraldFilter();
                case LATTE:
                    return new MagicLatteFilter();
                case WARM:
                    return new MagicWarmFilter();
                case TENDER:
                    return new JSTenderFilter();
                case SWEETS:
                    return new MagicSweetsFilter();
                case FAIRYTALE:
                    return new MagicFairytaleFilter();
                case SUNRISE:
                    return new MagicSunriseFilter();
                case SUNSET:
                    return new JSSunsetFilter();
                case GOLLDENT:
                    JSToneCurved goldent = new JSToneCurved();
                    goldent.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.goldenhour));
                    return goldent;
                case LEMON:
                    JSToneCurved LEMON = new JSToneCurved();
                    LEMON.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.tonelemon));
                    return LEMON;
                case MYSTERY:
                    JSToneCurved MYSTERY = new JSToneCurved();
                    MYSTERY.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.mystery));
                    return MYSTERY;
                case TOES:
                    JSToneCurved TOES = new JSToneCurved();
                    TOES.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.toes));
                    return TOES;
                case KISSKISS:
                    JSToneCurved KISSKISS = new JSToneCurved();
                    KISSKISS.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.kisskiss));
                    return KISSKISS;
                case SPARK:
                    JSToneCurved SPARK = new JSToneCurved();
                    SPARK.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.sparks));
                    return SPARK;
                case LULLABYE:
                    JSToneCurved LULLABYE = new JSToneCurved();
                    LULLABYE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.lullabye));
                    return LULLABYE;
                case OLDPOSTCARD:
                    JSToneCurved OLDPOSTCARD = new JSToneCurved();
                    OLDPOSTCARD.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.old_postcards_ii));
                    return OLDPOSTCARD;
                case WILDATHEART:
                    JSToneCurved WILDATHEART = new JSToneCurved();
                    WILDATHEART.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.wild_at_heart));
                    return WILDATHEART;
                case MONTHWING:
                    JSToneCurved MONTHWING = new JSToneCurved();
                    MONTHWING.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.moth_wings));
                    return MONTHWING;
                case AUGUSTMARCH:
                    JSToneCurved AUGUSTMARCH = new JSToneCurved();
                    AUGUSTMARCH.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.august_march));
                    return AUGUSTMARCH;
                case AFTERGLOW:
                    JSToneCurved AFTERGLOW = new JSToneCurved();
                    AFTERGLOW.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.afterglow));
                    return AFTERGLOW;
                case AFTERTWINLUNGS:
                    JSToneCurved AFTERTWINLUNGS = new JSToneCurved();
                    AFTERTWINLUNGS.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.twin_lungs));
                    return AFTERTWINLUNGS;
                case BLOODORANGE:
                    JSToneCurved BLOODORANGE = new JSToneCurved();
                    BLOODORANGE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.blood_orange));
                    return BLOODORANGE;
                case RIVERSANDRAIN:
                    JSToneCurved RIVERSANDRAIN = new JSToneCurved();
                    RIVERSANDRAIN.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.rivers_and_rain));
                    return RIVERSANDRAIN;
                case GREENVY:
                    JSToneCurved GREENVY = new JSToneCurved();
                    GREENVY.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.greenenvy));
                    return GREENVY;
                case PISTOL:
                    JSToneCurved PISTOL = new JSToneCurved();
                    PISTOL.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.pistol));
                    return PISTOL;
                case COLDDESERT:
                    JSToneCurved COLDDESERT = new JSToneCurved();
                    COLDDESERT.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.colddesert));
                    return COLDDESERT;
                case COUNTRY:
                    JSToneCurved COUNTRY = new JSToneCurved();
                    COUNTRY.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.country));
                    return COUNTRY;
                case TRAINS:
                    JSToneCurved TRAINS = new JSToneCurved();
                    TRAINS.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.trains));
                    return TRAINS;
                case FOGGYBLUE:
                    JSToneCurved FOGGYBLUE = new JSToneCurved();
                    FOGGYBLUE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.fogy_blue));
                    return FOGGYBLUE;
                case FRESHBLUE:
                    JSToneCurved FRESHBLUE = new JSToneCurved();
                    FRESHBLUE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.fresh_blue));
                    return FRESHBLUE;
                case GOSHINYOURHEAD:
                    JSToneCurved GOSHINYOURHEAD = new JSToneCurved();
                    GOSHINYOURHEAD.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.ghostsinyourhead));
                    return GOSHINYOURHEAD;
                case WINDOWWARMTH:
                    JSToneCurved WINDOWWARMTH = new JSToneCurved();
                    WINDOWWARMTH.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.windowwarmth));
                    return WINDOWWARMTH;
                case BABYFACE:
                    JSToneCurved BABYFACE = new JSToneCurved();
                    BABYFACE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.babyface));
                    return BABYFACE;
                case SKYPEBLUE:
                    JSToneCurved SKYPEBLUE = new JSToneCurved();
                    SKYPEBLUE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.xanh));
                    return SKYPEBLUE;

                //image adjust
                case BRIGHTNESS:
                    return new GPUImageBrightnessFilter();
                case CONTRAST:
                    return new GPUImageContrastFilter();
                case EXPOSURE:
                    return new GPUImageExposureFilter();
                case HUE:
                    return new GPUImageHueFilter();
                case SATURATION:
                    return new GPUImageSaturationFilter();
                case SHARPEN:
                    return new GPUImageSharpenFilter();
                //---------------------------------

                case VIGNETTE:
                    GPUImageVignetteFilter gpuImageVignetteFilter = new GPUImageVignetteFilter(new PointF(0.5f, 0.5f), new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f);
                    return gpuImageVignetteFilter;

                case NONE:
                    return new JSNormalFilter();

                default:
                    return null;//new JSNormalFilter(); // null;
            }
        }
    }

    public MagicFilterType getCurrentFilterType() {
        return filterType;
    }
}
