package com.example.pview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import display.interactive.renderlib.RenderUtils;

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

        // Library resources need to be loaded before use
        RenderUtils.initRendLib();
        // getScreenSize
        int[] resolution = RenderUtils.getDeviceNativeResolution(context);
        mScreenWidth = resolution[0];
        mScreenHeight = resolution[1];
        mBitmap = RenderUtils.getAccelerateBitmap(3840, 2160);

        getHolder().addCallback(this);


        mPath.moveTo(0f, 100f);


        // init paint
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
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
        if(MotionEvent.ACTION_DOWN == event.getAction()) {
            Prex = event.getX();
            Prey = event.getY();
            mPath.moveTo(event.getX(), event.getY());
            mPaintCanvas.drawPoint(Prex, Prey, mPaint);
        } else if(MotionEvent.ACTION_UP == event.getAction()) {
            mPaintCanvas.drawPoint(event.getX(), event.getY(), mPaint);
        } else if(MotionEvent.ACTION_MOVE == event.getAction()) {
            mPath.quadTo(Prex, Prey, event.getX(), event.getY());
            Prex = event.getX();
            Prey = event.getY();
            mPaintCanvas.drawPath(mPath,mPaint);
        }
        return true;
    }

}
