package com.kssjw.minecarttrainsfork.util;

public class LogUtil {

    private LogUtil() {}

    private static final String HEAD = "[Minecart Trains Fork] ";

    public static void print(String str) {
        System.out.println(HEAD + str);
    }
}