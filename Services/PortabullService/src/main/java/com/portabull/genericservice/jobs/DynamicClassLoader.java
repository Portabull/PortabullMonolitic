package com.portabull.genericservice.jobs;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DynamicClassLoader extends URLClassLoader {

    public DynamicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public DynamicClassLoader(URL[] urls) {
        super(urls);
    }

    public DynamicClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    @Override
    public Class<?> findClass(String path) throws ClassNotFoundException {
        try {
            System.out.println("Path : " + path);
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            return defineClass(null, bytes, 0, bytes.length);
        } catch (IOException e) {
//            e.printStackTrace();
            return super.findClass(path);
        }
    }
}
