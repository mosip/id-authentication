package io.mosip.registration.processor.token.validator.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.core.token.validation.exception.AccessDeniedException;
import io.mosip.registration.processor.core.token.validation.exception.InvalidTokenException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TokenValidator.class })
public class TokenValidatorTest {

	@Mock
	Environment env;

	@InjectMocks
	TokenValidator tokenValidator;

	private Logger fooLogger;

	private ListAppender<ILoggingEvent> listAppender;

	private HttpsURLConnection huc;

	@Before
	public void setUp() throws ProtocolException {
		when(env.getProperty("TOKENVALIDATE")).thenReturn("http:localhost:8080/random/url");

		fooLogger = (Logger) LoggerFactory.getLogger(TokenValidator.class);
		listAppender = new ListAppender<>();

		huc = Mockito.mock(HttpsURLConnection.class);
		Mockito.doNothing().when(huc).setRequestProperty(anyString(), anyString());
		Mockito.doNothing().when(huc).setRequestMethod(anyString());
	}

	@Test
	public void testSuccessValidation() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);

		String response = "{\r\n" + "  \"id\": null,\r\n" + "  \"version\": null,\r\n"
				+ "  \"responsetime\": \"2019-04-09T06:52:17.714Z\",\r\n" + "  \"metadata\": null,\r\n"
				+ "  \"response\": {\r\n" + "    \"userId\": \"registrationprocessor\",\r\n"
				+ "    \"mobile\": \"123456789\",\r\n" + "    \"mail\": \"dummy.man@mindtree.com\",\r\n"
				+ "    \"langCode\": null,\r\n" + "    \"userPassword\": null,\r\n"
				+ "    \"name\": \"registrationprocessor\",\r\n" + "    \"role\": \"REGISTRATION_PROCESSOR\"\r\n"
				+ "  },\r\n" + "  \"errors\": null\r\n" + "}";
		InputStream stream = IOUtils.toInputStream(response, "UTF-8");
		Mockito.when(huc.getInputStream()).thenReturn(stream);

		URLConnection urlCon = (URLConnection) huc;
		URL urlObj = PowerMockito.mock(URL.class);

		PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(urlObj);
		PowerMockito.when(urlObj.openConnection()).thenReturn(urlCon);
		tokenValidator.validate("token_string", "/receiver");

		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.contains(Tuple.tuple(Level.INFO,
						"SESSIONID - REGISTRATIONID - Token Validation Successful For Role:  - REGISTRATION_PROCESSOR"));
	}

	@Test(expected = AccessDeniedException.class)
	public void testAccessDeniedValidation() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);

		String response = "{\r\n" + "  \"id\": null,\r\n" + "  \"version\": null,\r\n"
				+ "  \"responsetime\": \"2019-04-09T06:52:17.714Z\",\r\n" + "  \"metadata\": null,\r\n"
				+ "  \"response\": {\r\n" + "    \"userId\": \"registrationprocessor\",\r\n"
				+ "    \"mobile\": \"123456789\",\r\n" + "    \"mail\": \"dummy.man@mindtree.com\",\r\n"
				+ "    \"langCode\": null,\r\n" + "    \"userPassword\": null,\r\n"
				+ "    \"name\": \"registrationprocessor\",\r\n" + "    \"role\": \"REGISTRATI\"\r\n"
				+ "  },\r\n" + "  \"errors\": null\r\n" + "}";
		InputStream stream = IOUtils.toInputStream(response, "UTF-8");
		Mockito.when(huc.getInputStream()).thenReturn(stream);

		URLConnection urlCon = (URLConnection) huc;
		URL urlObj = PowerMockito.mock(URL.class);

		PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(urlObj);
		PowerMockito.when(urlObj.openConnection()).thenReturn(urlCon);
		tokenValidator.validate("token_string", "/receiver");

	}

	@Test(expected = InvalidTokenException.class)
	public void testInvalidTokenValidation() throws Exception {

		listAppender.start();
		fooLogger.addAppender(listAppender);

		String response = "{\r\n" + "  \"id\": null,\r\n" + "  \"version\": null,\r\n"
				+ "  \"responsetime\": \"2019-04-09T07:23:11.605Z\",\r\n" + "  \"metadata\": null,\r\n"
				+ "  \"response\": null,\r\n" + "  \"errors\": [\r\n" + "    {\r\n"
				+ "      \"errorCode\": \"KER-ATH-401\",\r\n"
				+ "      \"message\": \"JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.\"\r\n"
				+ "    }\r\n" + "  ]\r\n" + "}";
		InputStream stream = IOUtils.toInputStream(response, "UTF-8");
		Mockito.when(huc.getInputStream()).thenReturn(stream);

		URLConnection urlCon = (URLConnection) huc;
		URL urlObj = PowerMockito.mock(URL.class);

		PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(urlObj);
		PowerMockito.when(urlObj.openConnection()).thenReturn(urlCon);
		tokenValidator.validate("token_sample", "/receiver");

	}

	@Test
	public void testValidateAccess() {
		assertTrue(tokenValidator.validateAccess("/receiver", "REGISTRATION_PROCESSOR"));
		assertTrue(tokenValidator.validateAccess("/registration-status", "REGISTRATION_OFFICER"));
		assertTrue(tokenValidator.validateAccess("/registration-sync", "REGISTRATION_PROCESSOR"));
		assertTrue(tokenValidator.validateAccess("/biodedupe", "REGISTRATION_PROCESSOR"));
		assertTrue(tokenValidator.validateAccess("/printing", "REGISTRATION_PROCESSOR"));
		assertTrue(tokenValidator.validateAccess("/manual-verification", "REGISTRATION_ADMIN"));
		assertFalse(tokenValidator.validateAccess("/receiver", "MANUAL_VERIFIER"));
	}
}