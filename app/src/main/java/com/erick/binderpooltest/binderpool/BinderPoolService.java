package com.erick.binderpooltest.binderpool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * 只需要一个Service就可以管理多个AIDL接口
 * 使用Binder连接池，使应用变得较为轻量级
 */

public class BinderPoolService extends Service {
    private Binder mBinder = new BinderPool.BinderPoolImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
