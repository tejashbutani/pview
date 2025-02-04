package com.example.pview;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
//import display.interactive.renderlib.RenderUtils;
 import com.nomivision.sys.WhiteBoardSpeedup;


/**
 * @ClassName: display.interactive.rendlibtools.view
 * @Description: 作用表述
 * @Author: maoxingwen
 * @Date: 2024/11/23
 */
public class RendLibSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    /** only For Delta */
    // private WhiteBoardSpeedup mWhiteBoardSpeedup;
    
    private SurfaceHolder mHolder;

    private Bitmap mBitmap;

    private int mScreenWidth;

    private int mScreenHeight;

    /**
     * Drawing Canvas
     */
    private Canvas mPaintCanvas;


    private Paint mPaint;
    

    private float Prex = 0.0f;
    private float Prey = 0.0f;
    private Path mPath = new Path();

    private List<PointF> currentStrokePoints = new ArrayList<>();
    private MethodChannel methodChannel;

    private Map<Integer, Path> mPathMap = new HashMap<>();
    private Map<Integer, List<PointF>> mStrokePointsMap = new HashMap<>();
    private Map<Integer, Float> mLastXMap = new HashMap<>();
    private Map<Integer, Float> mLastYMap = new HashMap<>();

    private boolean isDashed = false;
    private static final float DASH_LENGTH = 30f;  // Length of dash
    private static final float GAP_LENGTH = 20f;   // Length of gap (2:3 ratio)
    private static final int defaultHighlighterAlpha = 75;

    public void setMethodChannel(MethodChannel channel) {
    this.methodChannel = channel;
   } 


    public RendLibSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public RendLibSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RendLibSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RendLibSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public RendLibSurfaceView(Context context, int initialColor, float initialWidth) {
        super(context);
        init(context, initialColor, initialWidth);
    }

    public RendLibSurfaceView(Context context, AttributeSet attrs, int initialColor, float initialWidth) {
        super(context, attrs);
        init(context, initialColor, initialWidth);
    }

    private void init(Context context) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        int[] resolution = RenderUtils.getDeviceNativeResolution(context);
//        mScreenWidth = resolution[0];
//        mScreenHeight = resolution[1];

        /** only for Testing on Tablet */
      mBitmap = Bitmap.createBitmap(3840, 2160, Bitmap.Config.ARGB_8888);

        /** only for HIKVISION */
//        RenderUtils.initRendLib();
//        mBitmap = RenderUtils.getAccelerateBitmap(3840, 2160);

        /** only for Delta */
        //  mWhiteBoardSpeedup = new WhiteBoardSpeedup();
        //  try {
        //      mWhiteBoardSpeedup.init(Bitmap.Config.ARGB_4444);
        //  } catch (Exception ex) {
        //      Log.e(TAG, "Failed to initialize WhiteBoardSpeedup: " + ex.toString());
        //      ex.printStackTrace();
        //  }
        //  mBitmap = mWhiteBoardSpeedup.getAccelFbCurFrameBitmap();

        getHolder().addCallback(this);
        
        // Initialize paint with optimal flags
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        float density = getResources().getDisplayMetrics().density;
        mPaint.setStrokeWidth(5.0f * density);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setPathEffect(new android.graphics.CornerPathEffect(40f));
        
        mPaintCanvas = new Canvas();
        mPaintCanvas.setBitmap(mBitmap);
    }

    private void init(Context context, int initialColor, float initialWidth) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        int[] resolution = RenderUtils.getDeviceNativeResolution(context);
//        mScreenWidth = resolution[0];
//        mScreenHeight = resolution[1];

        /** only for Testing on Tablet */
       mBitmap = Bitmap.createBitmap(3840, 2160, Bitmap.Config.ARGB_8888);

        /** only for HIKVISION */
//        RenderUtils.initRendLib();
//        mBitmap = RenderUtils.getAccelerateBitmap(3840, 2160);

        /** only for Delta */
        // mWhiteBoardSpeedup = new WhiteBoardSpeedup();
        // try {
        //     mWhiteBoardSpeedup.init(Bitmap.Config.ARGB_4444);
        // } catch (Exception ex) {
        //     Log.e(TAG, "Failed to initialize WhiteBoardSpeedup: " + ex.toString());
        //     ex.printStackTrace();
        // }
        // mBitmap = mWhiteBoardSpeedup.getAccelFbCurFrameBitmap();
//
        getHolder().addCallback(this);
        
        // Initialize paint with optimal flags and initial parameters
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(initialColor);
        float density = getResources().getDisplayMetrics().density;
        mPaint.setStrokeWidth(initialWidth * density);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setPathEffect(new android.graphics.CornerPathEffect(40f));
        
        mPaintCanvas = new Canvas();
        mPaintCanvas.setBitmap(mBitmap);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if(surfaceHolder != null) {
            mHolder = surfaceHolder;
            Canvas canvas = mHolder.lockCanvas();

            //For Tablet
           mHolder.setFormat(PixelFormat.TRANSLUCENT);
           canvas.drawColor(Color.GREEN);
           canvas.drawBitmap(mBitmap, 0, 0, null);


           //For IFP
            // canvas.drawColor(Color.WHITE);
            // mHolder.setFormat(PixelFormat.TRANSPARENT);

            mHolder.unlockCanvasAndPost(canvas);
        } else {
            Log.w("TestMXW", "surfaceHolder is null !!!");
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        clearCanvas();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                float startX = event.getX(actionIndex);
                float startY = event.getY(actionIndex);
                
                Path path = new Path();
                path.moveTo(startX, startY);
                mPathMap.put(pointerId, path);
                
                List<PointF> points = new ArrayList<>();
                points.add(new PointF(startX, startY));
                mStrokePointsMap.put(pointerId, points);
                
                mLastXMap.put(pointerId, startX);
                mLastYMap.put(pointerId, startY);

                if (isDashed) {
                    // For dashed lines, we'll draw segments based on physical distance
                    mPaintCanvas.drawPoint(startX, startY, mPaint);
                } else {
                    mPaintCanvas.drawPoint(startX, startY, mPaint);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < pointerCount; i++) {
                    int id = event.getPointerId(i);
                    float x = event.getX(i);
                    float y = event.getY(i);
                    
                    Path currentPath = mPathMap.get(id);
                    List<PointF> currentPoints = mStrokePointsMap.get(id);
                    Float lastX = mLastXMap.get(id);
                    Float lastY = mLastYMap.get(id);
                    
                    if (currentPath != null && currentPoints != null && lastX != null && lastY != null) {
                        if (isDashed) {
                            // Calculate the distance between points
                            float distance = (float) Math.sqrt(Math.pow(x - lastX, 2) + Math.pow(y - lastY, 2));
                            float density = getResources().getDisplayMetrics().density;
                            
                            // Only draw if we've moved enough distance
                            if (distance >= (DASH_LENGTH + GAP_LENGTH) * density) {
                                // Calculate the direction vector
                                float dirX = (x - lastX) / distance;
                                float dirY = (y - lastY) / distance;
                                
                                // Draw the dash
                                float dashEndX = lastX + dirX * DASH_LENGTH * density;
                                float dashEndY = lastY + dirY * DASH_LENGTH * density;
                                
                                currentPath.reset();
                                currentPath.moveTo(lastX, lastY);
                                currentPath.lineTo(dashEndX, dashEndY);
                                mPaintCanvas.drawPath(currentPath, mPaint);
                                
                                // Update the last position to after the gap
                                float newLastX = lastX + dirX * (DASH_LENGTH + GAP_LENGTH) * density;
                                float newLastY = lastY + dirY * (DASH_LENGTH + GAP_LENGTH) * density;
                                mLastXMap.put(id, newLastX);
                                mLastYMap.put(id, newLastY);
                            }
                        } else {
                            // Normal continuous line drawing
                            float midX = (lastX + x) / 2;
                            float midY = (lastY + y) / 2;
                            
                            currentPath.quadTo(lastX, lastY, midX, midY);
                            mPaintCanvas.drawPath(currentPath, mPaint);
                            
                            mLastXMap.put(id, x);
                            mLastYMap.put(id, y);
                        }
                        currentPoints.add(new PointF(x, y));
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                List<PointF> finalPoints = mStrokePointsMap.get(pointerId);
                if (finalPoints != null && methodChannel != null) {
                    Map<String, Object> strokeData = new HashMap<>();
                    List<Map<String, Double>> pointsList = new ArrayList<>();
                    
                    float density = getResources().getDisplayMetrics().density;
                    
                    for (PointF point : finalPoints) {
                        Map<String, Double> pointMap = new HashMap<>();
                        pointMap.put("x", (double) (point.x / density));
                        pointMap.put("y", (double) (point.y / density));
                        pointsList.add(pointMap);
                    }
                    
                    strokeData.put("points", pointsList);
                    strokeData.put("color", mPaint.getColor());
                    strokeData.put("width", (double) (mPaint.getStrokeWidth() / density));
                    
                    methodChannel.invokeMethod("onStrokeComplete", strokeData);
                }
                
                mPathMap.remove(pointerId);
                mStrokePointsMap.remove(pointerId);
                mLastXMap.remove(pointerId);
                mLastYMap.remove(pointerId);
                break;

            case MotionEvent.ACTION_CANCEL:
                mPathMap.clear();
                mStrokePointsMap.clear();
                mLastXMap.clear();
                mLastYMap.clear();
                break;
        }

        //For Tablet only
        // After each touch event, update the screen
        if (mHolder != null) {
            Canvas canvas = mHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.WHITE);  // Clear the canvas
                canvas.drawBitmap(mBitmap, 0, 0, null);  // Draw the bitmap
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
        
        return true;
    }

    public void updatePenColor(int color) {
        float density = getResources().getDisplayMetrics().density;
        
        mPaint.setColor(color);
        
        if (Color.alpha(color) < 255) {
            // Highlighter settings
            mPaint.setStrokeCap(Paint.Cap.SQUARE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setPathEffect(null);
            mPaint.setXfermode(new android.graphics.PorterDuffXfermode(
                android.graphics.PorterDuff.Mode.SRC_OVER));
            mPaint.setAlpha(Color.alpha(color));
            mPaint.setAlpha(defaultHighlighterAlpha);
        } else {
            // Normal pen settings (both dashed and continuous)
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setPathEffect(new android.graphics.CornerPathEffect(40f));
            mPaint.setXfermode(null);
            mPaint.setAlpha(255);
        }
    }

    public void updatePenWidth(float width) {
        float density = getResources().getDisplayMetrics().density;
        float physicalWidth = width * density;
        mPaint.setStrokeWidth(physicalWidth);
    }

    public void setDashed(boolean dashed) {
        isDashed = dashed;
        // Reapply pen settings to ensure path effect is updated
        updatePenColor(mPaint.getColor());
    }

    public void clearCanvas() {
        /** only for HIKVISION */
//        RenderUtils.clearBitmapContent();

        /** only for Delta */
        //  try {
        //      mWhiteBoardSpeedup.clearFbFrame(WhiteBoardSpeedup.WhichFrameFlags.ALL);
        //  } catch (Exception ex) {
        //      Log.e(TAG, "Failed to clear canvas: " + ex.toString());
        //      ex.printStackTrace();
        //  }
    }
}
