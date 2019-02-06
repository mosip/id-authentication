package io.mosip.kernel.auth.dto.otp;

public class OtpTemplateDto {
    private String id;
    private String name;
    private String description;
    private String fileFormatCode;
    private String model;
    private String fileText;
    private String moduleId;
    private String moduleName;
    private String templateTypeCode;
    private String langCode;
    private Boolean isActive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileFormatCode() {
        return fileFormatCode;
    }

    public void setFileFormatCode(String fileFormatCode) {
        this.fileFormatCode = fileFormatCode;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFileText() {
        return fileText;
    }

    public void setFileText(String fileText) {
        this.fileText = fileText;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getTemplateTypeCode() {
        return templateTypeCode;
    }

    public void setTemplateTypeCode(String templateTypeCode) {
        this.templateTypeCode = templateTypeCode;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
