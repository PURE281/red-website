/**   
 * @Title: MD5.java 
 * @Package com.niuniucaip.lotto.util.md5
 * @Description: (执行md5加密) 
 * @author nn
 * @date 2011-1-19 上午11:33:38  
 */
package com.jbb.library_common.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用于执行Md5加密
 * 
 * @ClassName: MD5
 * @Description: 用于执行Md5加密
 * @author nn
 * @date 2011-1-19 上午11:33:38
 * 
 */
public class MD5Util {

	/**
	 * md5加密
	 * 
	 * @Title: md5
	 * @Description: md5加密
	 * @param s
	 *            需要加密的字符串
	 * @return String 加密后的字符串
	 */
	public static String md5(String s) {
	    if (TextUtils.isEmpty(s)){
	        return "";
        }
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create HEX String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String shex = Integer.toHexString(0xFF & messageDigest[i]);
				if (shex.length() < 2) {
					shex = "0" + shex;
				}
				hexString.append(shex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}


    /**
     * md5加密
     * @Title: 百度校验md5
     * @Description: 百度校验md5加密
     * @param s 需要加密的字符串
     * @return String 加密后的字符串
     */
    public static String toBaiduMD5(String s) {
        if (s != null) {
            try {
                byte[] bs = s.getBytes("GBK");
                return encrypt(bs);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * md5 encrypt
     * @Title: 百度校验md5  encrypt
     * @Description: 百度校验md5 encrypt
     * @param obj 需要加密的字符串生成的字节码
     * @return String encrypt后的字符串
     */
    private synchronized static String encrypt(byte[] obj) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(obj);
            byte[] bs = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bs.length; i++) {
                sb.append(Integer.toHexString((0x000000ff & bs[i]) | 0xffffff00).substring(6));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
