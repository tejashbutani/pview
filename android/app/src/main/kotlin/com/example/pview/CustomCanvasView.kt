package com.example.pview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View

class CustomCanvasView(context: Context) : View(context) {
    private val paths = mutableListOf<Path>()
    private var currentPath: Path? = null
    private var lastX = 0f
    private var lastY = 0f
    
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw all completed paths
        for (path in paths) {
            canvas.drawPath(path, paint)
        }
        // Draw current path if exists
        currentPath?.let { canvas.drawPath(it, paint) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Start new path
                currentPath = Path().apply {
                    moveTo(x, y)
                }
                lastX = x
                lastY = y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Only draw if we have a current path
                currentPath?.let { path ->
                    // Calculate the distance moved
                    val dx = Math.abs(x - lastX)
                    val dy = Math.abs(y - lastY)
                    
                    // Only register movement if it's significant enough
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        // Create smooth curve through points
                        path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                        lastX = x
                        lastY = y
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                // Complete the path and add to paths list
                currentPath?.let { path ->
                    path.lineTo(x, y)
                    paths.add(path)
                }
                currentPath = null
            }
        }
        
        invalidate()
        return true
    }

    fun clearCanvas() {
        paths.clear()
        currentPath = null
        invalidate()
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }
}
