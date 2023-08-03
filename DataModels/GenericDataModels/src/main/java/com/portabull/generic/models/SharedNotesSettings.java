package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "shared_notes_settings", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class SharedNotesSettings {

    @Id
    @Column(name = "share_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_note_share_id")
    private Long shareId;

    @Column(name = "note_id")
    private Long noteId;

    @Column(name = "notes_editable")
    @ColumnDefault("false")
    private Boolean notesEditable;

    @Column(name = "notes_sharable")
    @ColumnDefault("false")
    private Boolean notesSharable;

    @Column(name = "notes_delete")
    @ColumnDefault("false")
    private Boolean notesDelete;

    @Column(name = "notes_downloadable")
    @ColumnDefault("true")
    private Boolean notesDownloadable;

    @Column(name = "shared_to")
    private Long sharedTO;

    @Column(name = "shared_by")
    private Long sharedBy;

    @Column(name = "message")
    private String message;

    @Column(name = "user_note_status") // 0 - Reject , 1 - Accept, null -pending
    private Integer userNoteStatus;

    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
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
}
