package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "static_java_imports", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class StaticJavaImports {


    @Id
    @Column(name = "static_java_import_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_static_java_import_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long schedulerId;

    @Column(name = "import_package")
    private String importPackage;

    @Column(name = "framework_name")
    private String frameworkName;

    public Long getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(Long schedulerId) {
        this.schedulerId = schedulerId;
    }

    public String getImportPackage() {
        return importPackage;
    }

    public void setImportPackage(String importPackage) {
        this.importPackage = importPackage;
    }

    public String getFrameworkName() {
        return frameworkName;
    }

    public void setFrameworkName(String frameworkName) {
        this.frameworkName = frameworkName;
    }
}
