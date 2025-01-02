package com.example.pview;

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

import display.interactive.renderlib.RenderUtils;
import io.flutter.plugin.common.MethodChannel;

/**
 * @ClassName: display.interactive.rendlibtools.view
 * @Description: 作用表述
 * @Author: maoxingwen
 * @Date: 2024/11/23
 */
public class RendLibSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    
    private SurfaceHolder mHolder;

    private Bitmap mBitmap;


    private int mScreenWidth;

    private int mScreenHeight;

    /**
     * Drawing Camvas
     */
    private Canvas mPaintCanvas;


    private Paint mPaint;
    

    private float Prex = 0.0f;
    private float Prey = 0.0f;
    private Path mPath = new Path();

    private List<PointF> currentStrokePoints = new ArrayList<>();
    private MethodChannel methodChannel;

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

    private void init(Context context) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        
        RenderUtils.initRendLib();
//        int[] resolution = RenderUtils.getDeviceNativeResolution(context);
//        mScreenWidth = resolution[0];
//        mScreenHeight = resolution[1];
        
        // Create bitmap with optimal config for drawing
        mBitmap = RenderUtils.getAccelerateBitmap(3840, 2160);
        
        getHolder().addCallback(this);
        
        // Initialize paint with optimal flags
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setDither(true);
        
        mPaintCanvas = new Canvas();
        mPaintCanvas.setBitmap(mBitmap);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if(surfaceHolder != null) {
            mHolder = surfaceHolder;
            Canvas canvas = mHolder.lockCanvas();
            // Set the background of the acceleration bitmap to transparent
            canvas.drawColor(Color.WHITE);
            mHolder.setFormat(PixelFormat.TRANSPARENT);
            mHolder.unlockCanvasAndPost(canvas);
        } else {
            Log.w("TestMXW", "surfaceHolder is nulll !!!");
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        RenderUtils.clearBitmapContent();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Prex = event.getX();
                Prey = event.getY();
                currentStrokePoints.clear();
                currentStrokePoints.add(new PointF(Prex, Prey));
                mPath.moveTo(Prex, Prey);
                mPaintCanvas.drawPoint(Prex, Prey, mPaint);
                break;
                
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                currentStrokePoints.add(new PointF(x, y));
                mPath.lineTo(x, y);
                mPaintCanvas.drawLine(Prex, Prey, x, y, mPaint);
                Prex = x;
                Prey = y;
                break;
                
            case MotionEvent.ACTION_UP:
                currentStrokePoints.add(new PointF(event.getX(), event.getY()));
                mPaintCanvas.drawPoint(event.getX(), event.getY(), mPaint);
                
                // Send stroke data back to Flutter
                if (methodChannel != null) {
                    Map<String, Object> strokeData = new HashMap<>();
                    List<Map<String, Double>> points = new ArrayList<>();
                    
                    for (PointF point : currentStrokePoints) {
                        Map<String, Double> pointMap = new HashMap<>();
                        pointMap.put("x", (double) point.x);
                        pointMap.put("y", (double) point.y);
                        points.add(pointMap);
                    }
                    
                    strokeData.put("points", points);
                    strokeData.put("color", Color.RED);
                    strokeData.put("width", 4.0);
                    
                    methodChannel.invokeMethod("onStrokeComplete", strokeData);
                }
                mPath.reset();
                break;
        }
        return true;
    }

}
