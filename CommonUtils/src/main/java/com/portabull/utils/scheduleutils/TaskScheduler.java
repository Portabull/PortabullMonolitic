package com.portabull.utils.scheduleutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//@Component
public class TaskScheduler {

    private TaskScheduler() {
    }


    static Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    public static void deleteTempFilesOnSchedule(File file, Date date) {
        schedule(new TimerTask() {
            @Override
            public void run() {
                logDeletedFile(file);
            }
        }, date);
    }


    public static String logDeletedFile(File file) {
        String logMessage = new StringBuffer("File Deleted: ")
                .append(file.delete())
                .append(", File Name: ")
                .append(file.getName())
                .append(", File AbsolutePath: ")
                .append(file.getAbsolutePath()).toString();
        logger.info(logMessage);
        return logMessage;
    }

    public static void schedule(TimerTask timerTask, Date date) {
        new Timer().schedule(timerTask, date);
    }

}
