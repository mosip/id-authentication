package io.mosip.kernel.auth.dto;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class LoginUserDto {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
