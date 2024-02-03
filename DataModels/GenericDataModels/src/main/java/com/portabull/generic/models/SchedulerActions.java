package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "scheduler_actions", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class SchedulerActions {


    @Id
    @Column(name = "scheduler_action_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_scheduler_action_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long actionId;

    @Column(name = "scheduler_id")
    private Long schedulerId;

    @Column(name = "action_type")
    private String action_type;//Mail/RestAPI/

    @Column(name = "action", columnDefinition = "TEXT")
    private String action;//json string

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public Long getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(Long schedulerId) {
        this.schedulerId = schedulerId;
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
