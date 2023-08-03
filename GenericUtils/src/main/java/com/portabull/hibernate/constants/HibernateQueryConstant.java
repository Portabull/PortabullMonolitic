package com.portabull.hibernate.constants;

public class HibernateQueryConstant {

    private HibernateQueryConstant() {
    }

    public static final String Q_GET_STICKY_NOTES = "SELECT dn.noteId as noteId,dn.userId as userId,dn.tittle as tittle,dn.notes as notes,dn.createdDate as createdDate,dn.updatedDate as updatedDate,dn.noteHidden as noteHidden,sns.shareId as shareId,sns.notesEditable as notesEditable,sns.notesSharable as notesSharable,sns.notesDelete as notesDelete,sns.notesDownloadable as notesDownloadable,sns.sharedTO as sharedTO,sns.sharedBy as sharedBy,sns.message as message,sns.userNoteStatus as userNoteStatus FROM DocumentNotes dn LEFT JOIN SharedNotesSettings sns ON (dn.noteId=sns.noteId) WHERE dn.userId=:loggedInUserId OR sns.sharedTO=:loggedInUserId order by dn.updatedDate";

    public static final String Q_GET_STICKY_NOTE = "SELECT dn.noteId as noteId,dn.userId as userId,dn.tittle as tittle,dn.notes as notes,dn.createdDate as createdDate,dn.updatedDate as updatedDate,dn.noteHidden as noteHidden,sns.shareId as shareId,sns.notesEditable as notesEditable,sns.notesSharable as notesSharable,sns.notesDelete as notesDelete,sns.notesDownloadable as notesDownloadable,sns.sharedTO as sharedTO,sns.sharedBy as sharedBy,sns.message as message,sns.userNoteStatus as userNoteStatus FROM DocumentNotes dn LEFT JOIN SharedNotesSettings sns ON (dn.noteId=sns.noteId) WHERE (dn.userId=:loggedInUserId OR sns.sharedTO=:loggedInUserId) AND dn.noteId=:noteId order by dn.updatedDate";

    public static final String Q_GET_SHARED_COUNT = "SELECT COUNT(*) FROM SharedNotesSettings WHERE sharedTO=:loggedInUserId AND userNoteStatus IS NULL";

    public static final String Q_GET_PENDING_NOTES = "SELECT dn.noteId as noteId,dn.userId as userId,dn.tittle as tittle,dn.notes as notes,dn.createdDate as createdDate,dn.updatedDate as updatedDate,dn.noteHidden as noteHidden,sns.shareId as shareId,sns.notesEditable as notesEditable,sns.notesSharable as notesSharable,sns.notesDelete as notesDelete,sns.notesDownloadable as notesDownloadable,sns.sharedTO as sharedTO,sns.sharedBy as sharedBy,sns.message as message,sns.userNoteStatus as userNoteStatus FROM DocumentNotes dn LEFT JOIN SharedNotesSettings sns ON (dn.noteId=sns.noteId) WHERE sns.sharedTO=:loggedInUserId AND sns.userNoteStatus IS NULL order by dn.updatedDate";

}
