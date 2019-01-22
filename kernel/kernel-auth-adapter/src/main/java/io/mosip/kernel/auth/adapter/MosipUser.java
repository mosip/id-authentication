package io.mosip.kernel.auth.adapter;

/***********************************************************************************************************************
 * MOSIP USER IS THE STANDARD SPEC THAT WILL BE TUNED BASED ON THE DETAILS STORED IN LDAP FOR A USER
 **********************************************************************************************************************/

public class MosipUser {
    private String userName;
    private String mobile;
    private String mail;
    private String role;

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
