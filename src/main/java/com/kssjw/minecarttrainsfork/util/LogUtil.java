package com.kssjw.minecarttrainsfork.util;

public class LogUtil {

    private LogUtil() {}

    private static final String HEAD = "[Minecart Trains Fork] ";

    public static void print(Object obj) {
        System.out.println(HEAD + String.valueOf(obj));
    }
}