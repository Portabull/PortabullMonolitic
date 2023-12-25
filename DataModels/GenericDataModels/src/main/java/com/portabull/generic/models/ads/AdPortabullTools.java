package com.portabull.generic.models.ads;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "ad_portabull_tools", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class AdPortabullTools {

    @Id
    @Column(name = "tool_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_portabull_tool_id")
    private Long toolId;

    @Column(name = "tool_name", unique = true)
    private String toolName;

    @Column(name = "tool_details")
    private String toolDetails;

    @Column(name = "tool_html_pages")
    private String toolHtmlPages;

    @Column(name = "tool_dimensions", columnDefinition = "TEXT")
    private String toolDimensions;

    public Long getToolId() {
        return toolId;
    }

    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getToolDetails() {
        return toolDetails;
    }

    public void setToolDetails(String toolDetails) {
        this.toolDetails = toolDetails;
    }

    public String getToolHtmlPages() {
        return toolHtmlPages;
    }

    public void setToolHtmlPages(String toolHtmlPages) {
        this.toolHtmlPages = toolHtmlPages;
    }

    public String getToolDimensions() {
        return toolDimensions;
    }

    public void setToolDimensions(String toolDimensions) {
        this.toolDimensions = toolDimensions;
    }
}
