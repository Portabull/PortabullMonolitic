package com.portabull.generic.models;


import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "temp_json_keka", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class TempJsonKeka {

    @Id
    @Column(name = "temp_json_keka_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_temp_json_keka_id")
    private Long shareId;

    @Column(name = "refresh_token")
    private String refreshToken;

    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
