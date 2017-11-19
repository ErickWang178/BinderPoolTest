package com.erick.binderpooltest.interfaceImpl;

import android.os.RemoteException;

import com.erick.binderpooltest.ICompute;

/**
 * Created by Administrator on 2017/11/19 0019.
 */

public class ComputeImpl extends ICompute.Stub {
    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }

    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
