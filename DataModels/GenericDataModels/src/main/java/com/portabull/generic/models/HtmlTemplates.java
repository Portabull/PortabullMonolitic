package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "html_templates", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class HtmlTemplates implements Serializable {

    @Id
    @Column(name = "template_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_html_template")
    private Long templateID;

    @Column(name = "template")
    private String template;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "templateID")
    private Set<ImageURI> imageURI;

    public Long getTemplateID() {
        return templateID;
    }

    public void setTemplateID(Long templateID) {
        this.templateID = templateID;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Set<ImageURI> getImageURI() {
        return imageURI;
    }

    public void setImageURI(Set<ImageURI> imageURI) {
        this.imageURI = imageURI;
    }
}
