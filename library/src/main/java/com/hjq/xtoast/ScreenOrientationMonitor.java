package com.hjq.xtoast;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2022/10/03
 *    desc   : 屏幕方向旋转监听
 */
final class ScreenOrientationMonitor implements ComponentCallbacks {

   private int mScreenOrientation;

   private OnScreenOrientationCallback mCallback;

   public ScreenOrientationMonitor(Context context) {
      mScreenOrientation = context.getResources().getConfiguration().orientation;
      context.registerComponentCallbacks(this);
   }

   /**
    * 注册监听
    */
   void register(Context context) {
      context.registerComponentCallbacks(this);
   }

   /**
    * 取消监听
    */
   void unregister(Context context) {
      context.unregisterComponentCallbacks(this);
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      if (mScreenOrientation == newConfig.orientation) {
         return;
      }
      mScreenOrientation = newConfig.orientation;

      if (mCallback == null) {
         return;
      }
      mCallback.onScreenOrientationChange(mScreenOrientation);
   }

   @Override
   public void onLowMemory() {}

   public void setOnScreenOrientationCallback(OnScreenOrientationCallback callback) {
      mCallback = callback;
   }

   public interface OnScreenOrientationCallback {

      /**
       * 显示回调
       */
      default void onScreenOrientationChange(int orientation) {}
   }
}