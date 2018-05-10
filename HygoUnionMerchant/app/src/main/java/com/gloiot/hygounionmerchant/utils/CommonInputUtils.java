package com.gloiot.hygounionmerchant.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 输入工具类
 * Created by Dlt on 2017/7/26 16:28
 */
public class CommonInputUtils {

    /**
     * 格式化字符串(四舍五入保留两位小数)
     */
    public static String formattingString(String orginalString) {

        double d = Double.parseDouble(orginalString);
        String result = String.format("%.2f", d);// %.2f %. 表示 小数点前任意位数 2 表示两位小数 格式后的结果为f 表示浮点型。

        return result;
    }

    /**
     * 过滤空格
     *
     * @param editText
     */
    public static void filterBlank(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };

        editText.setFilters(new InputFilter[]{filter});

    }

    /**
     * 过滤空格和换行符
     *
     * @param editText
     */
    public static void filterBlankAndLineBreak(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
                if (source.equals(" ") || source.toString().contentEquals("\n")) {
                    return "";
                } else {
                    return null;
                }
            }
        };

        editText.setFilters(new InputFilter[]{filter});

    }

    /**
     * 格式化输入时金额字符（四舍五入保留两位小数）
     *
     * @param editText
     */
    public static void formattingInputMoneyString(final EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //小数点后位数控制
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {//可以适当输入多位，位数会在最后计算时四舍五入
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);//小数点后最多输入2位
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                //第一位为点
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                //第一位为零，后面只能输入点
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
//                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 输入内容是否符合正则条件
     *
     * @param inputString 输入的字符串
     * @param regexString 正则条件
     * @return 匹配返回true, 不匹配返回false
     */
    public static boolean isAccordWithRegex(String inputString, String regexString) {
        Pattern p = Pattern.compile(regexString);
        Matcher m = p.matcher(inputString);
        return m.matches();
    }


}
