package com.portabull.dms.documentstoragemodule;

import com.portabull.response.DocumentResponse;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentStorageModule {

    public DocumentResponse uploadDocument(InputStream file, String fileName) throws IOException;

    public DocumentResponse uploadDocument(InputStream file, String folderName, String fileName) throws IOException;

    public DocumentResponse downloadDocument(String eLocation);

    public DocumentResponse deleteDocument(String eLocation);

    public DocumentResponse transferDocument();

    public DocumentResponse downloadDocumentBytes(String eLocation);

}
