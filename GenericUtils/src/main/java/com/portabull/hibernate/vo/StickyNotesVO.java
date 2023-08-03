package com.portabull.hibernate.vo;

import java.util.Date;

public class StickyNotesVO {

    private Long noteId;

    private Long userId;

    private String tittle;

    private String notes;

    private Date createdDate;

    private Date updatedDate;

    private Boolean noteHidden;

    private Long shareId;

    private Boolean notesEditable;

    private Boolean notesSharable;

    private Boolean notesDelete;

    private Boolean notesDownloadable;

    private Long sharedTO;

    private Long sharedBy;

    private String message;

    private Integer userNoteStatus;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Boolean getNoteHidden() {
        return noteHidden;
    }

    public void setNoteHidden(Boolean noteHidden) {
        this.noteHidden = noteHidden;
    }

    public Boolean getNotesEditable() {
        return notesEditable;
    }

    public void setNotesEditable(Boolean notesEditable) {
        this.notesEditable = notesEditable;
    }

    public Boolean getNotesSharable() {
        return notesSharable;
    }

    public void setNotesSharable(Boolean notesSharable) {
        this.notesSharable = notesSharable;
    }

    public Boolean getNotesDelete() {
        return notesDelete;
    }

    public void setNotesDelete(Boolean notesDelete) {
        this.notesDelete = notesDelete;
    }

    public Boolean getNotesDownloadable() {
        return notesDownloadable;
    }

    public void setNotesDownloadable(Boolean notesDownloadable) {
        this.notesDownloadable = notesDownloadable;
    }

    public Long getSharedTO() {
        return sharedTO;
    }

    public void setSharedTO(Long sharedTO) {
        this.sharedTO = sharedTO;
    }

    public Long getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(Long sharedBy) {
        this.sharedBy = sharedBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUserNoteStatus() {
        return userNoteStatus;
    }

    public void setUserNoteStatus(Integer userNoteStatus) {
        this.userNoteStatus = userNoteStatus;
    }

    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }
}
