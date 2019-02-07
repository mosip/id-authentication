package io.mosip.kernel.auth.dto.otp;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class OtpSmsSendResponseDto {
    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
