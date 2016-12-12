package com.fenghuo.utils.hash;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class CipherUtils {
    public CipherUtils() {
    }

    protected static String DES_decrypt(String encrypted, String key) {
        byte[] res = DES_decrypt(encrypted.getBytes(), key);
        return res == null ? "" : new String(res);
    }

    public static byte[] DES_decrypt(byte[] data, String key) {
        try {
            SecureRandom e = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(2, secretKey, e);
            byte[] decryptedData = cipher.doFinal(data);
            return decryptedData;
        } catch (Exception var8) {
            var8.printStackTrace();
            return null;
        }
    }

    public static byte[] DES_encrypt(String src, String key) {
        try {
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(1, secretKey, sr);
            return cipher.doFinal(src.getBytes());
        } catch (Throwable var7) {
            return null;
        }
    }

    public static String getDesKey(int func) {
        if (81 == func) {
            byte[] DES_KEY = new byte[]{(byte) 97, (byte) 35, (byte) 112, (byte) 102, (byte) 117, (byte) 94, (byte) 115, (byte) 38};
            int i = DES_KEY[3] >> 4 & 15;
            int j = DES_KEY[3] & 15;
            DES_KEY[3] = (byte) (i * j);
            String res = new String(DES_KEY);
            return res;
        } else {
            return "";
        }
    }

    public static String encodeBase64(String str) {
        try {
            return new String(Base64.encode(str.getBytes(), 2));
        } catch (Exception var2) {
            return "";
        }
    }

    public static String encodeBase64(byte[] data) {
        try {
            return new String(Base64.encode(data, 2));
        } catch (Exception var2) {
            return "";
        }
    }
}