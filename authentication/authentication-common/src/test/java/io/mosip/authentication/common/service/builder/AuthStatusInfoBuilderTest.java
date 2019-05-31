package io.mosip.authentication.common.service.builder;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.impl.match.PinMatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;

public class AuthStatusInfoBuilderTest {

	@InjectMocks
	private AuthStatusInfoBuilder authStatusInfoBuilder;

	@Test
	public void TestConstructOTPError() {
		MatchOutput matchOutput = new MatchOutput(0, false, IdaIdMapping.OTP.getIdname(), PinMatchType.OTP, null);
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructOTPError", matchOutput,
				authStatusInfoBuilder);
	}

	@Test
	public void TestconstructBioError() {
		MatchOutput matchOutput = new MatchOutput(0, false, IdaIdMapping.FACE.getIdname(), BioMatchType.FACE, null);
		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		ReflectionTestUtils.invokeMethod(authStatusInfoBuilder, "constructBioError", matchOutput,
				authStatusInfoBuilder);
	}

}
