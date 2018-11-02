package com.seu.magicfilter.detection;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceTrackerFactory implements MultiProcessor.Factory<Face> {
    private GraphicOverlay mGraphicOverlay;
    private Bitmap bitmap;

    private Bitmap bitmapEarL;
    private Bitmap bitmapEarR;
    private Bitmap bitmapCheekL;
    private Bitmap bitmapCheekR;
    private Bitmap bitmapNose;
    private Bitmap bitmapHat;
    private Bitmap bitmapGlasses;
    private int checkSwichCamera;
    private GraphicTracker graphicTracker;
    private ArrayList<GraphicTracker> list = new ArrayList<>();
    private ArrayList<FaceGraphic> listGraphic = new ArrayList<>();

    public void setBitmapGlasses(Bitmap bitmapGlasses) {
        this.bitmapGlasses = bitmapGlasses;
        for (int i = 0; i < listGraphic.size(); i++) {
            listGraphic.get(i).setBitmapGlasses(bitmapGlasses);
        }
    }

    public void setCheckSwichCamera(int checkSwichCamera) {
        this.checkSwichCamera = checkSwichCamera;
        for (int i = 0; i < listGraphic.size(); i++) {
            listGraphic.get(i).setCheckSwichCamera(checkSwichCamera);
        }
    }

    public void setBitmapHat(Bitmap bitmapHat) {
        this.bitmapHat = bitmapHat;
        for (int i = 0; i < listGraphic.size(); i++) {
            listGraphic.get(i).setBitmapHat(bitmapHat);
        }
    }

    public void setBitmapNose(Bitmap bitmapNose) {
        this.bitmapNose = bitmapNose;
        for (int i = 0; i < listGraphic.size(); i++) {
            listGraphic.get(i).setBitmapNose(bitmapNose);
        }
    }

    private FaceGraphic graphic;


    public void setBitmapEarL(Bitmap bitmapEarL) {
        this.bitmapEarL = bitmapEarL;
        for (int i = 0; i < listGraphic.size(); i++) {
            listGraphic.get(i).setBitmapEarL(bitmapEarL);
        }
    }

    public void setBitmapEarR(Bitmap bitmapEarR) {
        this.bitmapEarR = bitmapEarR;
        for (int i = 0; i < listGraphic.size(); i++) {
            listGraphic.get(i).setBitmapEarR(bitmapEarR);
        }
    }

    public void setBitmapCheekL(Bitmap bitmapCheekL) {
        this.bitmapCheekL = bitmapCheekL;
        for (int i = 0; i < listGraphic.size(); i++) {
            listGraphic.get(i).setBitmapCheekL(bitmapCheekL);
        }
    }


    public void setBitmapCheekR(Bitmap bitmapCheekR) {
        this.bitmapCheekR = bitmapCheekR;
        for (int i = 0; i < listGraphic.size(); i++) {
            listGraphic.get(i).setBitmapCheekR(bitmapCheekR);
        }
    }

    public void setBitmapHeadband(Bitmap bitmap) {
        this.bitmap = bitmap;
        for (int i = 0; i < listGraphic.size(); i++) {
            listGraphic.get(i).setBitmapHeadband(bitmap);
        }
    }

    public FaceTrackerFactory(GraphicOverlay graphicOverlay) {
        mGraphicOverlay = graphicOverlay;
    }


    @Override
    public Tracker<Face> create(Face face) {
        Log.d("create tracker", "create tracker");
        graphic = new FaceGraphic(mGraphicOverlay);
        graphic.setBitmapHeadband(bitmap);
        graphic.setBitmapCheekL(bitmapCheekL);
        graphic.setBitmapCheekR(bitmapCheekR);
        graphic.setBitmapEarL(bitmapEarL);
        graphic.setBitmapEarR(bitmapEarR);
        graphic.setBitmapNose(bitmapNose);
        graphic.setBitmapHat(bitmapHat);
        graphic.setCheckSwichCamera(checkSwichCamera);
        graphic.setBitmapGlasses(bitmapGlasses);
        listGraphic.add(graphic);
        graphicTracker = new GraphicTracker<>(mGraphicOverlay, graphic);
        list.add(graphicTracker);
        return graphicTracker;
    }
}

/**
 * Graphic instance for rendering face position, size, and ID within an associated graphic overlay
 * view.
 */
class FaceGraphic extends TrackedGraphic<Face> {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.MAGENTA,
            Color.RED,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;
    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();
    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private static final float EYE_RADIUS_PROPORTION = 0.45f;

    PointF eyeLeft;
    PointF eyeRight;
    PointF nose;
    PointF mouthBottom;
    PointF mouthLeft;
    PointF mouthRight;
    PointF cheekLeft;
    PointF cheekRight;
    PointF center;


    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Face face) {
        mFace = face;
        updatePreviousProportions(mFace);
        eyeRight = getLandmarkPosition(mFace, Landmark.RIGHT_EYE);
        eyeLeft = getLandmarkPosition(mFace, Landmark.LEFT_EYE);
        nose = getLandmarkPosition(mFace, Landmark.NOSE_BASE);
        mouthBottom = getLandmarkPosition(mFace, Landmark.BOTTOM_MOUTH);
        mouthLeft = getLandmarkPosition(mFace, Landmark.LEFT_MOUTH);
        mouthRight = getLandmarkPosition(mFace, Landmark.RIGHT_MOUTH);
        cheekLeft = getLandmarkPosition(mFace, Landmark.LEFT_CHEEK);
        cheekRight = getLandmarkPosition(mFace, Landmark.RIGHT_CHEEK);

        postInvalidate();
    }


    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }
        PointF detectLeftEYE = eyeLeft;
        PointF detectRightEYE = eyeRight;

        PointF detectRighCheek = cheekRight;
        PointF detectLeftCheek = cheekLeft;
        PointF detectCenterNoseP = nose;
        PointF detectLeftMouthLeft = mouthLeft;
        PointF detectRightMouth = mouthRight;
        PointF detectButtomMouth = mouthBottom;


        float cx = translateX(face.getPosition().x + face.getWidth() / 2);
        float cy = translateY(face.getPosition().y + face.getHeight() / 2);
        center = new PointF(cx, cy);

        if (detectLeftEYE == null || detectRightEYE == null || detectLeftCheek == null ||
                detectRighCheek == null || detectLeftMouthLeft == null || detectRightMouth == null || detectCenterNoseP == null
                || detectButtomMouth == null || center == null) {
            return;
        }
        PointF leftPosition =
                new PointF(translateX(detectLeftEYE.x), translateY(detectLeftEYE.y));

        PointF rightPosition =
                new PointF(translateX(detectRightEYE.x), translateY(detectRightEYE.y));
        PointF leftCheekL =
                new PointF(translateX(detectLeftCheek.x), translateY(detectLeftCheek.y));

        PointF rightCheeekR =
                new PointF(translateX(detectRighCheek.x), translateY(detectRighCheek.y));


        PointF nose = new PointF(translateX(detectCenterNoseP.x), translateY(detectCenterNoseP.y));
        float distance = (float) Math.sqrt(
                Math.pow(rightPosition.x - leftPosition.x, 2) +
                        Math.pow(rightPosition.y - leftPosition.y, 2));

        float eyeRadius = EYE_RADIUS_PROPORTION * distance;

        if (checkSwichCamera == 1) {
            drawGlasses(canvas, face, eyeRadius, leftPosition, rightPosition);
        } else {
            drawGlasses(canvas, face, eyeRadius, rightPosition, leftPosition);
        }

        if (checkSwichCamera == 0) {
            drawCheek(canvas, face, eyeRadius, rightCheeekR, leftCheekL);
        } else if (checkSwichCamera == 1) {
            drawCheek(canvas, face, eyeRadius, leftCheekL, rightCheeekR);
        }

        drawEar(canvas, face, leftCheekL, rightCheeekR, center);
        drawNose(canvas, face, nose, eyeRadius);
        drawHat(canvas, face, center, leftCheekL, rightCheeekR);
        drawHeadband(canvas, face);
//        if (face == null) {
//            Log.d("datdb", "face null");
//            return;
//        }
//
//        PointF detectLeftEYE = eyeLeft;
//        PointF detectRightEYE = eyeRight;
//        PointF detectRighCheek = cheekRight;
//        PointF detectLeftCheek = cheekLeft;
//        PointF detectCenterNoseP = nose;
//        PointF detectLeftMouthLeft = mouthLeft;
//        PointF detectRightMouth = mouthRight;
//        PointF detectButtomMouth = mouthBottom;
//
//        float cx = translateX(face.getPosition().x + face.getWidth() / 2);
//        float cy = translateY(face.getPosition().y + face.getHeight() / 2);
//        center = new PointF(cx, cy);
//
//        if (detectLeftEYE == null || detectRightEYE == null || detectLeftCheek == null ||
//                detectRighCheek == null || detectLeftMouthLeft == null || detectRightMouth == null || detectCenterNoseP == null
//                || detectButtomMouth == null || center == null) {
//            return;
//        }
//
//        PointF leftPosition =
//                new PointF(translateX(detectLeftEYE.x), translateY(detectLeftEYE.y));
//
//        PointF rightPosition =
//                new PointF(translateX(detectRightEYE.x), translateY(detectRightEYE.y));
//        PointF leftCheekL =
//                new PointF(translateX(detectLeftCheek.x), translateY(detectLeftCheek.y));
//
//        PointF rightCheeekR =
//                new PointF(translateX(detectRighCheek.x), translateY(detectRighCheek.y));
//
//
//        PointF nose = new PointF(translateX(detectCenterNoseP.x), translateY(detectCenterNoseP.y));
//        float distance = (float) Math.sqrt(
//                Math.pow(rightPosition.x - leftPosition.x, 2) +
//                        Math.pow(rightPosition.y - leftPosition.y, 2));
//
//        float eyeRadius = EYE_RADIUS_PROPORTION * distance;
//
//        if (checkSwichCamera == 0) {
//
//            drawCheek(canvas, face, eyeRadius, rightCheeekR, leftCheekL);
//        } else if (checkSwichCamera == 1) {
//            drawCheek(canvas, face, eyeRadius, leftCheekL, rightCheeekR);
//        }
//
//        drawNose(canvas, face, nose, eyeRadius);
//        drawHat(canvas, face, center, leftCheekL, rightCheeekR);
//        drawHeadband(canvas, face);
    }


    private void drawHeadband(Canvas canvas, Face face) {
        if (bitmapHeadband != null && center != null) {
            Bitmap b = Bitmap.createScaledBitmap(bitmapHeadband, (int) face.getWidth(), (int) face.getWidth() * bitmapHeadband.getHeight() / bitmapHeadband.getWidth(), false);
            Camera camera = new Camera();
            Matrix matrix = new Matrix();
            camera.save();
            camera.rotateY(-face.getEulerY());
            camera.rotateZ(-face.getEulerZ());
            Log.d("gocnghieng", face.getEulerY() + " " + face.getEulerZ());
            camera.getMatrix(matrix);
            matrix.preTranslate(-(center.x - b.getWidth() / 2) / 2, -(center.y - b.getHeight() / 2 - distanca2Point(eyeLeft, eyeRight)) / 2);
            matrix.postTranslate((center.x - b.getWidth() / 2) / 2, (center.y - b.getHeight() / 2 - distanca2Point(eyeLeft, eyeRight)) / 2);
            matrix.postTranslate(center.x - b.getWidth() / 2, center.y - b.getHeight() / 2 - distanca2Point(eyeLeft, eyeRight));
//            matrix.preTranslate(-center.x / 2, -center.y / 2);
//            matrix.postTranslate(center.x / 2, center.y / 2);
//            matrix.postTranslate(center.x - b.getWidth() / 2, center.y - b.getHeight() / 2 - distanca2Point(eyeLeft, eyeRight));
            canvas.drawBitmap(b, matrix, null);
        } else {
        }
    }

    private void drawGlasses(Canvas canvas, Face face, float eyeRadius, PointF leftPosition, PointF rightPosition) {
        if (bitmapGlasses != null && center != null && eyeRight != null && eyeLeft != null) {
            int w = distanca2Point(eyeLeft, eyeRight) * 2 + distanca2Point(eyeLeft, eyeRight) / 3;
            Bitmap b = Bitmap.createScaledBitmap(bitmapGlasses, w, w * bitmapGlasses.getHeight() / bitmapGlasses.getWidth(), false);
            Camera camera = new Camera();
            Matrix matrix = new Matrix();
            camera.save();
            camera.rotateY(-face.getEulerY());
            camera.rotateZ(-face.getEulerZ());
            camera.getMatrix(matrix);
            matrix.preTranslate(-(center.x - b.getWidth() / 2) / 2, -(center.y - 2 * b.getHeight() / 5) / 4.5f);
            matrix.postTranslate((center.x - b.getWidth() / 2) / 2, (center.y - 2 * b.getHeight() / 5) / 4.5f);
            matrix.postTranslate(center.x - b.getWidth() / 2, center.y - 2 * b.getHeight() / 5);
            canvas.drawBitmap(b, matrix, null);
        } else {
        }

    }

    private void drawCheek(Canvas canvas, Face face, float eyeRadius, PointF leftCheekL, PointF rightCheeekR) {
        if (bitmapCheekL != null) {
            Camera cameraL = new Camera();
            Matrix matrixL = new Matrix();
            cameraL.save();
            cameraL.rotateY(-face.getEulerY());
            cameraL.rotateZ(-face.getEulerZ());

            int w1 = (int) (((rightCheeekR.x - leftCheekL.x)) / 1.4f) / 2;
            Bitmap resizedMaL = Bitmap.createScaledBitmap(bitmapCheekL, w1, w1 * bitmapCheekL.getHeight() / bitmapCheekL.getWidth(), false);
            matrixL.preTranslate(-(leftCheekL.x - resizedMaL.getWidth()), -(leftCheekL.y - resizedMaL.getHeight() / 2));
            matrixL.postTranslate(leftCheekL.x - resizedMaL.getWidth(), leftCheekL.y - resizedMaL.getHeight() / 2);
            matrixL.postTranslate(leftCheekL.x - resizedMaL.getWidth(), leftCheekL.y - resizedMaL.getHeight() / 2);
//            matrixL.preTranslate(-(center.x - resizedMaL.getWidth()) * 1.4f, -(center.y - resizedMaL.getHeight() / 2 + distanca2Point(leftCheekL, rightCheeekR)) / 1.6f);
//            matrixL.postTranslate((center.x - resizedMaL.getWidth()) * 1.4f, (center.y - resizedMaL.getHeight() / 2 + distanca2Point(leftCheekL, rightCheeekR)) / 1.6f);
//            matrixL.postTranslate(center.x - resizedMaL.getWidth() * 1.4f, center.y - resizedMaL.getHeight() / 2 + distanca2Point(leftCheekL, rightCheeekR) / 1.6f);

            canvas.drawBitmap(resizedMaL, matrixL, mBoxPaint);
            cameraL.restore();
        }
        if (bitmapCheekR != null) {
            Camera cameraR = new Camera();
            Matrix matrixR = new Matrix();
            cameraR.save();
            cameraR.rotateY(-face.getEulerY());
            cameraR.rotateZ(-face.getEulerZ());
            int w2 = (int) (((rightCheeekR.x - leftCheekL.x)) / 1.4f) / 2;
            Bitmap resizedMaR = Bitmap.createScaledBitmap(bitmapCheekR, w2, w2 * bitmapCheekL.getHeight() / bitmapCheekL.getWidth(), false);
//            matrixR.preTranslate(-(center.x + resizedMaR.getWidth() / 1.6f), -(center.y - resizedMaR.getHeight() / 2 + distanca2Point(leftCheekL, rightCheeekR)) / 1.6f);
//            matrixR.postTranslate((center.x + resizedMaR.getWidth() / 1.6f), (center.y - resizedMaR.getHeight() / 2 + distanca2Point(leftCheekL, rightCheeekR)) / 1.6f);
//            matrixR.postTranslate(center.x + resizedMaR.getWidth() / 1.6f, center.y - resizedMaR.getHeight() / 2 + distanca2Point(leftCheekL, rightCheeekR) / 1.6f);
            matrixR.preTranslate(-rightCheeekR.x, -(rightCheeekR.y - resizedMaR.getHeight() / 2));
            matrixR.postTranslate(rightCheeekR.x, rightCheeekR.y - resizedMaR.getHeight() / 2);
            matrixR.postTranslate(rightCheeekR.x, rightCheeekR.y - resizedMaR.getHeight() / 2);
            canvas.drawBitmap(resizedMaR, matrixR, mIdPaint);
            cameraR.restore();
        }
    }


    /*drawEar */
    private void drawEar(Canvas canvas, Face face, PointF cheekLeft, PointF cheekRight, PointF center) {

        if (bitmapEarL != null) {

            Bitmap bitmapEarLeft = Bitmap.createScaledBitmap(bitmapEarL, (int) (((cheekRight.x - cheekLeft.x)) / 1.4f), (int) (((cheekRight.x - cheekLeft.x)) / 1.4f), false);
            if (checkSwichCamera == 0) {
                bitmapEarLeft = rotateBitmap(bitmapEarLeft, 180);
            }
            Camera cameraEarLeft = new Camera();
            Matrix matrixEarLeft = new Matrix();
            cameraEarLeft.save();
            cameraEarLeft.rotateY(-face.getEulerY() / 2);
            cameraEarLeft.rotateZ(-face.getEulerZ() / 2);
            Log.d("gocnghieng", face.getEulerY() + " " + face.getEulerZ());
            cameraEarLeft.getMatrix(matrixEarLeft);
            matrixEarLeft.preTranslate(-(center.x - bitmapEarLeft.getWidth() * 1.5f) / 1.2f, -(center.y - bitmapEarLeft.getHeight() / 1.2f - distanca2Point(cheekLeft, cheekRight)) / 1.2f);
            matrixEarLeft.postTranslate((center.x - bitmapEarLeft.getWidth() * 1.5f) / 1.2f, (center.y - bitmapEarLeft.getHeight() / 1.2f - distanca2Point(cheekLeft, cheekRight)) / 1.2f);
            matrixEarLeft.postTranslate(center.x - bitmapEarLeft.getWidth() * 1.5f, center.y - bitmapEarLeft.getHeight() / 1.2f - distanca2Point(cheekLeft, cheekRight));

            canvas.drawBitmap(bitmapEarLeft, matrixEarLeft, mBoxPaint);

            cameraEarLeft.restore();
        }

//
        if (bitmapEarR != null) {
            Camera cameraEarRight = new Camera();
            Matrix matrixEarRight = new Matrix();
            cameraEarRight.save();
            cameraEarRight.rotateY(-face.getEulerY() / 2);
            cameraEarRight.rotateZ(-face.getEulerZ() / 2);

            Bitmap resizedEarRight = Bitmap.createScaledBitmap(bitmapEarR, (int) (((cheekRight.x - cheekLeft.x)) / 1.4f), (int) (((cheekRight.x - cheekLeft.x)) / 1.4f), false);
            if (checkSwichCamera == 0) {
                resizedEarRight = rotateBitmap(resizedEarRight, 180);
            }

            matrixEarRight.preTranslate(-(center.x + resizedEarRight.getWidth() / 1.5f) / 1.2f, -(center.y - resizedEarRight.getHeight() / 1.2f - distanca2Point(cheekLeft, cheekRight)) / 1.2f);
            matrixEarRight.postTranslate((center.x + resizedEarRight.getWidth() / 1.5f) / 1.2f, (center.y - resizedEarRight.getHeight() / 1.2f - distanca2Point(cheekLeft, cheekRight)) / 1.2f);
            matrixEarRight.postTranslate(center.x + resizedEarRight.getWidth() / 1.5f, center.y - resizedEarRight.getHeight() / 1.2f - distanca2Point(cheekLeft, cheekRight));
            canvas.drawBitmap(resizedEarRight, matrixEarRight, mIdPaint);

            cameraEarRight.restore();
        }
    }


    private void drawNose(Canvas canvas, Face face, PointF nose, float radius) {

        if (bitmapNose != null) {

            Bitmap bmNose = Bitmap.createScaledBitmap(bitmapNose, (int) (radius / 1.4f), (int) (radius / 1.4f), false);
//            Camera cameraNose = new Camera();
            Matrix matrixNose = new Matrix();
//            cameraNose.save();
//            cameraNose.rotateY(-face.getEulerY());
//            cameraNose.rotateZ(-face.getEulerZ());
            Log.d("gocnghieng", face.getEulerY() + " " + face.getEulerZ());
//            cameraNose.getMatrix(matrixNose);
            matrixNose.preTranslate(-(nose.x - bmNose.getWidth() / 2), -(nose.y) - bmNose.getHeight() / 2);
            matrixNose.postTranslate(nose.x - bmNose.getWidth() / 2, nose.y - bmNose.getHeight() / 2);
            matrixNose.postTranslate(nose.x - bmNose.getWidth() / 2, nose.y - bmNose.getHeight() / 2);
//            matrix.preTranslate(-center.x / 2, -center.y / 2);
//            matrix.postTranslate(center.x / 2, center.y / 2);
//            matrix.postTranslate(center.x - b.getWidth() / 2, center.y - b.getHeight() / 2 - distanca2Point(eyeLeft, eyeRight));
            canvas.drawBitmap(bmNose, matrixNose, mBoxPaint);
//            cameraNose.restore();
        }

    }


    private void drawHat(Canvas canvas, Face face, PointF center, PointF eyeLeft, PointF eyeRight) {

        if (bitmapHat != null && center != null) {
            Bitmap b = Bitmap.createScaledBitmap(bitmapHat, (int) face.getWidth(), (int) face.getWidth() * bitmapHat.getHeight() / bitmapHat.getWidth(), false);
            Camera cameraHat = new Camera();
            Matrix matrixHat = new Matrix();
            cameraHat.save();
            cameraHat.rotateY(-face.getEulerY());
            cameraHat.rotateZ(-face.getEulerZ());
            cameraHat.getMatrix(matrixHat);
            matrixHat.preTranslate(-(center.x - b.getWidth() / 2) / 2, -(center.y - b.getHeight() / 1.6f - distanca2Point(eyeLeft, eyeRight)) / 1.2f);
            matrixHat.postTranslate((center.x - b.getWidth() / 2) / 2, (center.y - b.getHeight() / 1.6f - distanca2Point(eyeLeft, eyeRight)) / 1.2f);
            matrixHat.postTranslate(center.x - b.getWidth() / 2, center.y - b.getHeight() / 1.6f - distanca2Point(eyeLeft, eyeRight));
            canvas.drawBitmap(b, matrixHat, null);
        }
    }

    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public int distanca2Point(PointF p1, PointF p2) {
        if (p1 == null || p2 == null) {
            return 0;
        }
        int powDeltaX = (int) Math.pow(p1.x - p2.x, 2);
        int powDeltaY = (int) Math.pow(p1.y - p2.y, 2);

        return (int) Math.sqrt(powDeltaX + powDeltaY);
    }

    private Bitmap bitmapHeadband;
    private Bitmap bitmapCheekL;
    private Bitmap bitmapCheekR;
    private Bitmap bitmapEarL;
    private Bitmap bitmapEarR;
    private Bitmap bitmapNose;
    private Bitmap bitmapHat;
    private Bitmap bitmapGlasses;
    private int checkSwichCamera; // 0: sau 1: trc


    public void setBitmapGlasses(Bitmap bitmapGlasses) {
        this.bitmapGlasses = bitmapGlasses;
        postInvalidate();
    }

    public void setCheckSwichCamera(int checkSwichCamera) {
        this.checkSwichCamera = checkSwichCamera;
        postInvalidate();
    }

    public void setBitmapHat(Bitmap bitmapHat) {
        this.bitmapHat = bitmapHat;
        postInvalidate();
    }

    public void setBitmapNose(Bitmap bitmapNose) {
        this.bitmapNose = bitmapNose;
        postInvalidate();
    }

    public void setBitmapEarR(Bitmap bitmapEarR) {
        this.bitmapEarR = bitmapEarR;
        postInvalidate();
    }


    public void setBitmapEarL(Bitmap bitmapEarL) {
        this.bitmapEarL = bitmapEarL;
        postInvalidate();
    }

    public void setBitmapCheekR(Bitmap bitmapCheekR) {
        this.bitmapCheekR = bitmapCheekR;
        postInvalidate();
    }

    public void setBitmapCheekL(Bitmap bitmapCheekL) {
        this.bitmapCheekL = bitmapCheekL;
        postInvalidate();
    }

    public void setBitmapHeadband(Bitmap bitmapHeadband) {
        this.bitmapHeadband = bitmapHeadband;
        postInvalidate();
    }

    private void updatePreviousProportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }
        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }
        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }

}
