package com.portabull.utils.objectutils;

import com.portabull.utils.fileutils.FileHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

@Component
public class ObjectHandling {

    private ObjectHandling() {
    }

    static Logger logger = LoggerFactory.getLogger(ObjectHandling.class);

    public static File writeObjectIntoFile(Object data, String absolutePath) throws IOException {
        return FileHandling.writeObjectIntoFile(data, absolutePath);
    }

    public static File writeObjectIntoFile(Object data, File file) throws IOException {
        return FileHandling.writeObjectIntoFile(data, file);
    }

    public static <T> T readObjectFromFile(File file) throws IOException, ClassNotFoundException {
        return FileHandling.readObjectFromFile(file);
    }

    public static boolean isNullObject(Object object) {
        return object == null;
    }

    public static boolean isNotNullObject(Object object) {
        return object != null;
    }

    public static boolean isEmptyCollection(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
