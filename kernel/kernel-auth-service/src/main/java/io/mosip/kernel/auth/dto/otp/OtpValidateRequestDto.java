package io.mosip.kernel.auth.dto.otp;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class OtpValidateRequestDto {
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
