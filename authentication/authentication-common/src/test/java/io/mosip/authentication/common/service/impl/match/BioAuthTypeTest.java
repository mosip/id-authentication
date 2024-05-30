package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.TriFunctionWithBusinessException;

public class BioAuthTypeTest {
	
	ObjectMapper mapper = new ObjectMapper();

	private void testSingleBioAuthType(BioAuthType testSubject, String bioType, boolean single) throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		authRequestDTO.setRequest(request);
		IdInfoFetcher idInfoFetcher = Mockito.mock(IdInfoFetcher.class);
		String language = "";
		Map<String, Object> result;

		@SuppressWarnings("unchecked")
		TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> matchFunction = Mockito.mock(TriFunctionWithBusinessException.class);
		// default test
		Mockito.when(idInfoFetcher.getMatchFunction(Mockito.any())).thenReturn(matchFunction);
		Mockito.when(matchFunction.apply(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(60.0);
		result = testSubject.getMatchProperties(authRequestDTO, idInfoFetcher, language);
		
		assertTrue(result.isEmpty());
		
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		
		DataDTO data = new DataDTO();
		data.setBioType(bioType);
		data.setBioSubType("UNKNOWN");
		BioIdentityInfoDTO bioIdentity = new BioIdentityInfoDTO(data, "", "", "", null);
		biometrics.add(bioIdentity);
		
		if(!single) {
			data = new DataDTO();
			data.setBioType(bioType);
			bioIdentity = new BioIdentityInfoDTO(data, "", "", "", null);
			biometrics.add(bioIdentity);
		}
		
		request.setBiometrics(biometrics);
		
		result = testSubject.getMatchProperties(authRequestDTO, idInfoFetcher, language);
		assertTrue(!result.isEmpty());
	}
	
	
	@Test
	public void testGetMatchPropertiesFgrMinSingle() throws Exception {
		testSingleBioAuthType(BioAuthType.FGR_MIN, "FMR", true);
	}
	
	@Test
	public void testGetMatchPropertiesFgrImgSingle() throws Exception {
		testSingleBioAuthType(BioAuthType.FGR_IMG, "Finger", true);
	}
	
	
	@Test
	public void testGetMatchPropertiesIIRSingle() throws Exception {
		testSingleBioAuthType(BioAuthType.IRIS_IMG, "Iris", true);
	}
	
	@Test
	public void testGetMatchPropertiesFace() throws Exception {
		testSingleBioAuthType(BioAuthType.FACE_IMG, "FACE", true);
	}
	
	@Test
	public void testGetMatchPropertiesFgrMinMulti() throws Exception {
		testSingleBioAuthType(BioAuthType.FGR_MIN_COMPOSITE, "FMR", false);
	}
	
	@Test
	public void testGetMatchPropertiesFgrImgMulti() throws Exception {
		testSingleBioAuthType(BioAuthType.FGR_IMG_COMPOSITE, "Finger", false);
	}
	
	
	@Test
	public void testGetMatchPropertiesIIRMulti() throws Exception {
		testSingleBioAuthType(BioAuthType.IRIS_COMP_IMG, "Iris", false);
	}
	
	@Test
	public void testGetMatchPropertiesMultiModal() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		authRequestDTO.setRequest(request);
		IdInfoFetcher idInfoFetcher = Mockito.mock(IdInfoFetcher.class);
		String language = "";
		Map<String, Object> result;

		// default test
		@SuppressWarnings("unchecked")
		TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> matchFunction = Mockito.mock(TriFunctionWithBusinessException.class);
		// default test
		Mockito.when(idInfoFetcher.getMatchFunction(Mockito.any())).thenReturn(matchFunction);
		Mockito.when(matchFunction.apply(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(60.0);
		
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		
		DataDTO data = new DataDTO();
		data.setBioType("Finger");
		data.setBioSubType("UNKNOWN");
		BioIdentityInfoDTO bioIdentity = new BioIdentityInfoDTO(data, "", "", "", null);
		biometrics.add(bioIdentity);
		
		data = new DataDTO();
		data.setBioType("Iris");
		data.setBioSubType("UNKNOWN");
		bioIdentity = new BioIdentityInfoDTO(data, "", "", "", null);
			biometrics.add(bioIdentity);
		
		request.setBiometrics(biometrics);
		
		AuthType testSubject = BioAuthType.MULTI_MODAL;
		result = testSubject.getMatchProperties(authRequestDTO, idInfoFetcher, language);
		assertTrue(!result.isEmpty());
	}
	
}