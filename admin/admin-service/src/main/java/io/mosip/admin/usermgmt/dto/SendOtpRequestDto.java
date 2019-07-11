package io.mosip.admin.usermgmt.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.mosip.admin.constant.AdminConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendOtpRequestDto {
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String appId;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String context;
	@NotNull
	private List<String> otpChannel;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String userId;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private String useridtype;
	@NotBlank(message = AdminConstant.INVALID_REQUEST)
	private Object templateVariables;
}
