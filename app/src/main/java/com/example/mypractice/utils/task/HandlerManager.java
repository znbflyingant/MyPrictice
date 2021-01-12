package com.example.mypractice.utils.task;

import android.os.Handler;
import android.os.Looper;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述:
 * 作者：znb
 * 时间：2021/1/5 09:29
 */
class HandlerManager {
    private Handler handler;
    private Set<Runnable> runnables = new HashSet<>();
    private static final HandlerManager ourInstance = new HandlerManager();
    public static HandlerManager getInstance() {
        return ourInstance;
    }

    private HandlerManager() {
        this.handler = new Handler(Looper.getMainLooper());
    }
    public void release() {
        for (Runnable runnable : runnables) {
            this.handler.removeCallbacks(runnable);
        }

        this.handler = null;

    }
    public void postDelay(long delay, Runnable runnable) {
        runnables.add(runnable);
        if (handler == null) {
            return;
        }
        handler.postDelayed(runnable, delay);
    }

    public void remove(Runnable runnable) {
        if (runnable!=null) {
            runnables.remove(runnable);
        }
    }

    public void stop(Runnable runnable) {
        remove(runnable);
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
