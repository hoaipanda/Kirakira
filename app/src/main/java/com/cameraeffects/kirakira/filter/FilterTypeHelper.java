package com.cameraeffects.kirakira.filter;


import com.cameraeffects.kirakira.R;
import com.cameraeffects.kirakira.filter.helper.analog3_MagicFilterType;

public class FilterTypeHelper {

    public static analog3_MagicFilterType[] analog3_types = new analog3_MagicFilterType[]{
            analog3_MagicFilterType.NONE,
            analog3_MagicFilterType.GREENVY,
            analog3_MagicFilterType.BABYFACE,
            analog3_MagicFilterType.RIVERSANDRAIN,
            analog3_MagicFilterType.MONTHWING,
            analog3_MagicFilterType.GOSHINYOURHEAD,
            analog3_MagicFilterType.FOGGYBLUE,
            analog3_MagicFilterType.FRESHBLUE,
            analog3_MagicFilterType.SKYPEBLUE,
            analog3_MagicFilterType.WINDOWWARMTH,
            analog3_MagicFilterType.AFTERTWINLUNGS,
            analog3_MagicFilterType.BLOODORANGE,
            analog3_MagicFilterType.WILDATHEART,
            analog3_MagicFilterType.SPARK,
            analog3_MagicFilterType.LULLABYE,
            analog3_MagicFilterType.OLDPOSTCARD,
            analog3_MagicFilterType.AUGUSTMARCH,
            analog3_MagicFilterType.AFTERGLOW,
            analog3_MagicFilterType.FAIRYTALE,
            analog3_MagicFilterType.SUNRISE,
            analog3_MagicFilterType.SUNSET,
            analog3_MagicFilterType.WHITECAT,
            analog3_MagicFilterType.BLACKCAT,
            analog3_MagicFilterType.HUDSON,
            analog3_MagicFilterType.LEMON,
            analog3_MagicFilterType.KISSKISS,
            analog3_MagicFilterType.SKINWHITEN,
            analog3_MagicFilterType.HEALTHY,
            analog3_MagicFilterType.COLDDESERT,
            analog3_MagicFilterType.SWEETS,
            analog3_MagicFilterType.MYSTERY,
            analog3_MagicFilterType.TOES,
            analog3_MagicFilterType.ROMANCE,
            analog3_MagicFilterType.SAKURA,
            analog3_MagicFilterType.WARM,
            analog3_MagicFilterType.ANTIQUE,
            analog3_MagicFilterType.NOSTALGIA,
            analog3_MagicFilterType.CALM,
            analog3_MagicFilterType.LATTE,
            analog3_MagicFilterType.TENDER,
            analog3_MagicFilterType.COOL,
            analog3_MagicFilterType.EMERALD,
            analog3_MagicFilterType.EVERGREEN,
            analog3_MagicFilterType.AMARO,
            analog3_MagicFilterType.BRANNAN,
            analog3_MagicFilterType.BROOKLYN,
            analog3_MagicFilterType.EARLYBIRD,
            analog3_MagicFilterType.FREUD,
            analog3_MagicFilterType.HEFE,
            analog3_MagicFilterType.INKWELL,
            analog3_MagicFilterType.KEVIN,
            analog3_MagicFilterType.N1977,
            analog3_MagicFilterType.NASHVILLE,
            analog3_MagicFilterType.PIXAR,
            analog3_MagicFilterType.RISE,
            analog3_MagicFilterType.SIERRA,
            analog3_MagicFilterType.SUTRO,
            analog3_MagicFilterType.TOASTER2,
            analog3_MagicFilterType.VALENCIA,
            analog3_MagicFilterType.WALDEN,
            analog3_MagicFilterType.XPROII,
            analog3_MagicFilterType.GOLLDENT,
            analog3_MagicFilterType.PISTOL,
            analog3_MagicFilterType.COUNTRY,
            analog3_MagicFilterType.TRAINS
//            analog3_MagicFilterType.CRAYON,
//            analog3_MagicFilterType.SKETCH,
    };

    public static int FilterType2Name(analog3_MagicFilterType filterType) {
        switch (filterType) {
            case NONE:
                return R.string.filter_normal;
            case WHITECAT:
                return R.string.filter_whitecat;
            case BLACKCAT:
                return R.string.filter_blackcat;
            case ROMANCE:
                return R.string.filter_romance;
            case SAKURA:
                return R.string.filter_sakura;
            case AMARO:
                return R.string.filter_amaro;
            case BRANNAN:
                return R.string.filter_brannan;
            case BROOKLYN:
                return R.string.filter_brooklyn;
            case EARLYBIRD:
                return R.string.filter_Earlybird;
            case FREUD:
                return R.string.filter_freud;
            case HEFE:
                return R.string.filter_hefe;
            case HUDSON:
                return R.string.filter_hudson;
            case INKWELL:
                return R.string.filter_inkwell;
            case KEVIN:
                return R.string.filter_kevin;
            case N1977:
                return R.string.filter_1977;
            case NASHVILLE:
                return R.string.filter_nashville;
            case PIXAR:
                return R.string.filter_pixar;
            case RISE:
                return R.string.filter_rise;
            case SIERRA:
                return R.string.filter_sierra;
            case SUTRO:
                return R.string.filter_sutro;
            case TOASTER2:
                return R.string.filter_toastero;
            case VALENCIA:
                return R.string.filter_valencia;
            case WALDEN:
                return R.string.filter_walden;
            case XPROII:
                return R.string.filter_xproii;
            case ANTIQUE:
                return R.string.filter_antique;
            case CALM:
                return R.string.filter_calm;
            case COOL:
                return R.string.filter_cool;
            case EMERALD:
                return R.string.filter_emerald;
            case EVERGREEN:
                return R.string.filter_evergreen;
            case FAIRYTALE:
                return R.string.filter_fairytale;
            case HEALTHY:
                return R.string.filter_healthy;
            case NOSTALGIA:
                return R.string.filter_nostalgia;
            case TENDER:
                return R.string.filter_tender;
            case SWEETS:
                return R.string.filter_sweets;
            case LATTE:
                return R.string.filter_latte;
            case WARM:
                return R.string.filter_warm;
            case SUNRISE:
                return R.string.filter_sunrise;
            case SUNSET:
                return R.string.filter_sunset;
            case SKINWHITEN:
                return R.string.filter_skinwhiten;
            case CRAYON:
                return R.string.filter_crayon;
            case SKETCH:
                return R.string.filter_sketch;
            case GOLLDENT:
                return R.string.filter_goldenhousse;
            case LEMON:
                return R.string.filter_lemon;
            case MYSTERY:
                return R.string.filter_mystery;
            case TOES:
                return R.string.filter_toes;
            case KISSKISS:
                return R.string.kisskiss;
            case SPARK:
                return R.string.spark;
            case LULLABYE:
                return R.string.lullabye;
            case OLDPOSTCARD:
                return R.string.old_post_cards;
            case WILDATHEART:
                return R.string.wild_at_heart;
            case MONTHWING:
                return R.string.moth_wing;
            case AUGUSTMARCH:
                return R.string.august_march;
            case AFTERGLOW:
                return R.string.afterglow;
            case AFTERTWINLUNGS:
                return R.string.twin_lungs;
            case BLOODORANGE:
                return R.string.blood_orange;
            case RIVERSANDRAIN:
                return R.string.rivers_and_rain;
            case GREENVY:
                return R.string.greenenvy;
            case PISTOL:
                return R.string.piston;
            case COLDDESERT:
                return R.string.colddesert;
            case COUNTRY:
                return R.string.country;
            case TRAINS:
                return R.string.trains;
            case FOGGYBLUE:
                return R.string.fogy_blue;
            case FRESHBLUE:
                return R.string.fresh_blue;
            case GOSHINYOURHEAD:
                return R.string.ghostinyourhead;
            case WINDOWWARMTH:
                return R.string.windowwarmth;
            case BABYFACE:
                return R.string.babyface;
            case SKYPEBLUE:
                return R.string.skyblue;
            default:
                return R.string.filter_normal;
        }
    }
}
