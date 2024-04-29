package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "static_java_jar_paths", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class StaticJavaJarPaths {


    @Id
    @Column(name = "static_java_jar_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_static_java_jar_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long jarId;

    @Column(name = "import_package", columnDefinition = "TEXT")
    private String jarPath;

    @Column(name = "framework_name")
    private String description;

    public Long getJarId() {
        return jarId;
    }

    public void setJarId(Long jarId) {
        this.jarId = jarId;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
