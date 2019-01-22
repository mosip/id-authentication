package io.mosip.kernel.ldap.entities;

public class MosipUser {
    private String userName;
    private String mobile;
    private String mail;
    private String role;

    public MosipUser() {
    }

    public MosipUser(String userName, String mobile, String mail, String role) {
        this.userName = userName;
        this.mobile = mobile;
        this.mail = mail;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
