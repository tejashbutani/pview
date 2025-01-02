package com.example.pview

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import android.view.View
import display.interactive.renderlib.RenderUtils
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import java.io.ByteArrayOutputStream

class CustomPlatformView(
    context: Context,
    private val methodChannel: MethodChannel,
    private val textureRegistry: TextureRegistry
) : MethodChannel.MethodCallHandler {
    private val rendLibView: RendLibSurfaceView = RendLibSurfaceView(context)
    private var textureEntry: TextureRegistry.SurfaceTextureEntry? = null
    private var surface: Surface? = null

    init {
        methodChannel.setMethodCallHandler(this)
    }

    private fun initializeTexture(): Long {
        // Create a new SurfaceTexture
        textureEntry = textureRegistry.createSurfaceTexture()
        val surfaceTexture = textureEntry!!.surfaceTexture()
        
        // Configure the surface texture
        surfaceTexture.setDefaultBufferSize(
            rendLibView.getWidth(),
            rendLibView.getHeight()
        )
        
        // Create a Surface for drawing
        surface = Surface(surfaceTexture)
        
        return textureEntry!!.id()
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "initTexture" -> {
                val textureId = initializeTexture()
                result.success(textureId)
            }
            "clear" -> {
                rendLibView.clear()
                updateTexture()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    private fun updateTexture() {
        surface?.let { surface ->
            // Get Canvas from Surface
            val canvas = surface.lockCanvas(null)
            try {
                // Draw the bitmap onto the surface
                canvas.drawBitmap(rendLibView.getBitmap(), 0f, 0f, null)
            } finally {
                surface.unlockCanvasAndPost(canvas)
            }
        }
    }

    fun dispose() {
        methodChannel.setMethodCallHandler(null)
        surface?.release()
        textureEntry?.release()
        surface = null
        textureEntry = null
    }
}
