package com.innso.utils.cache;

import android.os.Looper;
import android.os.Message;

import com.innso.utils.diskLruCache.DiskLruCache;
import com.innso.utils.handler.Handler;
import com.innso.utils.handler.ISender;
import com.innso.utils.thread.ThreadPool;

import java.io.File;
import java.io.IOException;

/**
 * Created with Android Studio
 * <p/>
 * Project: lockscreen
 * Author: zhangshaolin(www.iooly.com)
 * Date:   14-6-26
 * Time:   上午7:46
 * Email:  app@iooly.com
 */
public class AsyncDiskLruCache<V> implements Handler {

    private static final String TASK_ID_PREFIX = "AsyncDiskLruCache://";

    private final DiskLruCache mDiskCache;
    private final ThreadPool mThreadPool;
    private final ISender mSender = ISender.Factory.newSender(this, Looper.getMainLooper());

    public static AsyncDiskLruCache open(File directory, int appVersion, int valueCount,
                                         long maxSize) throws IOException {
        DiskLruCache cache = DiskLruCache.open(directory, appVersion, valueCount, maxSize);
        return new AsyncDiskLruCache(cache);
    }

    private AsyncDiskLruCache(DiskLruCache cache)
            throws IOException {
        mDiskCache = cache;
        mThreadPool = ThreadPool.getInstance();
    }

    public void get(String key) {

    }

    @Override
    public void handleMessage(Message msg) {

    }
}
