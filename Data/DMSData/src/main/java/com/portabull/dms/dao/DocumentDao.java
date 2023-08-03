package com.portabull.dms.dao;

import com.portabull.entitys.Document;
import com.portabull.response.DocumentResponse;

import java.util.List;

public interface DocumentDao {

    public Object getData(Long id);

    public Document createDocument(Document document);

    public Object deleteData(Long id);

    public Document getDocumentByELocation(String eLocation);

    public List<Document> getDocumentsForUser(Integer pageNo, Integer resultSize);

    public Document getDocument(Long documentId);

    DocumentResponse getDmsFiles(Long dirId, boolean fetchRootDirs, String ds);

    public DocumentResponse createDMSDir(String dirName, Integer level, Long parentDirectoryId);

    void mapFileToDirectory(Document document, Long dirId);

    DocumentResponse editDirName(Long dirId, String dirName);

    DocumentResponse deleteDirectory(Long dirId);

    DocumentResponse editFileName(Long fileId, String fileName);

    DocumentResponse deleteFile(Document document);

}
