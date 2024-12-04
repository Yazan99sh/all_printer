/**
* @file Utils.java
* @brief 
* @author Hansen.Z
* @version 1.0
* @date 2019-05-24
*/

package com.example.all_printer;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.regex.Pattern;

public class SmartPosUtils {
	private final static String SP = "config";
	private final static String KEY_NVRAM_SAVED = "nvram_saved";

    public static boolean isHexString(String src) {
        return Pattern.matches("[a-fA-F0-9]+", src);
    }

    public static String printBytes(byte[] raw, int offset, int count) {
        if (raw == null) {
            return null;
        }
        if (offset < 0 || offset > raw.length) {
            offset = 0;
        }
        int end = offset + count;
        if (end > raw.length) {
            end = raw.length;
        }
        StringBuilder hex = new StringBuilder();
        for (int i = offset; i < end; i++) {
            int v = raw[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hex.append(0);
            }
            hex.append(hv);
            hex.append(" ");
        }
        if (hex.length() > 0) {
            hex.deleteCharAt(hex.length() - 1);
        }
        return hex.toString().toUpperCase();
    }


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (byte b : src) {
            buffer[0] = Character.forDigit((b >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(b & 0x0F, 16);
            stringBuilder.append(" ");
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString().toUpperCase();
    }

	public static byte[] stringToBytes(String str) {
		byte[] byteArray;
		if (str == null || str.length() == 0) {
			return null;
		}
		if (str.length() == 1) {
			str = "0"+str;
		}
		byteArray = new byte[str.length() >> 1];
		for (int i = 0; i < byteArray.length; i++){
			String subStr = str.substring(i<<1, (i<<1) + 2);
			byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
		}
		return byteArray;
	}

	public static void setNvramFlag(Context context, boolean saved) {
		SharedPreferences sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
		sp.edit().putBoolean(KEY_NVRAM_SAVED, saved).commit();
	}

	public static boolean getNvramFlag(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_NVRAM_SAVED, false);
	}
}
