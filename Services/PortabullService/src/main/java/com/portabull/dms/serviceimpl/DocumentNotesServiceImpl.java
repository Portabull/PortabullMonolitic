package com.portabull.dms.serviceimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dms.service.DocumentNotesService;
import com.portabull.dms.utils.DMSUtils;
import com.portabull.entitys.DocumentNotes;
import com.portabull.execption.BadRequestException;
import com.portabull.generic.dao.CommonDao;
import com.portabull.generic.models.SharedNotesSettings;
import com.portabull.generic.models.UserNoteSettings;
import com.portabull.hibernate.constants.HibernateQueryConstant;
import com.portabull.hibernate.vo.StickyNotesVO;
import com.portabull.payloads.KeyValueMapping;
import com.portabull.response.DocumentResponse;
import com.portabull.response.FileResponse;
import com.portabull.response.PortableResponse;
import com.portabull.um.services.UserProfileService;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.dateutils.DateUtils;
import com.portabull.utils.fileutils.FileHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentNotesServiceImpl implements DocumentNotesService {


    @Autowired
    DMSUtils dmsUtils;

    @Autowired
    CommonDao commonDao;


    @Autowired
    UserProfileService userProfileService;

    @Override
    public PortableResponse updateNotes(String noteId, String tittle, String note, boolean delete) {

        DocumentNotes documentNotes;

        Date date = new Date();

        if (!StringUtils.isEmpty(noteId)) {

            documentNotes = commonDao.findEntity(DocumentNotes.class, Long.valueOf(noteId));

            if (documentNotes == null) {
                throw new BadRequestException("Notes not found");
            }

            if (!documentNotes.getUserId().equals(CommonUtils.getLoggedInUserId())) {

                List<SharedNotesSettings> shareSettings = commonDao.findEntitiesByCriteria(
                        SharedNotesSettings.class, "noteId", documentNotes.getNoteId(),
                        "sharedTO", CommonUtils.getLoggedInUserId());

                if (CollectionUtils.isEmpty(shareSettings))
                    throw new BadRequestException("You cannot modify/delete this note");

                //delete option
                if (delete && (shareSettings.get(0).getNotesDelete() == null || !shareSettings.get(0).getNotesDelete())) {
                    throw new BadRequestException("You Cannot Delete this note, if required ask permission to note owner");
                }

                //edit option
                if (!delete && (shareSettings.get(0).getNotesEditable() == null || !shareSettings.get(0).getNotesEditable())) {
                    throw new BadRequestException("You Cannot Edit this note, if required ask permission to note owner");
                }

            }

        } else {

            documentNotes = new DocumentNotes();

            documentNotes.setCreatedDate(date);

            documentNotes.setNoteHidden(false);

            documentNotes.setUserId(CommonUtils.getLoggedInUserId());

        }

        if (delete) {
            commonDao.deleteEntity(DocumentNotes.class, Long.valueOf(noteId));
            return new PortableResponse("Notes deleted successfully", StatusCodes.C_200, PortableConstants.SUCCESS,
                    null);
        }

        documentNotes.setNotes(note);

        documentNotes.setTittle(tittle);

        documentNotes.setUpdatedDate(date);

        commonDao.saveOrUpdateEntity(documentNotes);

        return new PortableResponse("Notes created successfully", StatusCodes.C_200, PortableConstants.SUCCESS,
                documentNotes.getNoteId());
    }

    @Override
    public DocumentResponse downloadNotes(Long noteId) throws IOException {


        DocumentNotes documentNotes = commonDao.findEntity(DocumentNotes.class, Long.valueOf(noteId));

        Long loggedInUserId = CommonUtils.getLoggedInUserId();

        if (documentNotes == null) {
            throw new BadRequestException("No Document Found");
        }

        if (!documentNotes.getUserId().equals(loggedInUserId)) {

            List<SharedNotesSettings> sharedNotesSettings = commonDao.findEntitiesByCriteria(SharedNotesSettings.class,
                    "noteId", documentNotes.getNoteId(), "sharedTO", loggedInUserId);

            if (CollectionUtils.isEmpty(sharedNotesSettings)) {
                throw new BadRequestException("This document is not belongs to you");
            }

            if (sharedNotesSettings.get(0).getNotesDownloadable() == null || !sharedNotesSettings.get(0).getNotesDownloadable()) {
                throw new BadRequestException("You don't have download option, ask notes owner to give download access");
            }

        }

        String data = "Title : \n\t" + documentNotes.getTittle() + "\nNotes : \n\t" + documentNotes.getNotes()
                + "\nCreated Date : \n\t" + DateUtils.formatDate(DateUtils.PORTABLE_DEFAULT_DATE, documentNotes.getCreatedDate())
                + "\nUpdated Date : \n\t" + DateUtils.formatDate(DateUtils.PORTABLE_DEFAULT_DATE, documentNotes.getUpdatedDate());

        File tempFile = FileHandling.createTempFile(documentNotes.getTittle() + ".txt");

        try (FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write(data);
        }

        InputStream inputStream = FileHandling.getInputStream(tempFile);

        tempFile.delete();

        return new DocumentResponse().
                setFileResponse(new FileResponse().
                        setInputStream(inputStream).
                        setFileName(documentNotes.getTittle() + ".txt"))
                .setStatus(PortableConstants.SUCCESS).setStatusCode(StatusCodes.C_200);
    }

    @Override
    public PortableResponse enableDisableShareNotes(Boolean enableDisableShareNotes) {

        UserNoteSettings userNoteSettings = commonDao.findEntity(UserNoteSettings.class, CommonUtils.getLoggedInUserId());

        if (userNoteSettings == null) {
            userNoteSettings = new UserNoteSettings();
        }

        userNoteSettings.setEnableShareNotes(enableDisableShareNotes);

        commonDao.saveOrUpdateEntity(userNoteSettings);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse enableDisableHiddenNotes(Boolean enableDisableHiddenNotes) {
        UserNoteSettings userNoteSettings = commonDao.findEntity(UserNoteSettings.class, CommonUtils.getLoggedInUserId());

        if (userNoteSettings == null) {
            userNoteSettings = new UserNoteSettings();
        }

        userNoteSettings.setShowHiddenNotes(enableDisableHiddenNotes);

        commonDao.saveOrUpdateEntity(userNoteSettings);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse hideUnHideCompleteNotes(Boolean hideUnHideCompleteNotes) {

        List<DocumentNotes> documentNotes = commonDao.findEntitiesByCriteria(DocumentNotes.class,
                "userId", CommonUtils.getLoggedInUserId());

        documentNotes.forEach(documentNote -> {
            documentNote.setNoteHidden(hideUnHideCompleteNotes);
            commonDao.saveOrUpdateEntity(documentNote);
        });

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse getNotesSettings() {

        UserNoteSettings userNoteSettings = commonDao.findEntity(UserNoteSettings.class, CommonUtils.getLoggedInUserId());

        if (userNoteSettings == null) {

            userNoteSettings = new UserNoteSettings();

            userNoteSettings.setShowHiddenNotes(false);

            userNoteSettings.setEnableShareNotes(true);

            userNoteSettings.setUserId(CommonUtils.getLoggedInUserId());

            commonDao.saveOrUpdateEntity(userNoteSettings);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("enableDisableShareNotes", userNoteSettings.getEnableShareNotes());

        response.put("hideUnHideNotes", userNoteSettings.getShowHiddenNotes());

        response.put("hideUnHideDeleteButton", userNoteSettings.getHideDeleteButton());

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);
    }

    @Override
    public PortableResponse enableDisableSingleHiddenNotes(Boolean enableDisableHiddenNotes,
                                                           Long noteId) {

        DocumentNotes documentNotes = commonDao.findEntity(DocumentNotes.class, noteId);

        documentNotes.setNoteHidden(enableDisableHiddenNotes);

        commonDao.saveOrUpdateEntity(documentNotes);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse hideUnHideDeleteButton(Boolean hideUnHideDeleteButton) {

        UserNoteSettings userNoteSettings = commonDao.findEntity(UserNoteSettings.class, CommonUtils.getLoggedInUserId());

        userNoteSettings.setHideDeleteButton(hideUnHideDeleteButton);

        commonDao.saveOrUpdateEntity(userNoteSettings);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse acceptRejectNotification(Long sni, boolean isAccept) {

        SharedNotesSettings sharedNotesSettings = commonDao.findEntity(SharedNotesSettings.class, sni);

        if (sharedNotesSettings == null) {
            throw new BadRequestException("Owner removed access for you");
        }

        sharedNotesSettings.setUserNoteStatus(isAccept ? 1 : 0);

        commonDao.saveOrUpdateEntity(sharedNotesSettings);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse getNotesShareDetails(Long noteId) {

        Long loggedInUserId = CommonUtils.getLoggedInUserId();

        DocumentNotes documentNotes = commonDao.findEntity(DocumentNotes.class, noteId);

        List<SharedNotesSettings> sharedNotesSettings;
        if (documentNotes.getUserId().equals(loggedInUserId)) {

            sharedNotesSettings = commonDao.findEntitiesByCriteria(SharedNotesSettings.class, "noteId", noteId);

        } else {

            sharedNotesSettings = commonDao.findEntitiesByCriteria(
                    SharedNotesSettings.class, "noteId", noteId, "sharedBy", loggedInUserId);

        }

        List<Map<String, Object>> response = new ArrayList<>();

        Map<Long, String> userNames = getUserNames(sharedNotesSettings.stream().map(SharedNotesSettings::getSharedTO).collect(Collectors.toList()));

        sharedNotesSettings.forEach(settings -> {

            Map<String, Object> noteSettings = new HashMap<>();

            noteSettings.put("d", settings.getNotesDelete());

            noteSettings.put("e", settings.getNotesEditable());

            noteSettings.put("s", settings.getNotesSharable());

            noteSettings.put("dw", settings.getNotesDownloadable());

            noteSettings.put("su", userNames.get(settings.getSharedTO().toString()));

            noteSettings.put("m", settings.getMessage());

            noteSettings.put("si", settings.getShareId());

            response.add(noteSettings);

        });

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);

    }

    @Override
    public PortableResponse shareNotesDetails(Map<String, Object> payload) {

        Long notesId = Long.valueOf(payload.get("noteId").toString());

        String userName = payload.get("userName").toString();

        Long loggedInUserId = CommonUtils.getLoggedInUserId();

        Map<String, String> userIds = getUserIds(Arrays.asList(userName));

        if (CollectionUtils.isEmpty(userIds)) {
            throw new BadRequestException("User Not Found");
        }

        Long userId = Long.valueOf(userIds.get(userName));

        DocumentNotes documentNotes = commonDao.findEntity(DocumentNotes.class, notesId);

        if (documentNotes.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot share this notes again to owner");
        }

        if (!documentNotes.getUserId().equals(loggedInUserId)) {

            validatePermissions(documentNotes, loggedInUserId, payload);

        }

        SharedNotesSettings sharedNotesSettings = null;
        List<SharedNotesSettings> sharedNotesSetting = commonDao.findEntitiesByCriteria(SharedNotesSettings.class, "noteId", notesId, "sharedTO", userId);
        if (!CollectionUtils.isEmpty(sharedNotesSetting)) {
            sharedNotesSettings = sharedNotesSetting.get(0);
        }

        if (sharedNotesSettings == null) {

            sharedNotesSettings = new SharedNotesSettings();

            sharedNotesSettings.setSharedBy(CommonUtils.getLoggedInUserId());

        } else {
            if (!sharedNotesSettings.getSharedBy().equals(loggedInUserId)) {
                if (!loggedInUserId.equals(documentNotes.getUserId())) {
                    throw new BadRequestException("You have no permissions to edit this access");
                }
            }
        }


        sharedNotesSettings.setNoteId(notesId);

        sharedNotesSettings.setSharedTO(userId);

        sharedNotesSettings.setNotesDelete(Boolean.valueOf(payload.get("d").toString()));

        sharedNotesSettings.setNotesDownloadable(Boolean.valueOf(payload.get("dw").toString()));

        sharedNotesSettings.setNotesEditable(Boolean.valueOf(payload.get("e").toString()));

        sharedNotesSettings.setNotesSharable(Boolean.valueOf(payload.get("s").toString()));

        sharedNotesSettings.setMessage(payload.get("m") != null ? payload.get("m").toString() : "");

        commonDao.saveOrUpdateEntity(sharedNotesSettings);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse checkUserExists(Map<String, Object> payload) {
        String userName;

        if (StringUtils.isEmpty(payload.get("userName")) || StringUtils.isEmpty(payload.get("noteId"))) {
            throw new BadRequestException("Mandatory Fields missing");
        }

        userName = payload.get("userName").toString();

        Map<String, String> userDetails = getUserIds(Arrays.asList(userName));

        if (CollectionUtils.isEmpty(userDetails)) {
            throw new BadRequestException("User Not Found");
        }

        Long userId = Long.valueOf(userDetails.get(userName));

        Long noteId = Long.valueOf(payload.get("noteId").toString());

        List<SharedNotesSettings> sharedNotesSettings = commonDao.findEntitiesByCriteria(SharedNotesSettings.class,
                "sharedTO", userId, "noteId", noteId);

        if (!CollectionUtils.isEmpty(sharedNotesSettings)) {
            throw new BadRequestException("Already you shared this notes to the user");
        }

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);

    }

    @Override
    public PortableResponse removeShareAccess(Map<String, Object> payload) {
        String userName;
        Long loggedInUserId;

        if (StringUtils.isEmpty(payload.get("userName")) || StringUtils.isEmpty(payload.get("noteId"))) {
            throw new BadRequestException("Mandatory Fields missing");
        }

        userName = payload.get("userName").toString();

        Map<String, String> userDetails = getUserIds(Arrays.asList(userName));

        Long userId = Long.valueOf(userDetails.get(userName));

        Long noteId = Long.valueOf(payload.get("noteId").toString());

        SharedNotesSettings sharedNotesSettings = null;
        List<SharedNotesSettings> sharedNotesSetting = commonDao.findEntitiesByCriteria(SharedNotesSettings.class,
                "noteId", noteId, "sharedTO", userId);
        if (!CollectionUtils.isEmpty(sharedNotesSetting)) {
            sharedNotesSettings = sharedNotesSetting.get(0);
        }

        loggedInUserId = CommonUtils.getLoggedInUserId();

        if (sharedNotesSettings != null) {
            if (sharedNotesSettings.getSharedBy().equals(loggedInUserId)) {
                commonDao.deleteEntity(SharedNotesSettings.class, sharedNotesSettings.getShareId());
            } else {
                DocumentNotes documentNotes = commonDao.findEntity(DocumentNotes.class, sharedNotesSettings.getNoteId());
                if (loggedInUserId.equals(documentNotes.getUserId())) {
                    commonDao.deleteEntity(SharedNotesSettings.class, sharedNotesSettings.getShareId());
                } else {
                    throw new BadRequestException("You have no permissions to remove this access");
                }
            }
        }

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse getNotes() {

        Long loggedInUserId = CommonUtils.getLoggedInUserId();

        Map<String, Object> response = new HashMap<>();

        List<Map> notes = new ArrayList<>();

        List<Map> pendingSharedNotesResponse = new ArrayList<>();

        response.put("nc", 0);

        UserNoteSettings userNoteSettings = commonDao.findEntity(UserNoteSettings.class, loggedInUserId);

        if (userNoteSettings == null) {
            userNoteSettings = new UserNoteSettings();
            userNoteSettings.setUserId(loggedInUserId);
            userNoteSettings.setEnableShareNotes(true);
            userNoteSettings.setShowHiddenNotes(false);
            commonDao.saveOrUpdateEntity(userNoteSettings);
        }

        List<StickyNotesVO> stickyNotes = commonDao.executeQuery(HibernateQueryConstant.Q_GET_STICKY_NOTES,
                new KeyValueMapping<String, Object>().setKeys("loggedInUserId").setValues(loggedInUserId).getResult(), StickyNotesVO.class);

        List<Long> userIds = stickyNotes.stream().map(StickyNotesVO::getUserId).collect(Collectors.toList());

        userIds.addAll(
                stickyNotes.stream().filter(stickyNote -> stickyNote.getSharedBy() != null).map(StickyNotesVO::getSharedBy).collect(Collectors.toList())
        );

        Map<Long, String> userNames = getUserNames(userIds);

        List<Long> noteIds = new ArrayList<>();

        for (StickyNotesVO stickyNote : stickyNotes) {

            if (noteIds.contains(stickyNote.getNoteId()))
                continue;

            noteIds.add(stickyNote.getNoteId());

            Map<String, Object> note = prepareDocumentNotes(stickyNote, loggedInUserId);

            if (!stickyNote.getUserId().equals(loggedInUserId)) {
                //note belongs to someone
                note.put("sharedBy", userNames.get(stickyNote.getSharedBy().toString()));

                note.put("message", stickyNote.getMessage());

                note.put("sni", stickyNote.getShareId());

                note.put("hidden", false);

                note.put("esb", stickyNote.getNotesSharable());

                note.put("edwb", stickyNote.getNotesDownloadable());

                note.put("edb", stickyNote.getNotesDelete());

                note.put("eeb", stickyNote.getNotesEditable());

                if (stickyNote.getUserNoteStatus() == null) {
                    //it's in pending list
                    pendingSharedNotesResponse.add(note);
                } else if (stickyNote.getUserNoteStatus() == 1) {
                    //it's in approved list
                    notes.add(note);
                }

            } else {
                //note belongs to the user

                if (stickyNote.getNoteHidden() && !userNoteSettings.getShowHiddenNotes())
                    continue;

                notes.add(note);

            }

        }

        response.put("notes", notes);
        response.put("pendingNotes", pendingSharedNotesResponse);
        response.put("nc", pendingSharedNotesResponse.size());
        response.put("hideUnHideDeleteButton", userNoteSettings.getHideDeleteButton());

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);
    }

    @Override
    public PortableResponse getNotes(Long noteId) {

        Long loggedInUserId = CommonUtils.getLoggedInUserId();

        List<StickyNotesVO> stickyNotes = commonDao.executeQuery(HibernateQueryConstant.Q_GET_STICKY_NOTE,
                new KeyValueMapping<String, Object>().setKeys("loggedInUserId", "noteId").setValues(loggedInUserId, noteId).getResult(), StickyNotesVO.class);

        if (CollectionUtils.isEmpty(stickyNotes)) {
            throw new BadRequestException("Note not found");
        }

        Map<String, Object> note = prepareDocumentNotes(stickyNotes.get(0), loggedInUserId);

        note.put("note", stickyNotes.get(0).getNotes());

        if (!stickyNotes.get(0).getUserId().equals(loggedInUserId)) {
            //note belongs to someone
            Map<Long, String> userNames = getUserNames(Arrays.asList(stickyNotes.get(0).getUserId()));

            note.put("sharedBy", userNames.get(stickyNotes.get(0).getSharedBy().toString()));

            note.put("message", stickyNotes.get(0).getMessage());

            note.put("sni", stickyNotes.get(0).getShareId());

            note.put("hidden", false);

            note.put("esb", stickyNotes.get(0).getNotesSharable());

            note.put("edwb", stickyNotes.get(0).getNotesDownloadable());

            note.put("edb", stickyNotes.get(0).getNotesDelete());

            note.put("eeb", stickyNotes.get(0).getNotesEditable());

        }

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, note);
    }

    @Override
    public PortableResponse checkNotesCount() {
        Long loggedInUserId = CommonUtils.getLoggedInUserId();

        List<Object> sharedNotesCount = commonDao.execueQuery(HibernateQueryConstant.Q_GET_SHARED_COUNT, new KeyValueMapping<String, Object>()
                .setKeys("loggedInUserId").setValues(loggedInUserId).getResult());

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, sharedNotesCount.get(0).toString());
    }

    @Override
    public PortableResponse getNotifications() {

        List<Object> pendingSharedNotesResponse = new ArrayList<>();

        Long loggedInUserId = CommonUtils.getLoggedInUserId();

        List<StickyNotesVO> stickyNotes = commonDao.executeQuery(HibernateQueryConstant.Q_GET_PENDING_NOTES,
                new KeyValueMapping<String, Object>().setKeys("loggedInUserId").setValues(loggedInUserId).getResult(), StickyNotesVO.class);

        List<Long> userIds = stickyNotes.stream().map(StickyNotesVO::getUserId).collect(Collectors.toList());

        userIds.addAll(
                stickyNotes.stream().filter(stickyNote -> stickyNote.getSharedBy() != null).map(StickyNotesVO::getSharedBy).collect(Collectors.toList())
        );

        Map<Long, String> userNames = getUserNames(userIds);

        for (StickyNotesVO stickyNote : stickyNotes) {

            Map<String, Object> note = prepareDocumentNotes(stickyNote, loggedInUserId);

            //note belongs to someone
            note.put("sharedBy", userNames.get(stickyNote.getSharedBy().toString()));

            note.put("message", stickyNote.getMessage());

            note.put("sni", stickyNote.getShareId());

            note.put("hidden", false);

            note.put("esb", stickyNote.getNotesSharable());

            note.put("edwb", stickyNote.getNotesDownloadable());

            note.put("edb", stickyNote.getNotesDelete());

            note.put("eeb", stickyNote.getNotesEditable());

            pendingSharedNotesResponse.add(note);

        }

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, pendingSharedNotesResponse);

    }

    private Map<String, Object> prepareDocumentNotes(StickyNotesVO stickyNote, Long loggedInUserId) {
        Map<String, Object> note = new HashMap<>();
        note.put("noteId", stickyNote.getNoteId());
        note.put("tittle", stickyNote.getTittle());
        note.put("note", stickyNote.getNotes() != null ? stickyNote.getNotes().length() > 5000 ? stickyNote.getNotes().substring(0, 4999) : stickyNote.getNotes() : "");
        note.put("size", dmsUtils.getFormattedSize(Long.valueOf(stickyNote.getNotes().getBytes().length)));
        note.put("createdDate", DateUtils.formatDate(DateUtils.PORTABLE_DEFAULT_DATE, stickyNote.getCreatedDate()));
        note.put("updatedDate", DateUtils.formatDate(DateUtils.PORTABLE_DEFAULT_DATE, stickyNote.getUpdatedDate()));
        note.put("cv", stickyNote.getNotes().length() < 5000);
        note.put("hidden", stickyNote.getNoteHidden());
        note.put("s", !stickyNote.getUserId().equals(loggedInUserId));
        return note;
    }

    public Map<Long, String> getUserNames(List<Long> userIds) {

        Map<String, Object> request = new HashMap<>();

        request.put("userIds", userIds);

        PortableResponse response = userProfileService.getUserNames(request);

        return (Map<Long, String>) response.getData();

    }

    public Map<String, String> getUserIds(List<String> userNames) {

        PortableResponse userIds = userProfileService.getUserIds(userNames);

        return (Map<String, String>)   userIds.getData();

    }

    private void validatePermissions(DocumentNotes documentNotes, Long loggedInUserId, Map<String, Object> payload) {

        List<SharedNotesSettings> sharedNotesSettings = commonDao.findEntitiesByCriteria(SharedNotesSettings.class,
                "noteId", documentNotes.getNoteId(), "sharedTO", loggedInUserId);

        if (CollectionUtils.isEmpty(sharedNotesSettings)) {
            throw new BadRequestException("This document is not belongs to you");
        }

        if (sharedNotesSettings.get(0).getNotesSharable() == null || !sharedNotesSettings.get(0).getNotesSharable()) {
            throw new BadRequestException("You don't have share option, ask notes owner to give share access");
        }

        if (Boolean.valueOf(payload.get("d").toString()) && !sharedNotesSettings.get(0).getNotesDelete()) {
            throw new BadRequestException("You are not having delete access, so you cannot share delete access to other user");
        }

        if (Boolean.valueOf(payload.get("e").toString()) && !sharedNotesSettings.get(0).getNotesEditable()) {
            throw new BadRequestException("You are not having edit access, so you cannot share edit access to other user");
        }

        if (Boolean.valueOf(payload.get("dw").toString()) && !sharedNotesSettings.get(0).getNotesDownloadable()) {
            throw new BadRequestException("You are not having download access, so you cannot share download access to other user");
        }

    }

}
