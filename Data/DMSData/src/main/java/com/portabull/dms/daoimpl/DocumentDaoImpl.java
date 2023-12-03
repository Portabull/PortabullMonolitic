package com.portabull.dms.daoimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.dms.dao.DocumentDao;
import com.portabull.entitys.Document;
import com.portabull.entitys.UserDirectory;
import com.portabull.entitys.UserDocumentDirectoryMapping;
import com.portabull.execption.BadRequestException;
import com.portabull.execption.UnAuthorizedException;
import com.portabull.response.DocumentResponse;
import com.portabull.utils.commonutils.CommonUtils;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Repository
public class DocumentDaoImpl implements DocumentDao {

    @Autowired
    HibernateUtils hibernateUtils;

    private static final String PARENT_DIR = "parentDirId";

    static Logger logger = LoggerFactory.getLogger(DocumentDaoImpl.class);

    @Override
    public Object getData(Long id) {
        try {
            return hibernateUtils.findEntity(Document.class, id);
        } catch (Exception e) {
            logger.error("Error in getEntity", e);
        }
        return null;
    }

    @Override
    public Document createDocument(Document document) {
        try {
            return hibernateUtils.saveEntity(document);
        } catch (Exception e) {
            logger.error("Error in saveEntity", e);
        }
        return null;
    }

    @Override
    public Object deleteData(Long id) {
        Transaction tx = null;
        try (Session session = hibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.createQuery("DELETE FROM Document where id =:id").setParameter("id", id).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            logger.error("Error in deleteEntity", e);
            if (tx != null) {
                tx.rollback();
            }
        }
        return null;
    }


    @Override
    public Document getDocumentByELocation(String eLocation) {
        try (Session session = hibernateUtils.getSession()) {
            return (Document) session.createQuery(" FROM Document where eLocation=:eLocation").setParameter("eLocation", eLocation).uniqueResult();
        }
    }

    @Override
    public List<Document> getDocumentsForUser(Integer pageNo, Integer resultSize) {
        try (Session session = hibernateUtils.getSession()) {
            return session.createQuery(" FROM Document where userID=:userID ORDER BY uploadedDate DESC").setFirstResult(pageNo).setMaxResults(resultSize).setParameter("userID", CommonUtils.getLoggedInUserId()).list();
        }
    }

    @Override
    public Document getDocument(Long documentId) {
        return hibernateUtils.findEntity(Document.class, documentId);
    }

    @Override
    public DocumentResponse getDmsFiles(Long dirId, boolean fetchRootDirs, String ds) {

        DocumentResponse response = new DocumentResponse();

        Long userID = CommonUtils.getLoggedInUserId();

        if (fetchRootDirs) {

            prepareRootDirs(response, userID);

            return response;

        }


        Map<String, Object> currentDirectories = new LinkedHashMap<>();

        List<Object> currentDirectoryFiles = new ArrayList<>();

        List<Object> data = new ArrayList<>();

        try (Session session = hibernateUtils.getSession()) {

            Long parentDirId = (Long) session.createQuery("SELECT parentDirectory.id FROM UserDirectory WHERE id=:id and userID=:userID").setParameter("id", dirId).setParameter("userID", userID).uniqueResult();

            currentDirectories.put(PARENT_DIR, parentDirId == null ? "root" : parentDirId);

            if (dirId == null) {

                prepareRootDirs(response, userID);

                return response;

            }

            UserDirectory currentDirectory = (UserDirectory) session.createQuery(" FROM UserDirectory WHERE id=:dirId and userID=:userID").setParameter("dirId", dirId).setParameter("userID", userID).uniqueResult();

            List<UserDirectory> directories = session.createQuery(" FROM UserDirectory WHERE parentDirectory.id=:dirId and userID=:userID and (isDeleted=false or isDeleted is null)").setParameter("userID", userID).setParameter("dirId", dirId).list();

            if (currentDirectory != null) {


                List<UserDocumentDirectoryMapping> dirMapping = session.createQuery(" FROM UserDocumentDirectoryMapping WHERE userDirectory.id=:dirId").setParameter("dirId", currentDirectory.getId()).list();

                if (!CollectionUtils.isEmpty(dirMapping)) {

                    for (UserDocumentDirectoryMapping userDocumentDirectoryMapping : dirMapping) {

                        Document document = session.get(Document.class, userDocumentDirectoryMapping.getDocumentId());

                        if (BooleanUtils.isTrue(document.getDeleted())) continue;

                        Map<String, Object> file = new LinkedHashMap<>();

                        file.put("fileId", userDocumentDirectoryMapping.getDocumentId());

                        file.put("fileName", document.getName());

                        currentDirectoryFiles.add(file);
                    }

                }


            }

            if (!CollectionUtils.isEmpty(directories)) {

                directories.forEach(directory -> {

                    Map<String, Object> entry = new LinkedHashMap<>();

                    entry.put("dirId", directory.getId());

                    entry.put("dirName", directory.getDirectoryName());

                    data.add(entry);

                });

            }
        }

        currentDirectories.put("dirs", data);

        currentDirectories.put("currentFiles", currentDirectoryFiles);

        response.setData(currentDirectories);

        return response;
    }

    private void prepareRootDirs(DocumentResponse response, Long userID) {

        Map<String, Object> directoryResponse = new LinkedHashMap<>();

        List<Object> dirs = new ArrayList<>();

        List<UserDirectory> userDirectories;

        try (Session session = hibernateUtils.getSession()) {

            userDirectories = session.createQuery(" FROM UserDirectory WHERE parentDirectory IS NULL AND rootLevel=true AND userID=:userID").setParameter("userID", userID).list();

        }

        if (CollectionUtils.isEmpty(userDirectories)) {
            UserDirectory userDirectory = new UserDirectory();
            userDirectory.setDirCreatedDate(new Date());
            userDirectory.setDirLevel(0);
            userDirectory.setDirectoryName("root");
            userDirectory.setRootLevel(true);
            userDirectory.setParentDirectory(null);
            userDirectory.setUserID(userID);
            hibernateUtils.saveOrUpdateEntity(userDirectory);
            userDirectories.add(userDirectory);
        }


        for (UserDirectory userDirectory : userDirectories) {

            Map<String, Object> eachDir = new LinkedHashMap<>();

            eachDir.put("dirId", userDirectory.getId());

            eachDir.put("dirName", userDirectory.getDirectoryName());

            dirs.add(eachDir);
        }


        directoryResponse.put("dirs", dirs);

        directoryResponse.put(PARENT_DIR, null);

        response.setData(directoryResponse);

    }

    @Override
    public DocumentResponse createDMSDir(String dirName, Integer level, Long parentDirectoryId) {

        try (Session session = hibernateUtils.getSession()) {
            Number dirCount = (Number) session.createQuery("SELECT count(*) FROM UserDirectory WHERE parentDirectory.id=:id AND directoryName=:directoryName").setParameter("directoryName", dirName).setParameter("id", parentDirectoryId).uniqueResult();

            if (dirCount != null && dirCount.intValue() != 0) {
                throw new BadRequestException("Already Dir Present Not Allowed Duplicate Directory");
            }
        }

        DocumentResponse response = new DocumentResponse();

        UserDirectory userDirectory = new UserDirectory();

        UserDirectory parentDirectory = new UserDirectory();

        parentDirectory.setId(parentDirectoryId);

        userDirectory.setParentDirectory(parentDirectory);

        userDirectory.setDirCreatedDate(new Date());

        userDirectory.setRootLevel(false);

        userDirectory.setUserID(CommonUtils.getLoggedInUserId());

        userDirectory.setDirectoryName(dirName);

        userDirectory.setDirLevel(level);

        hibernateUtils.saveOrUpdateEntity(userDirectory);

        response.setData(userDirectory.getId());

        return response;
    }

    @Override
    public void mapFileToDirectory(Document document, Long dirId) {

        UserDirectory userDirectory = new UserDirectory();

        userDirectory.setId(dirId);

        UserDocumentDirectoryMapping mapping = new UserDocumentDirectoryMapping();

        mapping.setDocumentId(document.getId());

        mapping.setUserDirectory(userDirectory);

        hibernateUtils.saveOrUpdateEntity(mapping);

    }

    @Override
    public DocumentResponse editDirName(Long dirId, String dirName) {

        UserDirectory userDirectory = hibernateUtils.findEntity(UserDirectory.class, dirId);

        if (!userDirectory.getUserID().equals(CommonUtils.getLoggedInUserId())) {
            throw new UnAuthorizedException("This directory is not belongs to you");
        }

        userDirectory.setDirectoryName(dirName);

        hibernateUtils.saveOrUpdateEntity(userDirectory);

        return new DocumentResponse("Directory name Edited Successfully", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public DocumentResponse deleteDirectory(Long dirId) {

        UserDirectory userDirectory = hibernateUtils.findEntity(UserDirectory.class, dirId);

        if (!userDirectory.getUserID().equals(CommonUtils.getLoggedInUserId())) {
            throw new UnAuthorizedException("This directory is not belongs to you");
        }

        try (Session session = hibernateUtils.getSession()) {

            List<Long> documentsIds = session.createQuery(" SELECT documentId FROM UserDocumentDirectoryMapping WHERE userDirectory.id=:id").setParameter("id", dirId).list();

            Transaction transaction = session.beginTransaction();

            session.createQuery("UPDATE Document SET isDeleted=true WHERE id IN (:ids)").setParameter("ids", documentsIds).executeUpdate();

            transaction.commit();

        }

        userDirectory.setDeleted(true);

        hibernateUtils.saveOrUpdateEntity(userDirectory);

        return new DocumentResponse("File Deleted Successfully", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public DocumentResponse editFileName(Long fileId, String fileName) {

        Document document = hibernateUtils.findEntity(Document.class, fileId);

        if (!document.getUserID().equals(CommonUtils.getLoggedInUserId())) {
            throw new UnAuthorizedException("This File is not belongs to you");
        }

        document.setName(fileName);

        hibernateUtils.saveOrUpdateEntity(document);

        return new DocumentResponse("File name Edited Successfully", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public DocumentResponse deleteFile(Document document) {

        document.setDeleted(true);

        hibernateUtils.saveOrUpdateEntity(document);

        return new DocumentResponse("File Deleted Successfully", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public List<UserDirectory> getUserFolders(Long folderId) {

        List<UserDirectory> userFolders = new ArrayList<>();

        folderStructure(userFolders, folderId);

        return userFolders;
    }

    @Override
    public List<UserDocumentDirectoryMapping> getDirMapping(List<UserDirectory> userFolders) {

        try (Session session = hibernateUtils.getSession()) {

            return session.createQuery(" FROM UserDocumentDirectoryMapping WHERE userDirectory IN (:userFolders)").setParameter("userFolders", userFolders).list();

        }

    }


    public void folderStructure(List<UserDirectory> userDirs, Long folderId) {
        List<UserDirectory> dirs;
        try (Session session = hibernateUtils.getSession()) {
            dirs = session.createQuery(" FROM UserDirectory WHERE parentDirectory.id =: dirId").setParameter("dirId", folderId).list();
            for (UserDirectory dir : dirs) {
                userDirs.add(dir);
                folderStructure(userDirs, dir.getId());
            }
        }
    }


}
