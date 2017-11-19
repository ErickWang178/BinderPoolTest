package com.erick.binderpooltest.interfaceImpl;

import android.os.RemoteException;

import com.erick.binderpooltest.ISecurityCenter;

/**
 * Created by Administrator on 2017/11/19 0019.
 */

public class SecurityCenterImpl extends ISecurityCenter.Stub{
    private static char SECRET_CODE = '^';

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }

    @Override
    public String encrypt(String content) throws RemoteException {
        char [] chars = content.toCharArray();

        for (int i=0; i<chars.length; i++){
            chars[i] ^= SECRET_CODE;
        }

        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
