/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateResponseDto;
import io.mosip.kernel.auth.entities.otp.OtpTemplateDto;
import io.mosip.kernel.auth.entities.otp.OtpTemplateResponseDto;
import io.mosip.kernel.auth.service.OTPTemplateService;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class OTPTemplateServiceImpl implements OTPTemplateService {
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	MosipEnvironment mosipEnvironment;

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.OTPTemplateService#getTemplatesBasedOnAppId(io.mosip.kernel.auth.entities.MosipUserDto, java.lang.String)
	 */
	@Override
	public String getTemplatesBasedOnAppId(MosipUserDto mosipUserDto, String channel) {
		
		return null;
	}
	
	private String getOtpEmailMessage(OtpGenerateResponseDto otpGenerateResponseDto) {
        try {
            final String url = mosipEnvironment.getMasterDataUrl()
                    + mosipEnvironment.getMasterDataTemplateApi()
                    + "eng"
                    + mosipEnvironment.getMasterDataOtpTemplate();

            OtpTemplateResponseDto otpTemplateResponseDto = restTemplate.getForObject(url, OtpTemplateResponseDto.class);
            OtpTemplateDto otpTemplateDto = otpTemplateResponseDto.getTemplates().get(0);
            String template = otpTemplateDto.getFileText();
            template.replace("$otp", otpGenerateResponseDto.getOtp());
            return template;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

}
