package com.fenghuo.utils.hash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by Administrator on 2016/5/28.
 */
public class HashUtils {

    public static String getSha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes());

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMd5(byte[] buffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSha256(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    private static final boolean DEBUG = false;
    private static final String TAG = "SmsUtils";

    public static PublicKey getPublicKey(Context context) {
        if (DEBUG) {
            Log.d(TAG, "getPublicKey: start");
        }
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (pkgInfo.signatures != null && pkgInfo.signatures.length > 0) {
                byte[] cert = pkgInfo.signatures[0].toByteArray();
                if (DEBUG) {
                    Log.d(TAG, "getPublicKey: signatures:" + cert.length);
                }
                InputStream input = new ByteArrayInputStream(cert);
                CertificateFactory cf = CertificateFactory.getInstance("X509");
                X509Certificate c = (X509Certificate) cf.generateCertificate(input);
                return c.getPublicKey();
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "getPublicKey: signatures ERRO :", e);
            }
        }
        if (DEBUG) {
            Log.e(TAG, "getPublicKey: signatures null(debug)");
        }
        return null;
    }

    public static String parse(Context context, byte[] data) {
        try {
            if (DEBUG) {
                Log.v("BlackDownload", "parse ...");
            }
//			KeyStore keyStore = CertificateCoder.getKeyStore("debug.keystore", "android");
//			PrivateKey privateKey = (PrivateKey) keyStore.getKey("androiddebugkey", "android".toCharArray());
//			PublicKey publicKey = keyStore.getCertificate("androiddebugkey").getPublicKey();

            PublicKey publicKey = getPublicKey(context);
            if (DEBUG) {
                Log.d(TAG, "parse: PublicKey :" + publicKey);
            }
            byte[] headerLength = new byte[4];
            System.arraycopy(data, 0, headerLength, 0, 4);
            int iKeyLength = byteToInt2(headerLength);

            byte[] keyHeader = new byte[iKeyLength];
            System.arraycopy(data, 4, keyHeader, 0, iKeyLength);

            Cipher cipherRSADecrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSADecrypt.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] keyHeaderAfterRSA = cipherRSADecrypt.doFinal(keyHeader);

            byte[] content = new byte[data.length - iKeyLength - 4];
            System.arraycopy(data, iKeyLength + 4, content, 0, data.length - iKeyLength - 4);
            byte[] dataAfterDesDecrypt = desDecrypt(content, keyHeaderAfterRSA);
            String result = new String(dataAfterDesDecrypt);
            return result;
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "", e);
            }
            return null;
        }
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    public static int byteToInt2(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < 4; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8) | (src[offset + 3] & 0xFF));
        return value;
    }

    private static byte[] desEncrypt(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(
                new DESKeySpec(key));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
        return cipher.doFinal(data);
    }

    private static byte[] desDecrypt(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(
                new DESKeySpec(key));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new SecureRandom());
        return cipher.doFinal(data);
    }

    public static String genRandomKey() {
        String key = String.valueOf(System.currentTimeMillis());
        return key.substring(key.length() - 8, key.length());
    }

    private static void write2File(File path, byte[] content)
            throws IOException {
        FileOutputStream out = new FileOutputStream(path);
        out.write(content);
        out.close();
    }

    public static byte[] getFileContent(File path) throws IOException {
        FileInputStream in = new FileInputStream(path);
        byte[] b = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        while ((len = in.read(b)) > 0) {
            baos.write(b, 0, len);
        }
        in.close();
        return baos.toByteArray();

    }
}
