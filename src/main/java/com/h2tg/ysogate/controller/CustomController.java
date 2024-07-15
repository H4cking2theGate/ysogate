package com.h2tg.ysogate.controller;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import com.h2tg.ysogate.config.JndiConfig;
import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;

import java.io.File;

@JNDIController
public class CustomController implements Controller {
    public Object process(String args) throws Exception {
        File file = new File(JndiConfig.file);
        String rootPath = file.getParentFile() != null ? file.getParentFile().getCanonicalPath() : new File("").getCanonicalPath();
        String fileName = file.getName();
        GroovyScriptEngine engine = new GroovyScriptEngine(rootPath);
        Binding binding = new Binding();
        binding.setVariable("args", args);
        Object result = engine.run(fileName, binding);
        return result;
    }

    @JNDIMapping("/Custom/{args}")
    public String Custom(String args) {
        System.out.println("[Custom] File: " + JndiConfig.file + " Args: " + args);
        return args;
    }
}
