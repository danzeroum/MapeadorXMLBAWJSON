package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa a configuração de integração com o Microsoft Office,
 * especificamente com o SharePoint, dentro de um processo no IBM BAW.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "officeIntegration")
public class OfficeIntegration {

    @XmlElement
    private String sharePointUserName;

    @XmlElement
    private String sharePointPassword; // Mapeado como String para acomodar a tag vazia

    @XmlElement
    private String sharePointURL;

    @XmlElement
    private boolean sharePointParentSiteDisabled;

    @XmlElement
    private String sharePointParentSiteName;

    @XmlElement
    private String sharePointParentSiteTemplate;

    @XmlElement
    private String sharePointWorkspaceSiteName;

    @XmlElement
    private String sharePointWorkspaceSiteDescription;

    @XmlElement
    private String sharePointWorkspaceSiteTemplate;

    @XmlElement
    private int sharePointLCID;


    // --- Getters e Setters ---

    public String getSharePointUserName() {
        return sharePointUserName;
    }

    public void setSharePointUserName(String sharePointUserName) {
        this.sharePointUserName = sharePointUserName;
    }

    public String getSharePointPassword() {
        return sharePointPassword;
    }

    public void setSharePointPassword(String sharePointPassword) {
        this.sharePointPassword = sharePointPassword;
    }

    public String getSharePointURL() {
        return sharePointURL;
    }

    public void setSharePointURL(String sharePointURL) {
        this.sharePointURL = sharePointURL;
    }

    public boolean isSharePointParentSiteDisabled() {
        return sharePointParentSiteDisabled;
    }

    public void setSharePointParentSiteDisabled(boolean sharePointParentSiteDisabled) {
        this.sharePointParentSiteDisabled = sharePointParentSiteDisabled;
    }

    public String getSharePointParentSiteName() {
        return sharePointParentSiteName;
    }

    public void setSharePointParentSiteName(String sharePointParentSiteName) {
        this.sharePointParentSiteName = sharePointParentSiteName;
    }

    public String getSharePointParentSiteTemplate() {
        return sharePointParentSiteTemplate;
    }

    public void setSharePointParentSiteTemplate(String sharePointParentSiteTemplate) {
        this.sharePointParentSiteTemplate = sharePointParentSiteTemplate;
    }

    public String getSharePointWorkspaceSiteName() {
        return sharePointWorkspaceSiteName;
    }

    public void setSharePointWorkspaceSiteName(String sharePointWorkspaceSiteName) {
        this.sharePointWorkspaceSiteName = sharePointWorkspaceSiteName;
    }

    public String getSharePointWorkspaceSiteDescription() {
        return sharePointWorkspaceSiteDescription;
    }

    public void setSharePointWorkspaceSiteDescription(String sharePointWorkspaceSiteDescription) {
        this.sharePointWorkspaceSiteDescription = sharePointWorkspaceSiteDescription;
    }

    public String getSharePointWorkspaceSiteTemplate() {
        return sharePointWorkspaceSiteTemplate;
    }

    public void setSharePointWorkspaceSiteTemplate(String sharePointWorkspaceSiteTemplate) {
        this.sharePointWorkspaceSiteTemplate = sharePointWorkspaceSiteTemplate;
    }

    public int getSharePointLCID() {
        return sharePointLCID;
    }

    public void setSharePointLCID(int sharePointLCID) {
        this.sharePointLCID = sharePointLCID;
    }
}