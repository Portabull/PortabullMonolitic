package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "scheduler_task", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class SchedulerTask {

    @Id
    @Column(name = "scheduler_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_scheduler_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long schedulerId;

    @Column(name = "scheduler_name")
    private String schedulerName;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "trigger_type")
    private String triggerType;//Daily/TimetoTime/Specific time

    @Column(name = "days")
    private String days;//m,te,w,th,f,s,su

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "schedulerId")
    private Set<SchedulerActions> schedulerActions;

    @Column(name = "specific_date")
    private Date specificDate;

    @Column(name = "time_gap")
    private Integer timeGap;

    @Column(name = "last_triggered_date")
    private Date lastTriggeredDate;

    @Column(name = "specific_time")
    private String specificDailyTime;

    @Column(name = "is_active")
    private boolean isActive;

    public Long getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(Long schedulerId) {
        this.schedulerId = schedulerId;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public Set<SchedulerActions> getSchedulerActions() {
        return schedulerActions;
    }

    public void setSchedulerActions(Set<SchedulerActions> schedulerActions) {
        this.schedulerActions = schedulerActions;
    }

    public Date getSpecificDate() {
        return specificDate;
    }

    public void setSpecificDate(Date specificDate) {
        this.specificDate = specificDate;
    }

    public Integer getTimeGap() {
        return timeGap;
    }

    public void setTimeGap(Integer timeGap) {
        this.timeGap = timeGap;
    }

    public Date getLastTriggeredDate() {
        return lastTriggeredDate;
    }

    public void setLastTriggeredDate(Date lastTriggeredDate) {
        this.lastTriggeredDate = lastTriggeredDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getSpecificDailyTime() {
        return specificDailyTime;
    }

    public void setSpecificDailyTime(String specificDailyTime) {
        this.specificDailyTime = specificDailyTime;
    }
}
