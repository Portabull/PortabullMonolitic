package com.portabull.entitys;


import com.portabull.constants.DatabaseSchema;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "document_notes", schema = DatabaseSchema.DMS, catalog = DatabaseSchema.DMS)
public class DocumentNotes {

    @Id
    @Column(name = "note_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_document_notes")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long noteId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tittle")
    private String tittle;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "note_hidden")
    @ColumnDefault("false")
    private Boolean noteHidden;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getNoteHidden() {
        return noteHidden;
    }

    public void setNoteHidden(Boolean noteHidden) {
        this.noteHidden = noteHidden;
    }
}
