package com.example.pview

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.view.TextureRegistry

class CustomViewFactory(
    private val messenger: BinaryMessenger,
    private val textureRegistry: TextureRegistry
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        return CustomPlatformView(context, messenger, viewId, textureRegistry)
    }
}
