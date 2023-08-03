package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_note_settings", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class UserNoteSettings {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "enable_share_notes")
    @ColumnDefault("true")
    private Boolean enableShareNotes;

    @Column(name = "show_hidden_notes")
    @ColumnDefault("false")
    private Boolean showHiddenNotes;

    @Column(name = "hide_delete_button")
    @ColumnDefault("false")
    private Boolean hideDeleteButton;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getEnableShareNotes() {
        return enableShareNotes;
    }

    public void setEnableShareNotes(Boolean enableShareNotes) {
        this.enableShareNotes = enableShareNotes;
    }

    public Boolean getShowHiddenNotes() {
        return showHiddenNotes;
    }

    public void setShowHiddenNotes(Boolean showHiddenNotes) {
        this.showHiddenNotes = showHiddenNotes;
    }

    public Boolean getHideDeleteButton() {
        return hideDeleteButton;
    }

    public void setHideDeleteButton(Boolean hideDeleteButton) {
        this.hideDeleteButton = hideDeleteButton;
    }
}
