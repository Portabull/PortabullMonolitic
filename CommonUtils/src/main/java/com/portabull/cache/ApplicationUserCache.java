package com.portabull.cache;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "temp_application_user_cache", schema = DatabaseSchema.USER_MANAGEMENT, catalog = DatabaseSchema.USER_MANAGEMENT,
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"token_id", "cache_key"}))
public class ApplicationUserCache {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_application_user_cache_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long id;

    @Column(name = "token_id")
    private String token;

    @Column(name = "cache_key")
    private String key;

    @Column(name = "cache_data", columnDefinition = "TEXT")
    private String data;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
