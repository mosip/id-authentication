package io.mosip.authentication.common.service.filter;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.spi.partner.service.PartnerService;

/**
 * The Class IdAuthFilterTest.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@AutoConfigureMockMvc
@Import(EnvUtil.class)
public class IdAuthFilterTest {

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The filter. */
	IdAuthFilter filter = new IdAuthFilter() {

		@Override
		protected String fetchId(ResettableStreamHttpServletRequest requestWrapper, String attribute) {
			return "";
		}

		@Override
		protected boolean isMispPolicyValidationRequired() {
			return false;
		}

		@Override
		protected boolean isCertificateValidationRequired() {
			return false;
		}

		@Override
		protected boolean isAMRValidationRequired() {
			return false;
		}
	};

	/** The request body. */
	Map<String, Object> requestBody = new HashMap<>();

	/** The response body. */
	Map<String, Object> responseBody = new HashMap<>();

	@Mock
	PartnerService partnerService;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(filter, "mapper", mapper);
		ReflectionTestUtils.setField(filter, "partnerService", partnerService);
	}

	/**
	 * Test decoded request.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws ServletException             the servlet exception
	 * @throws JsonParseException           the json parse exception
	 * @throws JsonMappingException         the json mapping exception
	 * @throws IOException                  Signals that an I/O exception has
	 *                                      occurred.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDecodedRequest()
			throws IdAuthenticationAppException, ServletException, JsonParseException, JsonMappingException, IOException {
		KeyManager keyManager = Mockito.mock(KeyManager.class);
		ReflectionTestUtils.setField(filter, "keyManager", keyManager);
		requestBody.put("request",
				"ew0KCSJhdXRoVHlwZSI6IHsNCgkJImFkZHJlc3MiOiAidHJ1ZSIsDQoJCSJiaW8iOiAidHJ1ZSIsDQoJCSJmYWNlIjogInRydWUiLA0KCQkiZmluZ2VycHJpbnQiOiAidHJ1ZSIsDQoJCSJmdWxsQWRkcmVzcyI6ICJ0cnVlIiwNCgkJImlyaXMiOiAidHJ1ZSIsDQoJCSJvdHAiOiAidHJ1ZSIsDQoJCSJwZXJzb25hbElkZW50aXR5IjogInRydWUiLA0KCQkicGluIjogInRydWUiDQoJfQ0KfQ==");
		requestBody.put("requestHMAC", "B93ACCB8D7A0B005864F684FB1F53A833BAF547ED4D610C5057DE6B55A4EF76C");
		requestBody.put("requestSessionKey", "B93ACCB8D7A0B005864F684FB1F53A833BAF547ED4D610C5057DE6B55A4EF76C");
		responseBody.put("request",
				"{authType={address=true, bio=true, face=true, fingerprint=true, fullAddress=true, iris=true, otp=true, personalIdentity=true, pin=true}}");
		String dicipheredreq = "{\"authType\":{\"address\":\"true\",\"bio\":\"true\",\"face\":\"true\",\"fingerprint\":\"true\",\"fullAddress\":\"true\",\"iris\":\"true\",\"otp\":\"true\",\"personalIdentity\":\"true\",\"pin\":\"true\"}}";
		Mockito.when(
				keyManager.requestData(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(new ObjectMapper().readValue(dicipheredreq.getBytes(), Map.class));
		Mockito.when(keyManager.kernelDecryptAndDecode(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn("B93ACCB8D7A0B005864F684FB1F53A833BAF547ED4D610C5057DE6B55A4EF76C");
		Map<String, Object> decipherRequest = filter.decipherRequest(requestBody);
		decipherRequest.remove("requestHMAC");
		decipherRequest.remove("requestSessionKey");
		assertEquals(responseBody.toString(), decipherRequest.toString());
	}

	/**
	 * Test in valid decoded request.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws JsonParseException           the json parse exception
	 * @throws JsonMappingException         the json mapping exception
	 * @throws IOException                  Signals that an I/O exception has
	 *                                      occurred.
	 */
	@Test(expected = IdAuthenticationAppException.class)
	public void testInValidDecodedRequest()
			throws IdAuthenticationAppException, JsonParseException, JsonMappingException, IOException {
		KeyManager keyManager = Mockito.mock(KeyManager.class);
		ReflectionTestUtils.setField(filter, "keyManager", keyManager);
		requestBody.put("request", 123214214);
		filter.decipherRequest(requestBody);
	}

	/**
	 * Valid partner id test.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public void validPartnerIdTest() throws IdAuthenticationAppException {
		ReflectionTestUtils.invokeMethod(filter, "validPartnerId", "1873299273");
	}

	/**
	 * Mandatory auth policy test OTP check.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void mandatoryAuthPolicyTestOTPcheck() throws IdAuthenticationAppException {
		String requestedAuth = "{\"requestedAuth\": {\r\n" + "                             \"bio\": true,\r\n"
				+ "                             \"demo\": false,\r\n" + "                             \"otp\": false,\r\n"
				+ "                             \"pin\": false\r\n" + "              }}";
		try {
			Map<String, Object> requestBodyMap = mapper.readValue(requestedAuth.getBytes("UTF-8"), HashMap.class);

			filter.checkAllowedAuthTypeBasedOnPolicy(getPolicyFor92834787293(), requestBodyMap);
		} catch (IOException e) {
			throw new IdAuthenticationAppException();
		} catch (IdAuthenticationAppException e) {
			assertEquals(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(), e.getErrorCode());
		}
	}

	/**
	 * Mandatory auth policy testbiocheck.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void mandatoryAuthPolicyTestbiocheck() throws IdAuthenticationAppException {
		String requestedAuth = "{\"requestedAuth\": {\r\n" + "                             \"bio\": false,\r\n"
				+ "                             \"demo\": false,\r\n" + "                             \"otp\": true,\r\n"
				+ "                             \"pin\": false\r\n" + "              }}";
		try {
			Map<String, Object> requestBodyMap = mapper.readValue(requestedAuth.getBytes("UTF-8"), HashMap.class);

			filter.checkAllowedAuthTypeBasedOnPolicy(getPolicyFor92834787293(), requestBodyMap);
		} catch (IOException e) {
			throw new IdAuthenticationAppException();
		} catch (IdAuthenticationAppException e) {
			assertEquals(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(), e.getErrorCode());
		}
	}

	/**
	 * Mandatory auth policy testpincheck.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void mandatoryAuthPolicyTestpincheck() throws IdAuthenticationAppException {
		String requestedAuth = "{\"requestedAuth\": {\r\n" + "                             \"bio\": false,\r\n"
				+ "                             \"demo\": false,\r\n" + "                             \"otp\": true,\r\n"
				+ "                             \"pin\": false\r\n" + "              }}";
		try {
			Map<String, Object> requestBodyMap = mapper.readValue(requestedAuth.getBytes("UTF-8"), HashMap.class);

			filter.checkAllowedAuthTypeBasedOnPolicy(getPolicyFor0983222(), requestBodyMap);
		} catch (IOException e) {
			throw new IdAuthenticationAppException();
		} catch (IdAuthenticationAppException e) {
			assertEquals(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(), e.getErrorCode());
		}
	}

	/**
	 * Mandatory auth policy testdemocheck.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void mandatoryAuthPolicyTestdemocheck() throws IdAuthenticationAppException {
		String requestedAuth = "{\"requestedAuth\": {\r\n" + "                             \"bio\": false,\r\n"
				+ "                             \"demo\": false,\r\n" + "                             \"otp\": false,\r\n"
				+ "                             \"pin\": true\r\n" + "              }}";
		try {
			Map<String, Object> requestBodyMap = mapper.readValue(requestedAuth.getBytes("UTF-8"), HashMap.class);
			filter.checkAllowedAuthTypeBasedOnPolicy(getPolicyFor0983222(), requestBodyMap);
		} catch (IOException e) {
			throw new IdAuthenticationAppException();
		} catch (IdAuthenticationAppException e) {
			assertEquals(IdAuthenticationErrorConstants.AUTHTYPE_MANDATORY.getErrorCode(), e.getErrorCode());
		}
	}

	/**
	 * Allowed auth policy test OTP check.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void allowedAuthPolicyTestOTPCheck() throws IdAuthenticationAppException {
		String requestedAuth = "{\"requestedAuth\": {\r\n" + "                             \"bio\": false,\r\n"
				+ "                             \"demo\": false,\r\n" + "                             \"otp\": true,\r\n"
				+ "                             \"pin\": false\r\n" + "              }}";
		try {
			Map<String, Object> requestBodyMap = mapper.readValue(requestedAuth.getBytes("UTF-8"), HashMap.class);
			filter.checkAllowedAuthTypeBasedOnPolicy(getPolicyFor0983252(), requestBodyMap);
		} catch (IOException e) {
			throw new IdAuthenticationAppException();
		} catch (IdAuthenticationAppException e) {
			assertEquals(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(), e.getErrorCode());
		}
	}

	/**
	 * Allowed auth policy test demo check.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Test
	public void allowedAuthPolicyTestDemoCheck() throws IdAuthenticationAppException {
		String requestedAuth = "{\"requestedAuth\": {\r\n" + "                             \"bio\": false,\r\n"
				+ "                             \"demo\": true,\r\n" + "                             \"otp\": false,\r\n"
				+ "                             \"pin\": false\r\n" + "              }}";
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> requestBodyMap = mapper.readValue(requestedAuth.getBytes("UTF-8"), HashMap.class);
			filter.checkAllowedAuthTypeBasedOnPolicy(getPolicyFor0983222(), requestBodyMap);
		} catch (IOException e) {
			throw new IdAuthenticationAppException();
		} catch (IdAuthenticationAppException e) {
			assertEquals(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(), e.getErrorCode());
		}
	}

	/**
	 * Allowed auth policy test pin check.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void allowedAuthPolicyTestPinCheck() throws IdAuthenticationAppException {
		String requestedAuth = "{\"requestedAuth\": {\r\n" + "                             \"bio\": false,\r\n"
				+ "                             \"demo\": false,\r\n" + "                             \"otp\": false,\r\n"
				+ "                             \"pin\": true\r\n" + "              }}";
		try {
			Map<String, Object> requestBodyMap = mapper.readValue(requestedAuth.getBytes("UTF-8"), HashMap.class);
			filter.checkAllowedAuthTypeBasedOnPolicy(getPolicyFor0983754(), requestBodyMap);
		} catch (IOException e) {
			throw new IdAuthenticationAppException();
		} catch (IdAuthenticationAppException e) {
			assertEquals(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(), e.getErrorCode());
		}

	}

	/**
	 * Allowed auth policy test bio check.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void allowedAuthPolicyTestBioCheck() throws IdAuthenticationAppException {
		String requestedAuth = "{\"requestedAuth\": {\r\n" + "                             \"bio\": true,\r\n"
				+ "                             \"demo\": true,\r\n" + "                             \"otp\": true,\r\n"
				+ "                             \"pin\": true\r\n" + "              }}";
		try {
			Map<String, Object> requestBodyMap = mapper.readValue(requestedAuth.getBytes("UTF-8"), HashMap.class);
			filter.checkAllowedAuthTypeBasedOnPolicy(getPolicyFor0123456(), requestBodyMap);
		} catch (IOException e) {
			throw new IdAuthenticationAppException();
		} catch (IdAuthenticationAppException e) {
			assertEquals(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(), e.getErrorCode());
		}
	}

	/**
	 * Validate deciphered request test.
	 *
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = IdAuthenticationAppException.class)
	public void validateDecipheredRequestTest() throws IdAuthenticationAppException {
		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(new HttpServletRequest() {

			@Override
			public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
					throws IllegalStateException {

				return null;
			}

			@Override
			public AsyncContext startAsync() throws IllegalStateException {

				return null;
			}

			@Override
			public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

			}

			@Override
			public void setAttribute(String name, Object o) {

			}

			@Override
			public void removeAttribute(String name) {

			}

			@Override
			public boolean isSecure() {

				return false;
			}

			@Override
			public boolean isAsyncSupported() {

				return false;
			}

			@Override
			public boolean isAsyncStarted() {

				return false;
			}

			@Override
			public ServletContext getServletContext() {

				return null;
			}

			@Override
			public int getServerPort() {

				return 0;
			}

			@Override
			public String getServerName() {

				return null;
			}

			@Override
			public String getScheme() {

				return null;
			}

			@Override
			public RequestDispatcher getRequestDispatcher(String path) {

				return null;
			}

			@Override
			public int getRemotePort() {

				return 0;
			}

			@Override
			public String getRemoteHost() {

				return null;
			}

			@Override
			public String getRemoteAddr() {

				return null;
			}

			@Override
			public String getRealPath(String path) {

				return null;
			}

			@Override
			public BufferedReader getReader() throws IOException {

				return null;
			}

			@Override
			public String getProtocol() {

				return null;
			}

			@Override
			public String[] getParameterValues(String name) {

				return null;
			}

			@Override
			public Enumeration<String> getParameterNames() {

				return null;
			}

			@Override
			public Map<String, String[]> getParameterMap() {

				return null;
			}

			@Override
			public String getParameter(String name) {

				return null;
			}

			@Override
			public Enumeration<Locale> getLocales() {

				return null;
			}

			@Override
			public Locale getLocale() {

				return null;
			}

			@Override
			public int getLocalPort() {

				return 0;
			}

			@Override
			public String getLocalName() {

				return null;
			}

			@Override
			public String getLocalAddr() {

				return null;
			}

			@Override
			public ServletInputStream getInputStream() throws IOException {

				return null;
			}

			@Override
			public DispatcherType getDispatcherType() {

				return null;
			}

			@Override
			public String getContentType() {

				return null;
			}

			@Override
			public long getContentLengthLong() {

				return 0;
			}

			@Override
			public int getContentLength() {

				return 0;
			}

			@Override
			public String getCharacterEncoding() {

				return null;
			}

			@Override
			public Enumeration<String> getAttributeNames() {

				return null;
			}

			@Override
			public Object getAttribute(String name) {

				return null;
			}

			@Override
			public AsyncContext getAsyncContext() {

				return null;
			}

			@Override
			public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass)
					throws IOException, ServletException {

				return null;
			}

			@Override
			public void logout() throws ServletException {

			}

			@Override
			public void login(String username, String password) throws ServletException {

			}

			@Override
			public boolean isUserInRole(String role) {

				return false;
			}

			@Override
			public boolean isRequestedSessionIdValid() {

				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromUrl() {

				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromURL() {

				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromCookie() {

				return false;
			}

			@Override
			public Principal getUserPrincipal() {

				return null;
			}

			@Override
			public HttpSession getSession(boolean create) {

				return null;
			}

			@Override
			public HttpSession getSession() {

				return null;
			}

			@Override
			public String getServletPath() {

				return null;
			}

			@Override
			public String getRequestedSessionId() {

				return null;
			}

			@Override
			public StringBuffer getRequestURL() {

				return new StringBuffer("localhost:8090/identity/auth/0.8/1873299273/735899345");
			}

			@Override
			public String getRequestURI() {

				return null;
			}

			@Override
			public String getRemoteUser() {

				return null;
			}

			@Override
			public String getQueryString() {

				return null;
			}

			@Override
			public String getPathTranslated() {

				return null;
			}

			@Override
			public String getPathInfo() {

				return null;
			}

			@Override
			public Collection<Part> getParts() throws IOException, ServletException {

				return null;
			}

			@Override
			public Part getPart(String name) throws IOException, ServletException {

				return null;
			}

			@Override
			public String getMethod() {

				return null;
			}

			@Override
			public int getIntHeader(String name) {

				return 0;
			}

			@Override
			public Enumeration<String> getHeaders(String name) {

				return null;
			}

			@Override
			public Enumeration<String> getHeaderNames() {

				return null;
			}

			@Override
			public String getHeader(String name) {

				return null;
			}

			@Override
			public long getDateHeader(String name) {

				return 0;
			}

			@Override
			public Cookie[] getCookies() {

				return null;
			}

			@Override
			public String getContextPath() {

				return "identity";
			}

			@Override
			public String getAuthType() {

				return null;
			}

			@Override
			public String changeSessionId() {

				return null;
			}

			@Override
			public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {

				return false;
			}
		});
		String requestedAuth = "{\"requestedAuth\": {\r\n" + "                             \"bio\": true,\r\n"
				+ "                             \"demo\": false,\r\n" + "                             \"otp\": false,\r\n"
				+ "                             \"pin\": false\r\n" + "              }}";

		Map<String, Object> requestBodyMap;
		try {
			requestBodyMap = mapper.readValue(requestedAuth.getBytes("UTF-8"), HashMap.class);
			filter.validateDecipheredRequest(requestWrapper, requestBodyMap);
		} catch (IOException e) {
			throw new IdAuthenticationAppException();
		}

	}

	private PartnerPolicyResponseDTO getPolicyFor92834787293() {
		String policy = "{	\"policyId\": \"92834787293\",	\"policyName\": \"Auth Policy1\",	\"policyDescription\": \"Auth Policy Group1\",	\"policyStatus\": true,	\"policy\": {		\"authTokenType\": \"policy\",		\"allowedKycAttributes\": [{			\"attributeName\": \"fullName\",			\"required\": true		}, {			\"attributeName\": \"dateOfBirth\",			\"required\": true		}, {			\"attributeName\": \"gender\",			\"required\": true		}, {			\"attributeName\": \"phone\",			\"required\": true		}, {			\"attributeName\": \"email\",			\"required\": true		}, {			\"attributeName\": \"addressLine1\",			\"required\": true		}, {			\"attributeName\": \"addressLine2\",			\"required\": true		}, {			\"attributeName\": \"addressLine3\",			\"required\": true		}, {			\"attributeName\": \"region\",			\"required\": true		}, {			\"attributeName\": \"province\",			\"required\": true		}, {			\"attributeName\": \"city\",			\"required\": true		}, {			\"attributeName\": \"postalCode\",			\"required\": false		}, {			\"attributeName\": \"photo\",			\"required\": true		}],		\"allowedAuthTypes\": [{			\"authType\": \"otp-request\",			\"mandatory\": false		}, {			\"authType\": \"otp\",			\"mandatory\": false		}, {			\"authType\": \"demo\",			\"mandatory\": false		}, {			\"authType\": \"kyc\",			\"mandatory\": false		}, {			\"authType\": \"pin\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FINGER\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"IRIS\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FACE\",			\"mandatory\": false		}]	},	\"partnerId\": \"1111\",	\"partnerName\": \"Airtekibop\",	\"mispExpiresOn\": \"2021-05-19T05:58:23.32448\",	\"apiKeyExpiresOn\": \"2021-05-19T05:53:52.633968\",	\"policyExpiresOn\": \"2021-08-17T05:52:56.953157\",	\"certificateData\": null}";
		PartnerPolicyResponseDTO policyDto = null;
		try {
			policyDto = mapper.readValue(policy.getBytes(UTF_8), PartnerPolicyResponseDTO.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return policyDto;

	}

	private PartnerPolicyResponseDTO getPolicyFor0983222() {
		String policy = "{	\"policyId\": \"92834787293\",	\"policyName\": \"Auth Policy1\",	\"policyDescription\": \"Auth Policy Group1\",	\"policyStatus\": true,	\"policy\": {		\"authTokenType\": \"policy\",		\"allowedKycAttributes\": [{			\"attributeName\": \"fullName\",			\"required\": true		}, {			\"attributeName\": \"dateOfBirth\",			\"required\": true		}, {			\"attributeName\": \"gender\",			\"required\": true		}, {			\"attributeName\": \"phone\",			\"required\": true		}, {			\"attributeName\": \"email\",			\"required\": true		}, {			\"attributeName\": \"addressLine1\",			\"required\": true		}, {			\"attributeName\": \"addressLine2\",			\"required\": true		}, {			\"attributeName\": \"addressLine3\",			\"required\": true		}, {			\"attributeName\": \"region\",			\"required\": true		}, {			\"attributeName\": \"province\",			\"required\": true		}, {			\"attributeName\": \"city\",			\"required\": true		}, {			\"attributeName\": \"postalCode\",			\"required\": false		}, {			\"attributeName\": \"photo\",			\"required\": true		}],		\"allowedAuthTypes\": [{			\"authType\": \"otp-request\",			\"mandatory\": false		}, {			\"authType\": \"otp\",			\"mandatory\": false		}, {			\"authType\": \"demo\",			\"mandatory\": false		}, {			\"authType\": \"kyc\",			\"mandatory\": false		}, {			\"authType\": \"pin\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FINGER\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"IRIS\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FACE\",			\"mandatory\": false		}]	},	\"partnerId\": \"1111\",	\"partnerName\": \"Airtekibop\",	\"mispExpiresOn\": \"2021-05-19T05:58:23.32448\",	\"apiKeyExpiresOn\": \"2021-05-19T05:53:52.633968\",	\"policyExpiresOn\": \"2021-08-17T05:52:56.953157\",	\"certificateData\": null}";
		PartnerPolicyResponseDTO policyDto = null;
		try {
			policyDto = mapper.readValue(policy.getBytes(UTF_8), PartnerPolicyResponseDTO.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return policyDto;
	}

	/**
	 * 
	 * @return
	 */
	private PartnerPolicyResponseDTO getPolicyFor0983252() {
		String policy = "{	\"policyId\": \"92834787293\",	\"policyName\": \"Auth Policy1\",	\"policyDescription\": \"Auth Policy Group1\",	\"policyStatus\": true,	\"policy\": {		\"authTokenType\": \"policy\",		\"allowedKycAttributes\": [{			\"attributeName\": \"fullName\",			\"required\": true		}, {			\"attributeName\": \"dateOfBirth\",			\"required\": true		}, {			\"attributeName\": \"gender\",			\"required\": true		}, {			\"attributeName\": \"phone\",			\"required\": true		}, {			\"attributeName\": \"email\",			\"required\": true		}, {			\"attributeName\": \"addressLine1\",			\"required\": true		}, {			\"attributeName\": \"addressLine2\",			\"required\": true		}, {			\"attributeName\": \"addressLine3\",			\"required\": true		}, {			\"attributeName\": \"region\",			\"required\": true		}, {			\"attributeName\": \"province\",			\"required\": true		}, {			\"attributeName\": \"city\",			\"required\": true		}, {			\"attributeName\": \"postalCode\",			\"required\": false		}, {			\"attributeName\": \"photo\",			\"required\": true		}],		\"allowedAuthTypes\": [{			\"authType\": \"otp-request\",			\"mandatory\": false		}, {			\"authType\": \"otp\",			\"mandatory\": false		}, {			\"authType\": \"demo\",			\"mandatory\": false		}, {			\"authType\": \"kyc\",			\"mandatory\": false		}, {			\"authType\": \"pin\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FINGER\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"IRIS\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FACE\",			\"mandatory\": false		}]	},	\"partnerId\": \"1111\",	\"partnerName\": \"Airtekibop\",	\"mispExpiresOn\": \"2021-05-19T05:58:23.32448\",	\"apiKeyExpiresOn\": \"2021-05-19T05:53:52.633968\",	\"policyExpiresOn\": \"2021-08-17T05:52:56.953157\",	\"certificateData\": null}";
		PartnerPolicyResponseDTO policyDto = null;
		try {
			policyDto = mapper.readValue(policy.getBytes(UTF_8), PartnerPolicyResponseDTO.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return policyDto;
	}

	/**
	 * 
	 * @return
	 */
	private PartnerPolicyResponseDTO getPolicyFor0983754() {
		String policy = "{	\"policyId\": \"92834787293\",	\"policyName\": \"Auth Policy1\",	\"policyDescription\": \"Auth Policy Group1\",	\"policyStatus\": true,	\"policy\": {		\"authTokenType\": \"policy\",		\"allowedKycAttributes\": [{			\"attributeName\": \"fullName\",			\"required\": true		}, {			\"attributeName\": \"dateOfBirth\",			\"required\": true		}, {			\"attributeName\": \"gender\",			\"required\": true		}, {			\"attributeName\": \"phone\",			\"required\": true		}, {			\"attributeName\": \"email\",			\"required\": true		}, {			\"attributeName\": \"addressLine1\",			\"required\": true		}, {			\"attributeName\": \"addressLine2\",			\"required\": true		}, {			\"attributeName\": \"addressLine3\",			\"required\": true		}, {			\"attributeName\": \"region\",			\"required\": true		}, {			\"attributeName\": \"province\",			\"required\": true		}, {			\"attributeName\": \"city\",			\"required\": true		}, {			\"attributeName\": \"postalCode\",			\"required\": false		}, {			\"attributeName\": \"photo\",			\"required\": true		}],		\"allowedAuthTypes\": [{			\"authType\": \"otp-request\",			\"mandatory\": false		}, {			\"authType\": \"otp\",			\"mandatory\": false		}, {			\"authType\": \"demo\",			\"mandatory\": false		}, {			\"authType\": \"kyc\",			\"mandatory\": false		}, {			\"authType\": \"pin\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FINGER\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"IRIS\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FACE\",			\"mandatory\": false		}]	},	\"partnerId\": \"1111\",	\"partnerName\": \"Airtekibop\",	\"mispExpiresOn\": \"2021-05-19T05:58:23.32448\",	\"apiKeyExpiresOn\": \"2021-05-19T05:53:52.633968\",	\"policyExpiresOn\": \"2021-08-17T05:52:56.953157\",	\"certificateData\": null}";
		PartnerPolicyResponseDTO policyDto = null;
		try {
			policyDto = mapper.readValue(policy.getBytes(UTF_8), PartnerPolicyResponseDTO.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return policyDto;
	}

	/**
	 * 
	 * @return
	 */
	private PartnerPolicyResponseDTO getPolicyFor0123456() {
		String policy = "{	\"policyId\": \"92834787293\",	\"policyName\": \"Auth Policy1\",	\"policyDescription\": \"Auth Policy Group1\",	\"policyStatus\": true,	\"policy\": {		\"authTokenType\": \"policy\",		\"allowedKycAttributes\": [{			\"attributeName\": \"fullName\",			\"required\": true		}, {			\"attributeName\": \"dateOfBirth\",			\"required\": true		}, {			\"attributeName\": \"gender\",			\"required\": true		}, {			\"attributeName\": \"phone\",			\"required\": true		}, {			\"attributeName\": \"email\",			\"required\": true		}, {			\"attributeName\": \"addressLine1\",			\"required\": true		}, {			\"attributeName\": \"addressLine2\",			\"required\": true		}, {			\"attributeName\": \"addressLine3\",			\"required\": true		}, {			\"attributeName\": \"region\",			\"required\": true		}, {			\"attributeName\": \"province\",			\"required\": true		}, {			\"attributeName\": \"city\",			\"required\": true		}, {			\"attributeName\": \"postalCode\",			\"required\": false		}, {			\"attributeName\": \"photo\",			\"required\": true		}],		\"allowedAuthTypes\": [{			\"authType\": \"otp-request\",			\"mandatory\": false		}, {			\"authType\": \"otp\",			\"mandatory\": false		}, {			\"authType\": \"demo\",			\"mandatory\": false		}, {			\"authType\": \"kyc\",			\"mandatory\": false		}, {			\"authType\": \"pin\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FINGER\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"IRIS\",			\"mandatory\": false		}, {			\"authType\": \"bio\",			\"authSubType\": \"FACE\",			\"mandatory\": false		}]	},	\"partnerId\": \"1111\",	\"partnerName\": \"Airtekibop\",	\"mispExpiresOn\": \"2021-05-19T05:58:23.32448\",	\"apiKeyExpiresOn\": \"2021-05-19T05:53:52.633968\",	\"policyExpiresOn\": \"2021-08-17T05:52:56.953157\",	\"certificateData\": null}";
		PartnerPolicyResponseDTO policyDto = null;
		try {
			policyDto = mapper.readValue(policy.getBytes(UTF_8), PartnerPolicyResponseDTO.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return policyDto;
	}
}
