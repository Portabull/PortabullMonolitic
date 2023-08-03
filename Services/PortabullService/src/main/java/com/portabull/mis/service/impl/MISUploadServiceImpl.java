package com.portabull.mis.service.impl;

import com.portabull.dms.service.DocumentService;
import com.portabull.mis.service.MISUploadService;
import com.portabull.payloads.MultiPartFileRequest;
import com.portabull.response.DocumentResponse;
import com.portabull.response.FileResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.HomePageUrl;
import com.portabull.utils.MockMultipartFile;
import com.portabull.utils.datautils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;

@Service
public class MISUploadServiceImpl implements MISUploadService {

    @Autowired
    HomePageUrl homePageUrl;

    @Autowired
    DocumentService documentService;

    @Override
    public PortableResponse uploadFileToDMS(FileResponse fileResponse, String fileFormat) throws IOException {
        byte[] fileBytes = FileResponse.getBytes(fileResponse.getInputStream());

        String fileName = StringUtils.append("misReport.", fileFormat.toLowerCase());

        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

        DocumentResponse documentResponse;
        try {
            documentResponse = documentService.uploadDocument(new MockMultipartFile(fileName,
                    fileName, fileTypeMap.getContentType(fileName),
                    fileBytes.length, fileBytes, fileResponse.getInputStream()));
        } catch (Exception e) {
            throw new IOException(e);
        }

        return new PortableResponse(documentResponse.getMessage(), documentResponse.getStatusCode(),
                documentResponse.getStatus(), documentResponse.getData());
    }


}
