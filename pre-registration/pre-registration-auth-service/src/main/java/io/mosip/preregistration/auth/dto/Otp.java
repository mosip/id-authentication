package io.mosip.preregistration.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Otp {
	private String userId;
    private String langCode;
}
