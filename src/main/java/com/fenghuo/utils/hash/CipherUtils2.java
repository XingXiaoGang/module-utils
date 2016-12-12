package com.fenghuo.utils.hash;

import android.util.Log;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

//某个程序里面用的
public final class CipherUtils2 {

    public static String key;
    public static String algorithm;
    //key 密码key
    private static final byte[] keyAscii;
    //算法(aes)
    private static final byte[] algorithmAscii;
    private static SecretKeySpec secretKeySpec;

    static {
        keyAscii = new byte[]{(byte) 70, (byte) 17, (byte) 16, (byte) 18, (byte) 81, (byte) 120, (byte) 123, (byte) 16, (byte) 81, (byte) 81, (byte) 1, (byte) 90, (byte) 13, (byte) 68, (byte) 108, (byte) 3};
        algorithmAscii = new byte[]{(byte) 20, (byte) 4, (byte) 22};
        secretKeySpec = null;
        key = "";
        algorithm = "";
    }

    public static SecretKeySpec getSecreteKey() {
        if (secretKeySpec == null) {
            key = decodeAscii(keyAscii);
            algorithm = decodeAscii(algorithmAscii);
            secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
        }
        return secretKeySpec;
    }

    public static String encrypt(String str) {
        String str2 = "";
        try {
            byte[] bytes = str.getBytes();
            Key a = CipherUtils2.getSecreteKey();
            Cipher instance = Cipher.getInstance(CipherUtils2.algorithm);
            instance.init(1, a);
            byte[] doFinal = instance.doFinal(bytes);
            StringBuffer stringBuffer = new StringBuffer(doFinal.length * 2);
            for (int i = 0; i < doFinal.length; i++) {
                stringBuffer.append(new StringBuilder(String.valueOf(Integer.toString((doFinal[i] >> 4) & 15, 16))).append(Integer.toString(doFinal[i] & 15, 16)).toString());
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return str2;
        }
    }

    public static String decrypt(String str) {
        String str2 = "";
        try {
            byte[] bArr;
            if (str.length() <= 0) {
                bArr = null;
            } else {
                bArr = new byte[(str.length() / 2)];
                for (int i = 0; i < str.length() / 2; i++) {
                    bArr[i] = (byte) ((Integer.parseInt(str.substring(i * 2, (i * 2) + 1), 16) * 16) + Integer.parseInt(str.substring((i * 2) + 1, (i * 2) + 2), 16));
                }
            }
            Key a = CipherUtils2.getSecreteKey();
            Cipher instance = Cipher.getInstance(algorithm);
            instance.init(2, a);
            return new String(instance.doFinal(bArr));
        } catch (Exception e) {
            return str2;
        }
    }

    public static String decodeAscii(byte[] bArr) {
        try {
            int length = bArr.length;
            bArr[0] = (byte) (((bArr[0] >> 4) & 15) | ((bArr[0] << 4) & 240));
            for (int i = 1; i < length; i++) {
                bArr[i] = (byte) ((bArr[i] & 255) ^ (bArr[i - 1] & 255));
            }
            return new String(bArr, 0, length);
        } catch (Exception e) {
            Log.w("warnCode", "T\uff1a008:" + e.toString());
            return "";
        }
    }
}
