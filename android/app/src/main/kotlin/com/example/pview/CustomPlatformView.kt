package com.example.pview

import android.content.Context
import android.graphics.Color
import android.view.View
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class CustomPlatformView(
    context: Context,
    private val methodChannel: MethodChannel,
    creationParams: Map<String, Any>?
) : PlatformView, MethodChannel.MethodCallHandler {
    private val rendLibView: RendLibSurfaceView = RendLibSurfaceView(
        context,
        (creationParams?.get("color") as? Number)?.toInt() ?: Color.BLACK,
        (creationParams?.get("width") as? Double)?.toFloat() ?: 5.0f
    )

    init {
        methodChannel.setMethodCallHandler(this)
        rendLibView.setMethodChannel(methodChannel)
        
        // Handle initial pen settings
        val isDashed = creationParams?.get("isDashed") as? Boolean ?: false
        rendLibView.setDashed(isDashed)
        rendLibView.updatePenColor(
            (creationParams?.get("color") as? Number)?.toInt() ?: Color.BLACK
        )
        rendLibView.updatePenWidth(
            (creationParams?.get("width") as? Double)?.toFloat() ?: 5.0f
        )
    }

    override fun getView(): View {
        return rendLibView
    }

    override fun dispose() {
        methodChannel.setMethodCallHandler(null)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "updatePenColor" -> {
                val color = (call.argument<Number>("color"))?.toInt()
                if (color != null) {
                    rendLibView.updatePenColor(color)
                    result.success(null)
                } else {
                    result.error("INVALID_ARGUMENTS", "Color is null", null)
                }
            }
            "updatePenWidth" -> {
                val width = call.argument<Double>("width")
                if (width != null) {
                    rendLibView.updatePenWidth(width.toFloat())
                    result.success(null)
                } else {
                    result.error("INVALID_ARGUMENTS", "Width is null", null)
                }
            }
            "setDashed" -> {
                val isDashed = call.argument<Boolean>("dashed") ?: false
                rendLibView.setDashed(isDashed)
                result.success(null)
            }
            "clear" -> {
                rendLibView.clearCanvas()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }
}
