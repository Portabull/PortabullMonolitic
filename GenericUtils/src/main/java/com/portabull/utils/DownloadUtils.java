package com.portabull.utils;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class DownloadUtils {

    private DownloadUtils() {

    }

    public static ResponseEntity<InputStreamResource> download(String fileName,
                                                               MediaType mediaType,
                                                               InputStreamResource inputStreamResource) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(mediaType).body(inputStreamResource);
    }
}
