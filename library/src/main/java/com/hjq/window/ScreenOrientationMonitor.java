package com.hjq.window;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2022/10/03
 *    desc   : 屏幕方向旋转监听
 */
final class ScreenOrientationMonitor implements ComponentCallbacks {

   /** 当前屏幕的方向 */
   private int mScreenOrientation;

   /** 屏幕旋转回调 */
   @Nullable
   private Reference<OnScreenOrientationCallback> mCallbackReference;

   public ScreenOrientationMonitor(int screenOrientation) {
      mScreenOrientation = screenOrientation;
   }

   /**
    * 注册监听
    */
   void registerCallback(@Nullable Context context, @Nullable OnScreenOrientationCallback callback) {
      if (context == null) {
         return;
      }
      if (callback == null) {
         unregisterCallback(context);
         return;
      }
      Context applicationContext = context.getApplicationContext();
      if (applicationContext != null) {
         applicationContext.registerComponentCallbacks(this);
      }
      mCallbackReference = new WeakReference<>(callback);
   }

   /**
    * 取消监听
    */
   void unregisterCallback(@Nullable Context context) {
      if (context == null) {
          return;
      }
      Context applicationContext = context.getApplicationContext();
      if (applicationContext != null) {
         applicationContext.unregisterComponentCallbacks(this);
      }
      if (mCallbackReference != null) {
         mCallbackReference.clear();
      }
      mCallbackReference = null;
   }

   @Override
   public void onConfigurationChanged(@NonNull Configuration newConfig) {
      if (mScreenOrientation == newConfig.orientation) {
         return;
      }
      mScreenOrientation = newConfig.orientation;

      if (mCallbackReference == null) {
         return;
      }
      OnScreenOrientationCallback callback = mCallbackReference.get();
      if (callback == null) {
          return;
      }
      callback.onScreenOrientationChange(mScreenOrientation);
   }

   @Override
   public void onLowMemory() {
      // default implementation ignored
   }

   /**
    * 屏幕方向监听器
    */
   interface OnScreenOrientationCallback {

      /**
       * 监听屏幕旋转了
       *
       * @param newOrientation         最新的屏幕方向
       */
      default void onScreenOrientationChange(int newOrientation) {}
   }
}