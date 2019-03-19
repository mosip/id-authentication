package io.mosip.registration.processor.print.service.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class JsonRequestDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String nameLang1;
	
	private String addressLine1Lang1;
	
	private String addressLine2Lang1;
	
	private String addressLine3Lang1;
	
	private String regionLang1;
	
	private String provinceLang1;
	
	private String cityLang2;

	private String nameLang2;

	private String addressLine1Lang2;

	private String addressLine2Lang2;

	private String addressLine3Lang2;

	private String regionLang2;

	private String provinceLang2;

	private String cityLang1;
	
	private String postalCode;
	
	private String phoneNumber;

}
