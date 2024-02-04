package com.portabull.genericservice.jobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DynamicClassLoader extends ClassLoader {

    @Override
    public Class<?> findClass(String path) throws ClassNotFoundException {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            return defineClass(null, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(path, e);
        }
    }
}
