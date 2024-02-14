package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "generic_cache", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class GenericCache implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "generic_key")
    private String key;

    @Column(name = "generic_value", columnDefinition = "TEXT")
    private String value;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
