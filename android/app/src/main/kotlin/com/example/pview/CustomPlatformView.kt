package com.example.pview

import android.content.Context
import android.view.View
import display.interactive.renderlib.RenderUtils
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class CustomPlatformView(
    context: Context,
    private val methodChannel: MethodChannel,
    creationParams: Map<String, Any>?
) : PlatformView, MethodChannel.MethodCallHandler {
    private val rendLibView: RendLibSurfaceView = RendLibSurfaceView(context).apply {
        creationParams?.let {
            val color = it["color"] as? Int
            val width = it["width"] as? Double
            if (color != null && width != null) {
                updatePenSettings(color, width.toFloat())
            }
        }
    }

    init {
        methodChannel.setMethodCallHandler(this)
        rendLibView.setMethodChannel(methodChannel)
    }

    override fun getView(): View {
        return rendLibView
    }

    override fun dispose() {
        methodChannel.setMethodCallHandler(null)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "updatePenSettings" -> {
                val color = (call.argument<Number>("color"))?.toInt()
                val width = call.argument<Double>("width")
                android.util.Log.d("PenSettings", "Received method call - Color: $color, Width: $width")
                if (color != null && width != null) {
                    rendLibView.updatePenSettings(color, width.toFloat())
                    result.success(null)
                } else {
                    android.util.Log.e("PenSettings", "Invalid arguments - Color: $color, Width: $width")
                    result.error("INVALID_ARGUMENTS", "Color or width is null", null)
                }
            }
            "clear" -> {
                RenderUtils.clearBitmapContent()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }
}
