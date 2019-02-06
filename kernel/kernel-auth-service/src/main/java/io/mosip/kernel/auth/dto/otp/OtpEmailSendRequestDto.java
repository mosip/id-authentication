package io.mosip.kernel.auth.dto.otp;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class OtpEmailSendRequestDto {
    private String email;
    private String message;

    public OtpEmailSendRequestDto(String email, String message) {
        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
