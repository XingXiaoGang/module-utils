package com.fenghuo.utils.hash;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by gang on 16-6-4.
 */
public class CipherUtil {

    private static KeyGenerator keyGenerator;
    private static SecureRandom secureRandom;
    private static SecretKey secretKey;
    private static Cipher cipher;

    static {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    private static boolean prepare(byte[] seed) {
        if (secureRandom == null || secretKey == null) {
            try {
                secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(seed);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return false;
            }
            keyGenerator.init(128, secureRandom);
            secretKey = keyGenerator.generateKey();
        }
        return true;
    }

    /**
     * @param seed      key
     * @param cleartext 明文
     */
    public static String encrypt(String seed, String cleartext) {
        byte[] rawKey = seed.getBytes();
        if (prepare(rawKey)) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] enc = cleartext.getBytes();
                byte[] result = cipher.doFinal(enc);
                return parseByte2HexStr(result);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
        return cleartext;
    }

    /**
     * @param seed      key
     * @param encrypted 密文
     */
    public static String decrypt(String seed, String encrypted) {
        byte[] rawKey = seed.getBytes();
        if (prepare(rawKey)) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] enc = parseHexStr2Byte(encrypted);
                byte[] result = cipher.doFinal(enc);
                return new String(result);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
        return encrypted;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}
