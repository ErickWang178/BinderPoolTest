package com.erick.binderpooltest.binderpool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.erick.binderpooltest.interfaceImpl.ComputeImpl;
import com.erick.binderpooltest.interfaceImpl.SecurityCenterImpl;

import java.util.concurrent.CountDownLatch;

/**
 * Binder连接池
 * 根据相应的AIDL接口编码获取相应的AIDL接口对象，
 * 不用为每个AIDL接口创建Service，否则，使用多个Service使得应用变得重量级。
 */

public class BinderPool {
    private static String TAG = "BinderPool";

    public static final int BINDER_CODE_SECURITY_CENTER = 1;
    public static final int BINDER_CODE_COMPUT = 2;
    private static volatile BinderPool mInstance;

    private final Context mContext;
    private IBinderPool mBinderPool;
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private BinderPool(Context context){
        mContext = context.getApplicationContext();
    }

    public static BinderPool getInstance(Context context){
        if (mInstance == null) {
            synchronized (BinderPool.class){
                if (mInstance == null){
                    mInstance = new BinderPool(context);
                }
            }
        }

        return mInstance;
    }

    public synchronized void connectBinderPoolService(){
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(mContext,BinderPoolService.class);
        mContext.bindService(service,mBinderPoolConnection,Context.BIND_AUTO_CREATE);

        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode){
        IBinder binder = null;
        if (mBinderPool != null){
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return binder;
    }


    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = BinderPoolImpl.Stub.asInterface(service);

            try {
                mBinderPool.asBinder().linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binderDied: binder died");

            mBinderPool.asBinder().unlinkToDeath(mDeathRecipient,0);
            mBinderPool = null;

            //重连
            connectBinderPoolService();
        }
    };

    public static class BinderPoolImpl extends IBinderPool.Stub{

        public BinderPoolImpl(){
            super();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;

            switch (binderCode){
                case BINDER_CODE_SECURITY_CENTER:
                    binder = new SecurityCenterImpl();
                    break;
                case BINDER_CODE_COMPUT:
                    binder = new ComputeImpl();
                    break;
                default:
            }

            return binder;
        }
    }
}
