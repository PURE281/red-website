/**
 * 工程: Coyote <p>
 * 标题: StringUtil.java <p>
 * 包:   com.niuniucaip.lotto.ui.util.other <p>
 * 描述: TODO <p>
 * 作者: nn <p>
 * 时间: 2014年7月25日 上午10:42:12 <p>
 * 版权: Copyright 2014 Shenzhen NiuNiucaip Tech Co.,Ltd.
 * All rights reserved.
 */

package com.jbb.library_common.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jbb.library_common.comfig.KeyContacts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类: StringUtil <p>
 * 描述: 放一些字符串操作相关的Util <p>
 */
public class StringUtil {



    /**
     * 方法: formatMoney <p>
     * 描述: 格式化金额 格式：0.00 <p>
     */
    public static String formatMoney(String str) {
        if (TextUtils.isEmpty(str)) {
            return "0.00";
        }
        try {
            double value = Double.parseDouble(str);
            String strValue = formatMoney(value);
            return strValue;
        } catch (Exception e) {
            return "0.00";
        }
    }

    /**
     * 方法: formatMoneyFloor <p>
     * 描述: 格式化金额 格式：0.00  舍去后面的位数 <p>
     */
    public static String formatMoneyFloor(double money) {
        BigDecimal b = new BigDecimal(money);
        BigDecimal finalData = b.divide(new BigDecimal(1), 2, BigDecimal.ROUND_FLOOR);
        DecimalFormat format = new DecimalFormat("0.00");
        String strValue = format.format(finalData.doubleValue());
        return strValue;
    }

    /**
     * 方法: formatMoney <p>
     * 描述: 格式化金额 格式：0.00<p>
     */
    public static String formatMoney(double money) {
        BigDecimal b = new BigDecimal(money);
        BigDecimal finalData = b.divide(new BigDecimal(1), 2, BigDecimal.ROUND_HALF_UP);
        DecimalFormat format = new DecimalFormat("0.00");
        String strValue = format.format(finalData.doubleValue());
        return strValue;
    }



    /**
     * 方法: hasSpecific <p>
     * 描述: 判断字符串是否有特殊字符,可以包含中文， false为含有  true为不含<p>
     */
    public static boolean hasSpecificContainChina(String content) {
        String all = "^[\u4e00-\u9fa5a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(all);
        return pattern.matcher(content).matches();

    }

    /**
     * 方法: hasIllegalPwd <p>
     * 描述: 密码规则判断，由数字字母特殊字符组合而成，且不能为纯数字或纯字母<p>
     */
    public static boolean hasIllegalPwd(String content) {
        String all = "^(?![0-9]+$)(?![a-zA-Z]+$).{6,15}$";
        Pattern pattern = Pattern.compile(all);
        return !pattern.matcher(content).matches();

    }

    /**
     * 方法: hasSpecific <p>
     * 描述: 判断字符串是否有特殊字符,不可以包含中文， false为含有  true为不含<p>
     */
    public static boolean hasSpecific(String content) {
        String all = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(all);
        return pattern.matcher(content).matches();

    }

    /**
     * 描述:判断字符中是否有文字和数字
     */
    public static boolean isContainsCharNumber(String content) {
        String all = "[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(all);
        Matcher matcher = pattern.matcher(content);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg;

    }

    /**
     * 描述:判断email格式是否正确
     */
    public static boolean judgeEmail(String content) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(content);
        if (!matcher.matches()) {
            return false;
        }
        return true;
    }





    /**
     * 方法: getCharCount <p>
     * 描述: 返回某个字符串中某个字符的出现次数 <p>
     */
    public static int getCharCount(String src, char ch) {
        int count = 0;
        if (TextUtils.isEmpty(src)) {
            return count;
        }
        byte[] temp = src.getBytes();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == ch) {
                count++;
            }
        }
        return count;
    }

    /**
     * 方法: getShowStr <p>
     * 描述: 根据明文混淆成显示的文字 <p>
     */
    public static String getShowStr(String src, int type) {
        String str = "";
        if (TextUtils.isEmpty(src) || src.length() <= 1) {
            return str;
        }
        switch (type) {
            case 1: //姓名
                //手机号
                String all = "[0-9]{11}$";
                Pattern pattern = Pattern.compile(all);
                boolean isMobile = pattern.matcher(src).matches();
                if (isMobile) {
                    str = src.substring(0, 3) + "****" + src.substring(src.length() - 4, src.length());
                    break;
                }
                //邮箱
                if (judgeEmail(src)) {
                    str = src.substring(0, 3) + "***" + src.substring(src.lastIndexOf("."), src.length());
                    break;
                }
                //2-4位
                if (src.length() == 2) {
                    str = src.substring(0, 1) + "*";
                    break;
                }
                if (src.length() == 3) {
                    str = src.substring(0, 1) + "*" + src.substring(src.length() - 1, src.length());
                    break;
                }
                if (src.length() == 4) {
                    str = src.substring(0, 1) + "**" + src.substring(src.length() - 1, src.length());
                    break;
                }
                if (src.length() == 5) {
                    str = src.substring(0, 1) + "***" + src.substring(src.length() - 1, src.length());
                    break;
                }
                //其他情况
                str = src.substring(0, 1) + "***" + src.substring(src.length() - 2, src.length());
                break;
            case 2: //身份证号码
                if (src.length() < 15) {
                    return str;
                }
                str = src.substring(0, 3) + "*********" + src.substring(src.length() - 3, src.length());
                break;
            case 3: //手机号
                if (src.length() < 11) {
                    return str;
                }
                str = "*******" + src.substring(src.length() - 4, src.length());
                break;
            case 4: //银行卡
                if (src.length() < 5) {
                    return str;
                }
                str = "********" + src.substring(src.length() - 4, src.length());
                break;
            case 5: //邮箱
                if (src.length() < 3 || !src.contains("@")) {
                    return src;
                }
                str = src.substring(0, 3) + "***" + src.substring(src.indexOf("@"), src.length());
                break;

        }
        return str;
    }



//	/******************************************分割线**************************/

    /**
     * 描述：根据密码获取密码强度
     */
    public static String getPwdLevel(String pwd) {
        if (pwd == null || pwd.length() == 0) {
            return "0";
        }
        String level = "0";
        int pwdLength = pwd.length();
        int maths, bigs, smalls, sum;
        maths = bigs = smalls = sum = 0;
        if (Pattern.compile("(?i)[0-9]").matcher(pwd).find()) {
            maths = 1;
        }
        if (Pattern.compile("(?i)[a-z]").matcher(pwd).find()) {
            smalls = 1;
        }
        if (Pattern.compile("(?i)[A-Z]").matcher(pwd).find()) {
            bigs = 1;
        }


        sum = maths + smalls + bigs;
        if (pwdLength < 6) {
            level = "1";
        }

        if (pwdLength >= 6 && pwdLength <= 8) {
            switch (sum) {
                case 1:
                    level = "1";
                    break;
                case 2:
                case 3:
                    level = "2";
                    break;
                case 4:
                    level = "3";
                    break;
            }
        }

        if (pwdLength > 8 && pwdLength <= 11) {
            switch (sum) {
                case 1:
                    level = "2";
                    break;
                case 2:
                    level = "3";
                    break;
                case 3:
                    level = "4";
                    break;
                case 4:
                    level = "5";
                    break;
            }
        }

        if (pwdLength > 11) {
            switch (sum) {
                case 1:
                    level = "3";
                    break;
                case 2:
                    level = "4";
                    break;
                case 3:
                case 4:
                    level = "5";
                    break;
            }
        }
        return level;
    }


    /**
     * 描述:根据byte数组获取String  替代之前EncodingUtils里的getString方法
     */
    public static String getString(final byte[] data, final String charset) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        return getString(data, 0, data.length, charset);
    }

    /**
     *描述:EncodingUtils里的getString方法
     */
    public static String getString(final byte[] data, int offset, int length, String charset
    ) {

        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }

        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }

        try {
            return new String(data, offset, length, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(data, offset, length);
        }
    }
    /**
     * 描述：获取string中字符个数(规则：一个汉字相当于2个字符，字母数字等相当于1个字符)
     */
    public static int getCharNum(String string){
        int num = 0;
        char[] chars = string.toCharArray();
        for(int i=0;i<chars.length;i++){
            if(isChinese(chars[i])){
                num += 2;
            }else{
                num += 1;
            }
        }
        return num;
    }

    /**
     * 描述：判断字符是否是汉字
     * 参数：
     * 返回：
     * 作者：nn
     * 时间：2016/6/15 17:48
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }








    public static String getBankCode(String bankCode){
        if(TextUtils.isEmpty(bankCode))
            return "";
        else{
            if(bankCode.length() >=15){
                return "****" + "  ****" + "  ****  " + bankCode.substring(bankCode.length() - 4, bankCode.length());
            }else {
                return "*******" + bankCode.substring(bankCode.length() - 4, bankCode.length());
            }
        }

    }

    /**
     * 是否纯数字
     * @param str
     * @return
     */
    public static boolean isDigit(String str){
        boolean b = true;
        for (int i = 0; i < str.length(); i++){
            if (!Character.isDigit(str.charAt(i))){
                b = false;
            }
        }
        return b;
    }



    public static boolean isMobile(String mobile){
        String pattern = "^1[\\d]{10}";
        boolean isMatch = Pattern.matches(pattern, mobile);
        return isMatch;
    }
}
