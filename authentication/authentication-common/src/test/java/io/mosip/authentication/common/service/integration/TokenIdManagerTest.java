package io.mosip.authentication.common.service.integration;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.tokenidgenerator.dto.TokenIDResponseDto;
import io.mosip.kernel.tokenidgenerator.service.TokenIDGeneratorService;

/**
 * Test class for Tokenid Manager.
 * 
 * @author Prem kumar
 * @author Nagarjuna
 *
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
public class TokenIdManagerTest {

	@InjectMocks
	private TokenIdManager tokenIdManager;

	@Mock
	TokenIDGeneratorService tokenIDGeneratorService;

	@BeforeEach
	public void before() {
		ReflectionTestUtils.setField(tokenIdManager, "tokenIDGeneratorService", tokenIDGeneratorService);
	}
	
	@Test
	public void generateTokenIdTest_S1() throws IdAuthenticationBusinessException {
		String uin = "3568174910";
		String partnerCode = "1873299273";
		TokenIDResponseDto response = new TokenIDResponseDto();
		response.setTokenID("294283191679206709381119968230906377");
		Mockito.when(tokenIDGeneratorService.generateTokenID(uin, partnerCode)).thenReturn(response);
		tokenIdManager.generateTokenId(uin,partnerCode);
	}
	
	@Test
	public void generateTokenIdTest_S2() throws IdAuthenticationBusinessException {
		String uin = "3568174910";
		String partnerCode = "1873299273";
		TokenIDResponseDto response = new TokenIDResponseDto();
		response.setTokenID("294283191679206709381119968230906377");
		Mockito.when(tokenIDGeneratorService.generateTokenID(uin, partnerCode)).thenReturn(response);
		tokenIdManager.generateTokenId(uin,partnerCode);
	}
	
	@Test
	public void generateTokenIdTest_S3() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		String uin = "3568174910";
		String partnerCode = "1873299273";		
		tokenIdManager.generateTokenId(uin,partnerCode);
			});
	}
	
	@Test
	public void generateTokenIdTest_S4() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		String uin = "3568174910";
		String partnerCode = "1873299273";		
		Mockito.when(tokenIDGeneratorService.generateTokenID(uin, partnerCode)).thenReturn(null);
		tokenIdManager.generateTokenId(uin,partnerCode);
			});
	}
}
