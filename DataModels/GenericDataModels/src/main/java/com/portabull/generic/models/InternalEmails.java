package com.portabull.generic.models;


import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "internal_emails", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class InternalEmails {

    @Id
    @Column(name = "message_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_message_id")
    private Long messageId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "message_subject")
    private String messageSubject;

    @Column(name = "message_body", columnDefinition = "TEXT")
    private String messageBody;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "receiver_address", nullable = false)
    private String receiverAddress;

    @Column(name = "sender_address", nullable = false)
    private String senderAddress;

    @Column(name = "created_date")
    private String createdDate;

    @Column(name = "mail_seen")
    private Boolean mailSeen;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getMessageSubject() {
        return messageSubject;
    }

    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getMailSeen() {
        return mailSeen;
    }

    public void setMailSeen(Boolean mailSeen) {
        this.mailSeen = mailSeen;
    }
}
