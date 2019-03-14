package io.mosip.registration.processor.print.service.dto;

import lombok.Data;

@Data
public class JsonRequestDTO {

	private String nameEng;

	private String nameAra;

	private String phoneNumber;

	private String addressLine1Ara;

	private String addressLine1Eng;

	private String addressLine2Ara;

	private String addressLine2Eng;

	private String addressLine3Ara;

	private String addressLine3Eng;

	private String regionAra;

	private String regionEng;

	private String provinceAra;

	private String provinceEng;

	private String postalCode;

	private String cityAra;

	private String cityEng;

}
