package io.mosip.authentication.esignet.integration.dto;

import java.util.List;

import io.mosip.esignet.api.dto.KycSigningCertificateData;
import lombok.Data;

@Data
public class GetAllCertificatesResponse {
	
	private List<KycSigningCertificateData> allCertificates;

}
