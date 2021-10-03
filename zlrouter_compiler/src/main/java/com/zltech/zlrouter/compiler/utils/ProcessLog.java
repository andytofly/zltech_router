package com.zltech.zlrouter.compiler.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;


public class ProcessLog {
    private Messager messager;

    private ProcessLog(Messager messager) {
        this.messager = messager;
    }

    public static ProcessLog newLog(Messager messager) {
        return new ProcessLog(messager);
    }

    public void i(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, "\n **Compiler PrintMessage:"+msg+"-------");
    }
}
