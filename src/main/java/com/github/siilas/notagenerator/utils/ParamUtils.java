package com.github.siilas.notagenerator.utils;

public class ParamUtils {

    public static String get(String[] array, int index) {
        try {
            return array[index];
        } catch (Exception e) {
            return "";
        }
    }

}
