package com.erick.binderpooltest;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.erick.binderpooltest.binderpool.BinderPool;
import com.erick.binderpooltest.interfaceImpl.ComputeImpl;
import com.erick.binderpooltest.interfaceImpl.SecurityCenterImpl;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";

    private ISecurityCenter mSecurityCenter;
    private ICompute mCompute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new WorkRunnable()).start();
    }

    private class WorkRunnable implements Runnable{

        @Override
        public void run() {
            doWork();
        }
    }

    private void doWork() {
        //获取线程池对象
        BinderPool binderPool = BinderPool.getInstance(this);
        //链接线程池服务
        binderPool.connectBinderPoolService();

        //通过线程池获取相应模块的AIDL接口
        IBinder securityCenter = binderPool.queryBinder(BinderPool.BINDER_CODE_SECURITY_CENTER);
        mSecurityCenter = SecurityCenterImpl.asInterface(securityCenter);

        String msg = "Hello-Android";
        try {
            String password = mSecurityCenter.encrypt(msg);
            Log.d(TAG, "doWork: password==" + password);

            Log.d(TAG, "doWork: decrypt==" + mSecurityCenter.decrypt(password));
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        //通过线程池对象获取相应模块的AIDL接口
        IBinder compute = binderPool.queryBinder(BinderPool.BINDER_CODE_COMPUT);
        mCompute = ComputeImpl.asInterface(compute);
        try {
            Log.d(TAG, "doWork: 3+5=" + mCompute.add(3,5));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
