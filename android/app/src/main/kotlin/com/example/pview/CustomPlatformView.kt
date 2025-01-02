package com.example.pview

import android.content.Context
import android.view.View
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import io.flutter.view.TextureRegistry

class CustomPlatformView(
    context: Context,
    messenger: BinaryMessenger,
    viewId: Int,
    private val textureRegistry: TextureRegistry
) : PlatformView, MethodChannel.MethodCallHandler {
    private val rendLibView: RendLibSurfaceView = RendLibSurfaceView(context)
    private val methodChannel: MethodChannel = MethodChannel(messenger, "custom_canvas_view_$viewId")
    private var textureEntry: TextureRegistry.SurfaceTextureEntry? = null

    init {
        methodChannel.setMethodCallHandler(this)
    }

    override fun getView(): View {
        return rendLibView
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "initTexture" -> {
                textureEntry = textureRegistry.createSurfaceTexture()
                val surfaceTexture = textureEntry!!.surfaceTexture()
                
                // Configure the surface texture size
                surfaceTexture.setDefaultBufferSize(
                    rendLibView.getWidth(),
                    rendLibView.getHeight()
                )
                
                // Initialize the view with texture
                rendLibView.initializeTexture(surfaceTexture)
                
                result.success(textureEntry!!.id())
            }
            "dispose" -> {
                dispose()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    override fun dispose() {
        methodChannel.setMethodCallHandler(null)
        textureEntry?.release()
        textureEntry = null
        rendLibView.cleanup()
    }
}
