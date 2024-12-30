package com.example.pview

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

class CustomCanvasView(context: Context) : View(context) {
    private val paths = mutableListOf<Path>()
    private var currentPath: Path? = null
    private var lastX = 0f
    private var lastY = 0f
    
    // Create a bitmap to cache the drawing
    private var cacheBitmap: Bitmap? = null
    private var cacheCanvas: Canvas? = null
    
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Initialize or recreate the bitmap when size changes
        cacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        cacheCanvas = Canvas(cacheBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw the cached bitmap
        cacheBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        
        // Draw only the current path on top
        currentPath?.let { canvas.drawPath(it, paint) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path().apply {
                    moveTo(x, y)
                }
                lastX = x
                lastY = y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.let { path ->
                    val dx = Math.abs(x - lastX)
                    val dy = Math.abs(y - lastY)
                    
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                        lastX = x
                        lastY = y
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                currentPath?.let { path ->
                    path.lineTo(x, y)
                    // Draw the completed path to the cache
                    cacheCanvas?.drawPath(path, paint)
                    paths.add(path)
                }
                currentPath = null
                invalidate()
            }
        }
        
        return true
    }

    fun clearCanvas() {
        paths.clear()
        cacheBitmap?.eraseColor(Color.TRANSPARENT)
        currentPath = null
        invalidate()
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }
}
