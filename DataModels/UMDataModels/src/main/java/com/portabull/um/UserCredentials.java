package com.portabull.um;

import com.portabull.constants.DatabaseSchema;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "user_credentials", schema = DatabaseSchema.USER_MANAGEMENT, catalog = DatabaseSchema.USER_MANAGEMENT)
public class UserCredentials implements Serializable {


    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_user_cred_id")
    private Long userID;

    @Column(name = "user_name")
    private String userName;
    @Column(name = "login_user_name", unique = true)
    private String loginUserName;

    @Column(name = "password")
    private String password;

    @Column(name = "wrong_password_count")
    private Integer wrongPasswordCount;

    @Column(name = "is_account_locked")
    private Boolean isAccountLocked;

    @Column(name = "is_two_step_verification_enabled")
    private Boolean isTwoStepVerificationEnabled;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userId")
    private Set<LoggedInOTP> loggedInOTPS;

    @Column(name = "no_of_last_logged_in_details_capture")
    private Long noOfLastLoggedInDetailsCap;

    @Column(name = "logged_in_session_time_in_mins")
    private Long loggedInSessionTime;

    @Column(name = "account_unlock_reason")
    private String reason;

    @Column(name = "logged_with_oauth")
    private Boolean loggedWithOAuth;

    @Column(name = "user_already_registered")
    private Boolean alreadyRegistered;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "profile_pic_dms_id")
    private Long profilePicDMSId;

    @Column(name = "is_single_sign_in")
    private Boolean isSingleSignIn;

    /**
     * 0 - Send Otp to Email
     * 1 - Send Link to Email
     */

    @Column(name = "mfa_login_type")
    @ColumnDefault("0")
    private Integer mfaLoginType;

    public Integer getWrongPasswordCount() {
        return wrongPasswordCount;
    }

    public void setWrongPasswordCount(Integer wrongPasswordCount) {
        this.wrongPasswordCount = wrongPasswordCount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    public UserCredentials() {
    }

    public UserCredentials(Long userID, String userName) {
        this.userID = userID;
        this.loginUserName = userName;
    }

    public UserCredentials(String userName) {
        this.loginUserName = userName;
    }

    public Boolean getTwoStepVerificationEnabled() {
        return isTwoStepVerificationEnabled != null && isTwoStepVerificationEnabled;
    }

    public void setTwoStepVerificationEnabled(Boolean twoStepVerificationEnabled) {
        isTwoStepVerificationEnabled = twoStepVerificationEnabled;
    }

    public Set<LoggedInOTP> getLoggedInOTPS() {
        return loggedInOTPS;
    }

    public void setLoggedInOTPS(Set<LoggedInOTP> loggedInOTPS) {
        this.loggedInOTPS = loggedInOTPS;
    }

    public Long getNoOfLastLoggedInDetailsCap() {
        return noOfLastLoggedInDetailsCap;
    }

    public void setNoOfLastLoggedInDetailsCap(Long noOfLastLoggedInDetailsCap) {
        this.noOfLastLoggedInDetailsCap = noOfLastLoggedInDetailsCap;
    }

    public Long getLoggedInSessionTime() {
        return loggedInSessionTime;
    }

    public void setLoggedInSessionTime(Long loggedInSessionTime) {
        this.loggedInSessionTime = loggedInSessionTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getAccountLocked() {
        return isAccountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        isAccountLocked = accountLocked;
    }

    public Boolean getLoggedWithOAuth() {
        return loggedWithOAuth;
    }

    public void setLoggedWithOAuth(Boolean loggedWithOAuth) {
        this.loggedWithOAuth = loggedWithOAuth;
    }

    public Boolean getAlreadyRegistered() {
        return alreadyRegistered;
    }

    public void setAlreadyRegistered(Boolean alreadyRegistered) {
        this.alreadyRegistered = alreadyRegistered;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Long getProfilePicDMSId() {
        return profilePicDMSId;
    }

    public void setProfilePicDMSId(Long profilePicDMSId) {
        this.profilePicDMSId = profilePicDMSId;
    }

    public Boolean getSingleSignIn() {
        return isSingleSignIn;
    }

    public void setSingleSignIn(Boolean singleSignIn) {
        isSingleSignIn = singleSignIn;
    }

    public Integer getMfaLoginType() {
        return mfaLoginType;
    }

    public void setMfaLoginType(Integer mfaLoginType) {
        this.mfaLoginType = mfaLoginType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
