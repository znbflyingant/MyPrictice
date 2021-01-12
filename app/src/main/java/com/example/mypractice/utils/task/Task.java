package com.example.mypractice.utils.task;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by znb on 2020/11/18
 */
public class Task {
    private static final String TAG = "sq.Task";
    private static AtomicInteger sTaskId = new AtomicInteger(0);
    private int taskId = 0;
    private Runnable runnable = null;
    private boolean isExecuting = false;
    private boolean stopWhenExecuting = false;


    private Task() {
        taskId = sTaskId.getAndIncrement();
    }

    public static Task create() {
        return new Task();
    }

    public enum Result {
        Next,
        Stop,
    }

    public interface TaskFunc {
        Result exec(); //返回Next代表继续 循环， 返回Stop代表是该任务停止
    }

    public void oneShot(final TaskFunc taskFunc) {
        repeat(0, 0, taskFunc);
    }
    public void oneShot(long delay, final TaskFunc taskFunc) {
        repeat(delay, 0, taskFunc);
    }
    public void repeat(final long repeat, @NonNull final TaskFunc taskFunc){
        repeat(0,repeat,taskFunc);
    }
    public void repeat(long delay, final long repeat, @NonNull final TaskFunc taskFunc) {
        stop();
        if (isExecuting) {
            stopWhenExecuting = true;
        }
        runnable = new Runnable() {
            int crashTime = 0;
            @Override
            public void run() {
                Result ret = Result.Next;
                try {
                    isExecuting = true;
                    ret = taskFunc.exec();
                    isExecuting = false;
                    if (stopWhenExecuting) {
                        stopWhenExecuting = false;
                        return;
                    }
                    if (ret == null) {
                        ret = Result.Next;
                    }
                    crashTime = 0;
                } catch (Exception e) {
                    isExecuting = false;
                    Log.e(TAG, "run: task execption" + e.getCause() + "e.class==");
                    ++crashTime;
                    ret = Result.Next;
                }
                if (crashTime > 3) {
                    runnable = null;
                }
                if (ret != Result.Stop && repeat > 0) {
                    HandlerManager.getInstance().postDelay(repeat, runnable);
                } else {
                    HandlerManager.getInstance().remove(runnable);
                    runnable = null;
                }
            }
        };
        if (delay > 0) {
            HandlerManager.getInstance().postDelay(delay, runnable);
        } else {
            runnable.run();
        }

    }

    public boolean isRunning() {
        return runnable != null;
    }

    public void stop() {
        if (runnable != null) {
            HandlerManager.getInstance().stop(runnable);
            runnable = null;
        }
    }
}
