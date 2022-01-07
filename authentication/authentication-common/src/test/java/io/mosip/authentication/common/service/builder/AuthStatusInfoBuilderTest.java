package io.mosip.authentication.common.service.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.FingerPrintMatchingStrategy;
import io.mosip.authentication.common.service.impl.match.FullAddressMatchingStrategy;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.impl.match.NameMatchingStrategy;
import io.mosip.authentication.common.service.impl.match.OtpMatchingStrategy;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.common.service.impl.match.PinMatchType;
import io.mosip.authentication.common.service.impl.match.PinMatchingStrategy;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

public class AuthStatusInfoBuilderTest {

	@InjectMocks
	private AuthStatusInfoBuilder authStatusInfoBuilder;

	@Test
	public void TestConstructOTPError() {
		MatchOutput matchOutput = new MatchOutput(0, false, IdaIdMapping.OTP.getIdname(), PinMatchType.OTP, null, "id");
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructOTPError", matchOutput,
				authStatusInfoBuilder);
	}

	@Test
	public void TestconstructBioError() {
		MatchOutput matchOutput = new MatchOutput(0, false, IdaIdMapping.FACE.getIdname(), BioMatchType.FACE, null, "id");
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructBioError", matchOutput,
				authStatusInfoBuilder);
	}
	
	@Test
	public void TestconstructPinError() {
		MatchOutput matchOutput = new MatchOutput(0, false, IdaIdMapping.PIN.getIdname(), PinMatchType.SPIN, null, "id");
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructPinError", matchOutput,
				authStatusInfoBuilder);
	}
	
	@Test
	public void TestconstructDemoErrorForDOB() {
		MatchOutput matchOutput = new MatchOutput(0, false, IdaIdMapping.DOB.getIdname(), DemoMatchType.DOB, null, "id");
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		IDAMappingConfig idMappingConfig = Mockito.mock(IDAMappingConfig.class);
		Mockito.when(idMappingConfig.getFace()).thenReturn(new ArrayList<String>());
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructDemoError", matchOutput,
				authStatusInfoBuilder, idMappingConfig);
	}
	
	@Test
	public void TestconstructDemoErrorForAddresLine() {
		MatchOutput matchOutput = new MatchOutput(0, true, MatchingStrategyType.EXACT.toString(),
				DemoMatchType.ADDR_LINE1, "eng", IdaIdMapping.ADDRESSLINE1.getIdname());
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		IDAMappingConfig idMappingConfig = Mockito.mock(IDAMappingConfig.class);
		Mockito.when(idMappingConfig.getFullAddress()).thenReturn(List.of(IdaIdMapping.ADDRESSLINE1.getIdname()));
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructDemoError", matchOutput,
				authStatusInfoBuilder, idMappingConfig);
		AuthStatusInfo authStatusInfo = authStatusInfoBuilder.build();
		List<AuthError> err = authStatusInfo.getErr();
		assertEquals(1, err.size());
		assertEquals(err.get(0).getErrorCode(), IdAuthenticationErrorConstants.DEMOGRAPHIC_DATA_MISMATCH_LANG.getErrorCode());
		assertEquals(err.get(0).getErrorMessage(), "Demographic data address line item(s) in eng did not match");
	}
	
	@Test
	public void TestconstructDemoErrorForName() {
		MatchOutput matchOutput = new MatchOutput(0, true, MatchingStrategyType.EXACT.toString(),
				DemoMatchType.NAME, "eng", IdaIdMapping.NAME.getIdname());
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		IDAMappingConfig idMappingConfig = Mockito.mock(IDAMappingConfig.class);
		Mockito.when(idMappingConfig.getName()).thenReturn(List.of(IdaIdMapping.NAME.getIdname()));
		Mockito.when(idMappingConfig.getFullAddress()).thenReturn(List.of(IdaIdMapping.NAME.getIdname()));
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructDemoError", matchOutput,
				authStatusInfoBuilder, idMappingConfig);
		AuthStatusInfo authStatusInfo = authStatusInfoBuilder.build();
		List<AuthError> err = authStatusInfo.getErr();
		assertEquals(1, err.size());
		assertEquals(err.get(0).getErrorCode(), IdAuthenticationErrorConstants.DEMOGRAPHIC_DATA_MISMATCH_LANG.getErrorCode());
		assertEquals(err.get(0).getErrorMessage(), "Demographic data name in eng did not match");
	}
	
	@Test
	public void TestconstructDemoErrorForAge() {
		MatchOutput matchOutput = new MatchOutput(0, false, MatchingStrategyType.EXACT.toString(),
				DemoMatchType.AGE, "eng", IdaIdMapping.AGE.getIdname());
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		IDAMappingConfig idMappingConfig = Mockito.mock(IDAMappingConfig.class);
		Mockito.when(idMappingConfig.getName()).thenReturn(List.of(IdaIdMapping.DOB.getIdname()));
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructDemoError", matchOutput,
				authStatusInfoBuilder, idMappingConfig);
		AuthStatusInfo authStatusInfo = authStatusInfoBuilder.build();
		List<AuthError> err = authStatusInfo.getErr();
		assertEquals(1, err.size());
		assertEquals(err.get(0).getErrorCode(), IdAuthenticationErrorConstants.DEMOGRAPHIC_DATA_MISMATCH_LANG.getErrorCode());
		assertEquals(err.get(0).getErrorMessage(), "Demographic data age did not match");
	}
	
	@Test
	public void TestconstructDemoErrorForNameWithNameInFullAddress() {
		MatchOutput matchOutput = new MatchOutput(0, true, MatchingStrategyType.EXACT.toString(),
				DemoMatchType.NAME, "eng", IdaIdMapping.NAME.getIdname());
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		IDAMappingConfig idMappingConfig = Mockito.mock(IDAMappingConfig.class);
		Mockito.when(idMappingConfig.getName()).thenReturn(List.of(IdaIdMapping.NAME.getIdname()));
		Mockito.when(idMappingConfig.getFullAddress()).thenReturn(List.of(IdaIdMapping.NAME.getIdname()));
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructDemoError", matchOutput,
				authStatusInfoBuilder, idMappingConfig);
		AuthStatusInfo authStatusInfo = authStatusInfoBuilder.build();
		List<AuthError> err = authStatusInfo.getErr();
		assertEquals(1, err.size());
		assertEquals(err.get(0).getErrorCode(), IdAuthenticationErrorConstants.DEMOGRAPHIC_DATA_MISMATCH_LANG.getErrorCode());
		assertEquals(err.get(0).getErrorMessage(), "Demographic data name in eng did not match");
	}
	
	@Test
	public void TestconstructDemoWrongMatchType() {
		MatchOutput matchOutput = new MatchOutput(0, false, IdaIdMapping.NAME.getIdname(), BioMatchType.FACE, null, "id");
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		IDAMappingConfig idMappingConfig = Mockito.mock(IDAMappingConfig.class);
		Mockito.when(idMappingConfig.getFace()).thenReturn(new ArrayList<String>());
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructDemoError", matchOutput,
				authStatusInfoBuilder, idMappingConfig);
	}
	
	@Test
	public void TestBuild() {
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		authStatusInfoBuilder.setStatus(true);
		AuthStatusInfo statusInfo = authStatusInfoBuilder.build();
		assertTrue(statusInfo.isStatus());
	}
	
	@Test
	public void TestBuildAuthStatusInfo() {
		List<MatchInput> listMatchInputs = new ArrayList<>();
		MatchInput matchInput = new MatchInput(DemoAuthType.PERSONAL_IDENTITY, DemoMatchType.NAME.getIdMapping().getIdname(), DemoMatchType.NAME, NameMatchingStrategy.EXACT.name(), 60, new HashMap<>(), "fra");
		listMatchInputs.add(matchInput);
		matchInput = new MatchInput(DemoAuthType.FULL_ADDRESS, DemoMatchType.ADDR.getIdMapping().getIdname(), DemoMatchType.ADDR, FullAddressMatchingStrategy.EXACT.name(), 60, new HashMap<>(), "fra");
		listMatchInputs.add(matchInput);
		matchInput = new MatchInput(BioAuthType.FGR_IMG,  BioMatchType.FGRIMG_LEFT_INDEX.getIdMapping().getIdname(), BioMatchType.FGRIMG_LEFT_INDEX, FingerPrintMatchingStrategy.PARTIAL.name(), 60, new HashMap<>(), null);
		listMatchInputs.add(matchInput);
		matchInput = new MatchInput(PinAuthType.OTP, PinMatchType.OTP.getIdMapping().getIdname(), PinMatchType.OTP, OtpMatchingStrategy.EXACT.name(), 60, new HashMap<>(), null);
		listMatchInputs.add(matchInput);
		matchInput = new MatchInput(PinAuthType.SPIN, PinMatchType.SPIN.getIdMapping().getIdname(), PinMatchType.SPIN, PinMatchingStrategy.EXACT.name(), 60, new HashMap<>(), null);
		listMatchInputs.add(matchInput);
		
		List<MatchOutput> listMatchOutputs = new ArrayList<>();
		MatchOutput matchOutput = new MatchOutput(0, false, IdaIdMapping.NAME.getIdname(), DemoMatchType.NAME, null, "id");
		listMatchOutputs.add(matchOutput);
		matchOutput = new MatchOutput(100, true, IdaIdMapping.FULLADDRESS.getIdname(), DemoMatchType.ADDR, null, "id");
		listMatchOutputs.add(matchOutput);
		matchOutput = new MatchOutput(0, false, IdaIdMapping.LEFTINDEX.getIdname(), BioMatchType.FGRIMG_LEFT_INDEX, null, "id");
		listMatchOutputs.add(matchOutput);
		matchOutput = new MatchOutput(0, false, IdaIdMapping.OTP.getIdname(), PinMatchType.OTP, null, "id");
		listMatchOutputs.add(matchOutput);
		matchOutput = new MatchOutput(0, false, IdaIdMapping.PIN.getIdname(), PinMatchType.SPIN, null, "id");
		listMatchOutputs.add(matchOutput);

		AuthType[] authTypes = DemoAuthType.values();
		IDAMappingConfig idMappingConfig = Mockito.mock(IDAMappingConfig.class);
		Mockito.when(idMappingConfig.getName()).thenReturn(new ArrayList<String>());
		AuthStatusInfoBuilder.buildStatusInfo(true, listMatchInputs, listMatchOutputs, authTypes, idMappingConfig);
	}


}
