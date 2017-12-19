package com.github.siilas.notagenerator.log;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class Logger {

    private static final String PREFIX = "[NotaGenerator] ";

    private Logger() {
    }

    public static void error(String mensagem) {
        System.out.println(PREFIX + mensagem);
    }

    public static void error(String mensagem, Exception erro) {
        System.out.println(PREFIX + mensagem + " - " + ExceptionUtils.getStackTrace(erro));
    }

    public static void info(String mensagem) {
        System.out.println(PREFIX + mensagem);
    }

}
