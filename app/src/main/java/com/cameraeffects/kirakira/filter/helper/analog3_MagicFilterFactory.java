package com.cameraeffects.kirakira.filter.helper;

import android.content.Context;

import com.cameraeffects.kirakira.R;
import com.cameraeffects.kirakira.filter.advanced.analog3_JSAmaroFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_JSAntiqueFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_JSNormalFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_JSSierraFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_JSSunsetFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_JSTenderFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_JSToneCurved;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicBlackCatFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicBrannanFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicBrooklynFilterF;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicCalmFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicCoolFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicCrayonFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicEarlyBirdFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicEmeraldFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicEvergreenFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicFairytaleFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicFreudFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicHealthyFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicHefeFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicHudsonFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicInkwellFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicKevinFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicLatteFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicN1977Filter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicNashvilleFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicNostalgiaFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicPixarFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicRiseFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicRomanceFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicSakuraFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicSketchFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicSkinWhitenFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicSunriseFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicSutroFilterF;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicSweetsFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicToasterFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicValenciaFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicWaldenFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicWarmFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicWhiteCatFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_MagicXproIIFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_adjust.analog3_GPUImageBrightnessFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_adjust.analog3_GPUImageContrastFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_adjust.analog3_GPUImageExposureFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_adjust.analog3_GPUImageHueFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_adjust.analog3_GPUImageSaturationFilter;
import com.cameraeffects.kirakira.filter.advanced.analog3_adjust.analog3_GPUImageSharpenFilter;
import com.cameraeffects.kirakira.filter.base.analog3_GPUImageFilter;


public class analog3_MagicFilterFactory {

    private static analog3_MagicFilterType filterType = analog3_MagicFilterType.NONE;

    public static analog3_GPUImageFilter initFilters(analog3_MagicFilterType type, Context appctx) {
        if (type == null) {
            return new analog3_JSNormalFilter();
        } else {
            filterType = type;
            switch (type) {
                case FREUD:
                    return new analog3_MagicFreudFilter();
                case SKETCH:
                    return new analog3_MagicSketchFilter();
                case CRAYON:
                    return new analog3_MagicCrayonFilter();
                case NOSTALGIA:
                    return new analog3_MagicNostalgiaFilter();
                case SAKURA:
                    return new analog3_MagicSakuraFilter();
                case SKINWHITEN:
                    return new analog3_MagicSkinWhitenFilter();
                case HEALTHY:
                    return new analog3_MagicHealthyFilter();
                case WHITECAT:
                    return new analog3_MagicWhiteCatFilter();
                case BLACKCAT:
                    return new analog3_MagicBlackCatFilter();
                case ROMANCE:
                    return new analog3_MagicRomanceFilter();
                case AMARO:
                    return new analog3_JSAmaroFilter();
                case WALDEN:
                    return new analog3_MagicWaldenFilter();
                case ANTIQUE:
                    return new analog3_JSAntiqueFilter();
                case CALM:
                    return new analog3_MagicCalmFilter();
                case BRANNAN:
                    return new analog3_MagicBrannanFilter();
                case BROOKLYN:
                    return new analog3_MagicBrooklynFilterF();
                case EARLYBIRD:
                    return new analog3_MagicEarlyBirdFilter();
                case HEFE:
                    return new analog3_MagicHefeFilter();
                case HUDSON:
                    return new analog3_MagicHudsonFilter();
                case INKWELL:
                    return new analog3_MagicInkwellFilter();
                case KEVIN:
                    return new analog3_MagicKevinFilter();
                case N1977:
                    return new analog3_MagicN1977Filter();
                case NASHVILLE:
                    return new analog3_MagicNashvilleFilter();
                case PIXAR:
                    return new analog3_MagicPixarFilter();
                case RISE:
                    return new analog3_MagicRiseFilter();
                case SIERRA:
                    return new analog3_JSSierraFilter();
                case SUTRO:
                    return new analog3_MagicSutroFilterF();
                case TOASTER2:
                    return new analog3_MagicToasterFilter();
                case VALENCIA:
                    return new analog3_MagicValenciaFilter();
                case XPROII:
                    return new analog3_MagicXproIIFilter();
                case EVERGREEN:
                    return new analog3_MagicEvergreenFilter();
                case COOL:
                    return new analog3_MagicCoolFilter();
                case EMERALD:
                    return new analog3_MagicEmeraldFilter();
                case LATTE:
                    return new analog3_MagicLatteFilter();
                case WARM:
                    return new analog3_MagicWarmFilter();
                case TENDER:
                    return new analog3_JSTenderFilter();
                case SWEETS:
                    return new analog3_MagicSweetsFilter();
                case FAIRYTALE:
                    return new analog3_MagicFairytaleFilter();
                case SUNRISE:
                    return new analog3_MagicSunriseFilter();
                case SUNSET:
                    return new analog3_JSSunsetFilter();
                case GOLLDENT:
                    analog3_JSToneCurved goldent = new analog3_JSToneCurved();
                    goldent.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.goldenhour));
                    return goldent;
                case LEMON:
                    analog3_JSToneCurved LEMON = new analog3_JSToneCurved();
                    LEMON.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.tonelemon));
                    return LEMON;
                case MYSTERY:
                    analog3_JSToneCurved MYSTERY = new analog3_JSToneCurved();
                    MYSTERY.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.mystery));
                    return MYSTERY;
                case TOES:
                    analog3_JSToneCurved TOES = new analog3_JSToneCurved();
                    TOES.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.toes));
                    return TOES;
                case KISSKISS:
                    analog3_JSToneCurved KISSKISS = new analog3_JSToneCurved();
                    KISSKISS.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.kisskiss));
                    return KISSKISS;
                case SPARK:
                    analog3_JSToneCurved SPARK = new analog3_JSToneCurved();
                    SPARK.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.sparks));
                    return SPARK;
                case LULLABYE:
                    analog3_JSToneCurved LULLABYE = new analog3_JSToneCurved();
                    LULLABYE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.lullabye));
                    return LULLABYE;
                case OLDPOSTCARD:
                    analog3_JSToneCurved OLDPOSTCARD = new analog3_JSToneCurved();
                    OLDPOSTCARD.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.old_postcards_ii));
                    return OLDPOSTCARD;
                case WILDATHEART:
                    analog3_JSToneCurved WILDATHEART = new analog3_JSToneCurved();
                    WILDATHEART.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.wild_at_heart));
                    return WILDATHEART;
                case MONTHWING:
                    analog3_JSToneCurved MONTHWING = new analog3_JSToneCurved();
                    MONTHWING.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.moth_wings));
                    return MONTHWING;
                case AUGUSTMARCH:
                    analog3_JSToneCurved AUGUSTMARCH = new analog3_JSToneCurved();
                    AUGUSTMARCH.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.august_march));
                    return AUGUSTMARCH;
                case AFTERGLOW:
                    analog3_JSToneCurved AFTERGLOW = new analog3_JSToneCurved();
                    AFTERGLOW.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.afterglow));
                    return AFTERGLOW;
                case AFTERTWINLUNGS:
                    analog3_JSToneCurved AFTERTWINLUNGS = new analog3_JSToneCurved();
                    AFTERTWINLUNGS.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.twin_lungs));
                    return AFTERTWINLUNGS;
                case BLOODORANGE:
                    analog3_JSToneCurved BLOODORANGE = new analog3_JSToneCurved();
                    BLOODORANGE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.blood_orange));
                    return BLOODORANGE;
                case RIVERSANDRAIN:
                    analog3_JSToneCurved RIVERSANDRAIN = new analog3_JSToneCurved();
                    RIVERSANDRAIN.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.rivers_and_rain));
                    return RIVERSANDRAIN;
                case GREENVY:
                    analog3_JSToneCurved GREENVY = new analog3_JSToneCurved();
                    GREENVY.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.greenenvy));
                    return GREENVY;
                case PISTOL:
                    analog3_JSToneCurved PISTOL = new analog3_JSToneCurved();
                    PISTOL.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.pistol));
                    return PISTOL;
                case COLDDESERT:
                    analog3_JSToneCurved COLDDESERT = new analog3_JSToneCurved();
                    COLDDESERT.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.colddesert));
                    return COLDDESERT;
                case COUNTRY:
                    analog3_JSToneCurved COUNTRY = new analog3_JSToneCurved();
                    COUNTRY.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.country));
                    return COUNTRY;
                case TRAINS:
                    analog3_JSToneCurved TRAINS = new analog3_JSToneCurved();
                    TRAINS.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.trains));
                    return TRAINS;
                case FOGGYBLUE:
                    analog3_JSToneCurved FOGGYBLUE = new analog3_JSToneCurved();
                    FOGGYBLUE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.fogy_blue));
                    return FOGGYBLUE;
                case FRESHBLUE:
                    analog3_JSToneCurved FRESHBLUE = new analog3_JSToneCurved();
                    FRESHBLUE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.fresh_blue));
                    return FRESHBLUE;
                case GOSHINYOURHEAD:
                    analog3_JSToneCurved GOSHINYOURHEAD = new analog3_JSToneCurved();
                    GOSHINYOURHEAD.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.ghostsinyourhead));
                    return GOSHINYOURHEAD;
                case WINDOWWARMTH:
                    analog3_JSToneCurved WINDOWWARMTH = new analog3_JSToneCurved();
                    WINDOWWARMTH.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.windowwarmth));
                    return WINDOWWARMTH;
                case BABYFACE:
                    analog3_JSToneCurved BABYFACE = new analog3_JSToneCurved();
                    BABYFACE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.babyface));
                    return BABYFACE;
                case SKYPEBLUE:
                    analog3_JSToneCurved SKYPEBLUE = new analog3_JSToneCurved();
                    SKYPEBLUE.setFromCurveFileInputStream(appctx.getResources().openRawResource(R.raw.xanh));
                    return SKYPEBLUE;
                case BRIGHTNESS:
                    return new analog3_GPUImageBrightnessFilter();
                case CONTRAST:
                    return new analog3_GPUImageContrastFilter();
                case EXPOSURE:
                    return new analog3_GPUImageExposureFilter();
                case HUE:
                    return new analog3_GPUImageHueFilter();
                case SATURATION:
                    return new analog3_GPUImageSaturationFilter();
                case SHARPEN:
                    return new analog3_GPUImageSharpenFilter();
                //---------------------------------

                default:
                    return new analog3_JSNormalFilter(); // null;
            }
        }
    }

    public analog3_MagicFilterType getCurrentFilterType() {
        return filterType;
    }
}


