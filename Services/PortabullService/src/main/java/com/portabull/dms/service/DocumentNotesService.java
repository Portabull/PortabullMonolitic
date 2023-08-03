package com.portabull.dms.service;

import com.portabull.response.DocumentResponse;
import com.portabull.response.PortableResponse;

import java.io.IOException;
import java.util.Map;

public interface DocumentNotesService {

    public PortableResponse updateNotes(String noteId, String tittle, String note, boolean delete);

    public PortableResponse getNotes();

    DocumentResponse downloadNotes(Long noteId) throws IOException;

    PortableResponse enableDisableShareNotes(Boolean enableDisableShareNotes);

    PortableResponse enableDisableHiddenNotes(Boolean enableDisableHiddenNotes);

    PortableResponse hideUnHideCompleteNotes(Boolean hideUnHideCompleteNotes);

    PortableResponse getNotesSettings();

    PortableResponse enableDisableSingleHiddenNotes(Boolean enableDisableHiddenNotes, Long noteId);

    PortableResponse hideUnHideDeleteButton(Boolean hideUnHideDeleteButton);

    PortableResponse acceptRejectNotification(Long sni, boolean isAccept);

    PortableResponse getNotesShareDetails(Long noteId);

    PortableResponse shareNotesDetails(Map<String, Object> payload);

    PortableResponse checkUserExists(Map<String, Object> payload);

    PortableResponse removeShareAccess(Map<String, Object> payload);

    PortableResponse getNotes(Long noteId);

    PortableResponse checkNotesCount();

    PortableResponse getNotifications();
}
