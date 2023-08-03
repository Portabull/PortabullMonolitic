package com.portabull.um;


import com.portabull.constants.DatabaseSchema;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "notification_user_jwt_token", schema = DatabaseSchema.USER_MANAGEMENT, catalog = DatabaseSchema.USER_MANAGEMENT)
public class NotificationUserJWTToken {


    @Id
    @Column(name = "notification_user_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_notify_user_jwt_token_id")
    private Long notificationUserId;

    @Column(name = "random_token_id", unique = true)
    private String randomTokenId;

    @Column(name = "jwt_token", unique = true)
    private String jwtToken;

    @Column(name = "expiry_date")
    private Long expiryDate;

    @Column(name = "approval")
    @ColumnDefault("0")//0 - not seen ,1-approved 2- rejected
    private Integer approval = 0;

    public Long getNotificationUserId() {
        return notificationUserId;
    }

    public void setNotificationUserId(Long notificationUserId) {
        this.notificationUserId = notificationUserId;
    }

    public String getRandomTokenId() {
        return randomTokenId;
    }

    public void setRandomTokenId(String randomTokenId) {
        this.randomTokenId = randomTokenId;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getApproval() {
        return approval;
    }

    public void setApproval(Integer approval) {
        this.approval = approval;
    }
}
