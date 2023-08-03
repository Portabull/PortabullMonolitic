package com.portabull.mis.service;

import com.portabull.response.FileResponse;
import com.portabull.response.PortableResponse;

import java.io.IOException;

public interface MISUploadService {

    public PortableResponse uploadFileToDMS(FileResponse fileResponse, String fileFormat) throws IOException;

}
