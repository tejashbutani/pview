package com.example.pview

import android.content.Context
import android.view.View
import display.interactive.renderlib.RenderUtils
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class CustomPlatformView(
    context: Context,
    private val methodChannel: MethodChannel
) : PlatformView, MethodChannel.MethodCallHandler {
    private val rendLibView: RendLibSurfaceView = RendLibSurfaceView(context)

    init {
        methodChannel.setMethodCallHandler(this)
    }

    override fun getView(): View {
        return rendLibView
    }

    override fun dispose() {
        methodChannel.setMethodCallHandler(null)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "clear" -> {
                RenderUtils.clearBitmapContent()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }
}
