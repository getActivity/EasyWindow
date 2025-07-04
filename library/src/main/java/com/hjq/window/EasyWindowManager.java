package com.hjq.window;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/07/04
 *    desc   : EasyWindow 对象管理
 */
public final class EasyWindowManager {

    @NonNull
    private static final List<Reference<EasyWindow<?>>> WINDOW_INSTANCE_REFERENCE_LIST = new ArrayList<>();

    private EasyWindowManager() {
        // default implementation ignored
    }

    /**
     * 添加 EasyWindow 对象引用（仅供内部调用）
     */
    static synchronized void addWindowReference(@NonNull Reference<EasyWindow<?>> easyWindowReference) {
        EasyWindow<?> easyWindow = easyWindowReference.get();
        if (easyWindow == null) {
            return;
        }
        WINDOW_INSTANCE_REFERENCE_LIST.add(easyWindowReference);
    }

    /**
     * 移除 EasyWindow 对象引用（仅供内部调用）
     */
    static synchronized void removeWindowReference(@NonNull Reference<EasyWindow<?>> easyWindowReference) {
        // 清除对象引用
        easyWindowReference.clear();
        WINDOW_INSTANCE_REFERENCE_LIST.remove(easyWindowReference);
    }

    /**
     * 取消所有正在显示的悬浮窗
     */
    public static synchronized void cancelAllWindow() {
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            easyWindow.cancel();
        }
    }

    /**
     * 取消特定类名的悬浮窗
     */
    public static synchronized void cancelWindowByClass(@Nullable Class<? extends EasyWindow<?>> clazz) {
        if (clazz == null) {
            return;
        }
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!clazz.equals(easyWindow.getClass())) {
                continue;
            }
            easyWindow.cancel();
        }
    }

    /**
     * 取消特定标记的悬浮窗
     */
    public static synchronized void cancelWindowByTag(@Nullable String tag) {
        if (tag == null) {
            return;
        }
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!tag.equals(easyWindow.getWindowTag())) {
                continue;
            }
            easyWindow.cancel();
        }
    }

    /**
     * 显示所有已取消但未回收的悬浮窗
     */
    public static synchronized void showAllWindow() {
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            easyWindow.show();
        }
    }

    /**
     * 显示特定类名已取消但未回收的悬浮窗
     */
    public static synchronized void showWindowByClass(@Nullable Class<? extends EasyWindow<?>> clazz) {
        if (clazz == null) {
            return;
        }
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!clazz.equals(easyWindow.getClass())) {
                continue;
            }
            easyWindow.show();
        }
    }

    /**
     * 显示特定标记已取消但未回收的悬浮窗
     */
    public static synchronized void showWindowByTag(@Nullable String tag) {
        if (tag == null) {
            return;
        }
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!tag.equals(easyWindow.getWindowTag())) {
                continue;
            }
            easyWindow.show();
        }
    }

    /**
     * 回收所有正在显示的悬浮窗
     */
    public static synchronized void recycleAllWindow() {
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            // 这里解释一下，为什么要使用迭代器移除，如果不那么做的话
            // easyWindow.recycle 方法里面会再移除一次
            // 当前又是一个 while 循环，可能会出现角标越界的情况
            iterator.remove();
            easyWindow.recycle();
        }
    }

    /**
     * 回收特定类名的悬浮窗
     */
    public static synchronized void recycleWindowByClass(@Nullable Class<? extends EasyWindow<?>> clazz) {
        if (clazz == null) {
            return;
        }
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!clazz.equals(easyWindow.getClass())) {
                continue;
            }
            // 这里解释一下，为什么要使用迭代器移除，如果不那么做的话
            // easyWindow.recycle 方法里面会再移除一次
            // 当前又是一个 while 循环，可能会出现角标越界的情况
            iterator.remove();
            easyWindow.recycle();
        }
    }

    /**
     * 回收特定标记的悬浮窗
     */
    public static synchronized void recycleWindowByTag(@Nullable String tag) {
        if (tag == null) {
            return;
        }

        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!tag.equals(easyWindow.getWindowTag())) {
                continue;
            }
            // 这里解释一下，为什么要使用迭代器移除，如果不那么做的话
            // easyWindow.recycle 方法里面会再移除一次
            // 当前又是一个 while 循环，可能会出现角标越界的情况
            iterator.remove();
            easyWindow.recycle();
        }
    }

    /**
     * 判断当前是否有悬浮窗正在显示
     */
    public static synchronized boolean existAnyWindowShowing() {
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (easyWindow.isShowing()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前是否有特定类名的悬浮窗正在显示
     */
    public static synchronized boolean existWindowShowingByClass(@Nullable Class<? extends EasyWindow<?>> clazz) {
        if (clazz == null) {
            return false;
        }
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!clazz.equals(easyWindow.getClass())) {
                continue;
            }
            if (easyWindow.isShowing()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前是否有特定标记的悬浮窗正在显示
     */
    public static synchronized boolean existWindowShowingByTag(@Nullable String tag) {
        if (tag == null) {
            return false;
        }

        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!tag.equals(easyWindow.getWindowTag())) {
                continue;
            }
            if (easyWindow.isShowing()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 寻找特定类名的悬浮窗
     */
    @Nullable
    public static synchronized <X extends EasyWindow<?>> X findWindowInstanceByClass(@Nullable Class<X> clazz) {
        if (clazz == null) {
            return null;
        }
        for (Reference<EasyWindow<?>> easyWindowReference : WINDOW_INSTANCE_REFERENCE_LIST) {
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!clazz.equals(easyWindow.getClass())) {
                continue;
            }
            return (X) easyWindow;
        }
        return null;
    }

    /**
     * 寻找特定标记的悬浮窗
     */
    @Nullable
    public static synchronized EasyWindow<?> findWindowInstanceByTag(@Nullable String tag) {
        if (tag == null) {
            return null;
        }
        Iterator<Reference<EasyWindow<?>>> iterator = WINDOW_INSTANCE_REFERENCE_LIST.iterator();
        while (iterator.hasNext()) {
            Reference<EasyWindow<?>> easyWindowReference = iterator.next();
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            if (!tag.equals(easyWindow.getWindowTag())) {
                continue;
            }
            return easyWindow;
        }
        return null;
    }

    /**
     * 获取所有的悬浮窗
     */
    @NonNull
    public static synchronized List<EasyWindow<?>> getAllWindowInstance() {
        List<EasyWindow<?>> easyWindowList = new ArrayList<>(WINDOW_INSTANCE_REFERENCE_LIST.size());
        for (Reference<EasyWindow<?>> easyWindowReference : WINDOW_INSTANCE_REFERENCE_LIST) {
            if (easyWindowReference == null) {
                continue;
            }
            EasyWindow<?> easyWindow = easyWindowReference.get();
            if (easyWindow == null) {
                continue;
            }
            easyWindowList.add(easyWindow);
        }
        return easyWindowList;
    }
}