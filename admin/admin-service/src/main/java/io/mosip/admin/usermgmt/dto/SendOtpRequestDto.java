package io.mosip.admin.usermgmt.dto;

import java.util.List;

import lombok.Data;
@Data
public class SendOtpRequestDto {
private String appId;
private String context;
private List<String> otpChannel;
private String userId;
private String useridtype;
private Object templateVariables;
}
