package io.mosip.kernel.auth.dto.otp;

import java.util.ArrayList;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class OtpTemplateResponseDto {
    private ArrayList<OtpTemplateDto> templates;

    public ArrayList<OtpTemplateDto> getTemplates() {
        return templates;
    }

    public void setTemplates(ArrayList<OtpTemplateDto> templates) {
        this.templates = templates;
    }
}
