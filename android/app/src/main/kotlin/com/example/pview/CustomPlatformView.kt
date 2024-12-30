package com.example.pview

import android.content.Context
import android.view.View
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class CustomPlatformView(
    context: Context,
    private val methodChannel: MethodChannel
) : PlatformView, MethodChannel.MethodCallHandler {
    private val customView: CustomCanvasView = CustomCanvasView(context)

    init {
        methodChannel.setMethodCallHandler(this)
    }

    override fun getView(): View {
        return customView
    }

    override fun dispose() {
        methodChannel.setMethodCallHandler(null)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "clear" -> {
                customView.clearCanvas()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }
}
