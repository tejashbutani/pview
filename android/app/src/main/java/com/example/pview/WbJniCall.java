/*
 * Copyright (c) 2019 Hitevision
 */

package com.hht.wbjni;

import android.graphics.Canvas;

public class WbJniCall {
    static {
        System.loadLibrary("whiteboard_celerate");
    }

    public static native void fbStart(); //init fast draw buffer

    public static native void fbSetCanvas(Canvas canvas); //deliver app canvas to fast draw buffer

    public static native void fbStop(); // destroy fast draw buffer

    public static native void fbClear();    //clear fast draw buffer

    public static native void fbShowGop(boolean isShow);    // MTK9666 only

}
