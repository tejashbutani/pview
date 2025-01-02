package com.example.pview

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.view.TextureRegistry

class CustomViewFactory(
    private val messenger: BinaryMessenger,
    private val textureRegistry: TextureRegistry
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val channel = MethodChannel(messenger, "custom_canvas_view_$viewId")
        return CustomPlatformView(context, channel, textureRegistry)
    }
}
