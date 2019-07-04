package io.mosip.authentication.common.service.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.core.util.DateUtils;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class BaseIDAFilterTest {

	@Autowired
	Environment env;

	@Mock
	ResettableStreamHttpServletRequest requestWrapper;

	BaseIDAFilter baseIDAFilter = new BaseIDAFilter() {

		@Override
		protected void authenticateRequest(ResettableStreamHttpServletRequest requestWrapper)
				throws IdAuthenticationAppException {

		}
	};

	ObjectMapper mapper = new ObjectMapper();

	@Mock
	KeyManager keyManager;

	@Mock
	ServletRequest request;

	@Mock
	ServletResponse response;

	@Mock
	FilterChain chain;

	@Mock
	CharResponseWrapper responseWrapper;

	@Mock
	ByteArrayOutputStream output;

	@Before
	public void setup() {
		ReflectionTestUtils.setField(baseIDAFilter, "env", env);
		ReflectionTestUtils.setField(baseIDAFilter, "mapper", mapper);
		ReflectionTestUtils.setField(baseIDAFilter, "keyManager", keyManager);
	}

	@Test
	public void testDoFilter() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		// ReflectionTestUtils.setField(baseIDAFilter, "requestTime",
		// DateUtils.getUTCCurrentDateTime());
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"0.8\"}";
		String responsewrapper = "{\"status\":\"Y\",\"errors\":[],\"responseTime\":\"2019-03-14T16:52:02.973+05:30\",\"transactionID\":\"1234567890\",\"version\":null,\"staticToken\":null,\"id\":null}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};
		HttpServletRequest requ = new HttpServletRequest() {

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
				return servletInputStream;
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
				return "/staticpin/dsa";
			}

			@Override
			public String getRequestedSessionId() {
				return null;
			}

			@Override
			public StringBuffer getRequestURL() {
				StringBuffer sb = new StringBuffer("http://localhost:8090/identity/auth/0.8/");
				return sb;
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

				return "/identity";
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
		};

		FilterChain chain = new FilterChain() {

			@Override
			public void doFilter(ServletRequest request, ServletResponse response)
					throws IOException, ServletException {
				String responseStr = ((CharResponseWrapper) response).getResponse().toString();
				byte[] bytes = responseStr.getBytes();
				for (byte b : bytes) {
					response.getOutputStream().write(b);
				}
			}
		};
		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlRZXcvUFpQSEgwSXdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016RXpNVEV5T1RFeldoY05ORFl3TnpJNE1URXlPVEV6V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUlPeFpXQ2VUNmtPaUNhMkRNSFFjZU1DTXR6eVE0empqbWZSaVRuN0RRdXB1TmJ1N0JPOTg1MWJCcWk0QmYxd2oyWFhBdTlVVFN5cm9ORW0vUTQvWE5QSzNPZzdiTHhQUTVhRVVraU5SYnNoY0JCMk9VeXB6ZG8zdk5wQjdnSzJoY2hBUjZHZm1ZcXZuRngyTXR6VGp3Zy9GdmhVNnI4T0hWQVU5SkluMy9xTStYS1hMRm81R3VNSnp5NUExLytiTXRwSFZiZ0NJWkhHTllCVk9MZkdZNThqWDN6eGgyVXQ1QjlEYjZ2R3hKZHhYcVRzMi8wb0V0ZHRrUU5tSmNNcC9vdUphb3g1REo2dXJlQkt0SytSQ0lzc0F3SkdIUFEweHptbDNEb0dNK2xsUHo3WVhGZy8rK3U2YkZBN1hjdSsyVmp3QjVkOFo2cHhJaU5XUlhrTGsxVUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQVM3Z3ZiTXpmV3duVExOSFhBM1VqU3ducXZ6cXp4WUV0dnpUdThDQkVRZllWUXhTOEpSTjBHZ3lEYnJBMmxjd1JMRm5IdHdKTDBIemI1aVNnREM4WGtsUHcxYkdyQUZHT2FhMGtKQlVFazR0ekFBQkkrNndyNmZQVmVoWTlnUUsxTysyQm01bW1CT1haNnBoSHN2Tnk3SGFlUkZJNFo0Mzg4V21DM25uTXZaQUNSWEhhWTcrb1FxS1Rac055VU93dEI0V0lsYnBFWGFKazEydEpCcVVmaG9RTTdJWXJ4VVk3SjR3RFVLVTVIMTlRMTc2QnNwYzI2UFF1NWppbk8zRUNSQ3RHeTA1NHhBdGpvN2czRDhLRWhTRHRPUG9EYldGWjIwaGdnbE1RTXpycWVUMjJkdFh1YlBlUno4dUVQeXNQVnhsNVA1NXM0RDFVT1h4K0RCNmRxZz09Il0sImFsZyI6IlJTMjU2In0.RTNCMEM0NDI5OEZDMUMxNDlBRkJGNEM4OTk2RkI5MjQyN0FFNDFFNDY0OUI5MzRDQTQ5NTk5MUI3ODUyQjg1NQ.BSxmFZMecWJuIuSlLw7ULzV2fewA9sOr8vtWyv3D2j0HK5sqCOuOUj7_NBRxYh6jXTlQZ8Ua6rsRC4fQvMTa51rWoxQfZgpHO4TTrVmRTDvOfmXjtNrS5LM7Gqywmfl9sgwUFzkC0mOrUoi6KLZxberornvKhjn1quXl-sTiCOliMq-N2ZUbPahLNSWk0XuUKdnNq7CiNtuBm739-aep_c1E6LJhj4QTJpIc55cCqaRtRUWbQlVzQEXB7z6Mu_dlGYqnvaIq3pqkQXPDVS4vnj4MdiJ4KbiEY8gDpgXomGNmM29foLH8JQpFeINQrdEs0SBOipvNgLJspyqtPbdKeA";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		Mockito.when(output.toString()).thenReturn(responsewrapper);
		Mockito.when(responseWrapper.toString()).thenReturn(responsewrapper);
		Mockito.when(responseWrapper.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
		ReflectionTestUtils.invokeMethod(baseIDAFilter, "doFilter", requ, responseWrapper, chain);
	}

	@Test
	public void sendErrorResponseTest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException, IdAuthenticationAppException {
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"0.8\"}";
		String responsewrapper = "{\"status\":\"Y\",\"errors\":[],\"responseTime\":\"2019-03-14T16:52:02.973+05:30\",\"transactionID\":\"1234567890\",\"version\":null,\"staticToken\":null,\"id\":null}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};

		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlRZXcvUFpQSEgwSXdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016RXpNVEV5T1RFeldoY05ORFl3TnpJNE1URXlPVEV6V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUlPeFpXQ2VUNmtPaUNhMkRNSFFjZU1DTXR6eVE0empqbWZSaVRuN0RRdXB1TmJ1N0JPOTg1MWJCcWk0QmYxd2oyWFhBdTlVVFN5cm9ORW0vUTQvWE5QSzNPZzdiTHhQUTVhRVVraU5SYnNoY0JCMk9VeXB6ZG8zdk5wQjdnSzJoY2hBUjZHZm1ZcXZuRngyTXR6VGp3Zy9GdmhVNnI4T0hWQVU5SkluMy9xTStYS1hMRm81R3VNSnp5NUExLytiTXRwSFZiZ0NJWkhHTllCVk9MZkdZNThqWDN6eGgyVXQ1QjlEYjZ2R3hKZHhYcVRzMi8wb0V0ZHRrUU5tSmNNcC9vdUphb3g1REo2dXJlQkt0SytSQ0lzc0F3SkdIUFEweHptbDNEb0dNK2xsUHo3WVhGZy8rK3U2YkZBN1hjdSsyVmp3QjVkOFo2cHhJaU5XUlhrTGsxVUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQVM3Z3ZiTXpmV3duVExOSFhBM1VqU3ducXZ6cXp4WUV0dnpUdThDQkVRZllWUXhTOEpSTjBHZ3lEYnJBMmxjd1JMRm5IdHdKTDBIemI1aVNnREM4WGtsUHcxYkdyQUZHT2FhMGtKQlVFazR0ekFBQkkrNndyNmZQVmVoWTlnUUsxTysyQm01bW1CT1haNnBoSHN2Tnk3SGFlUkZJNFo0Mzg4V21DM25uTXZaQUNSWEhhWTcrb1FxS1Rac055VU93dEI0V0lsYnBFWGFKazEydEpCcVVmaG9RTTdJWXJ4VVk3SjR3RFVLVTVIMTlRMTc2QnNwYzI2UFF1NWppbk8zRUNSQ3RHeTA1NHhBdGpvN2czRDhLRWhTRHRPUG9EYldGWjIwaGdnbE1RTXpycWVUMjJkdFh1YlBlUno4dUVQeXNQVnhsNVA1NXM0RDFVT1h4K0RCNmRxZz09Il0sImFsZyI6IlJTMjU2In0.RTNCMEM0NDI5OEZDMUMxNDlBRkJGNEM4OTk2RkI5MjQyN0FFNDFFNDY0OUI5MzRDQTQ5NTk5MUI3ODUyQjg1NQ.BSxmFZMecWJuIuSlLw7ULzV2fewA9sOr8vtWyv3D2j0HK5sqCOuOUj7_NBRxYh6jXTlQZ8Ua6rsRC4fQvMTa51rWoxQfZgpHO4TTrVmRTDvOfmXjtNrS5LM7Gqywmfl9sgwUFzkC0mOrUoi6KLZxberornvKhjn1quXl-sTiCOliMq-N2ZUbPahLNSWk0XuUKdnNq7CiNtuBm739-aep_c1E6LJhj4QTJpIc55cCqaRtRUWbQlVzQEXB7z6Mu_dlGYqnvaIq3pqkQXPDVS4vnj4MdiJ4KbiEY8gDpgXomGNmM29foLH8JQpFeINQrdEs0SBOipvNgLJspyqtPbdKeA";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		Mockito.when(output.toString()).thenReturn(responsewrapper);
		Mockito.when(responseWrapper.toString()).thenReturn(responsewrapper);
		Mockito.when(responseWrapper.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
		ServletResponse respserv = new ServletResponse() {

			@Override
			public void setLocale(Locale loc) {

			}

			@Override
			public void setContentType(String type) {

			}

			@Override
			public void setContentLengthLong(long length) {

			}

			@Override
			public void setContentLength(int len) {

			}

			@Override
			public void setCharacterEncoding(String charset) {

			}

			@Override
			public void setBufferSize(int size) {

			}

			@Override
			public void resetBuffer() {

			}

			@Override
			public void reset() {

			}

			@Override
			public boolean isCommitted() {

				return false;
			}

			@Override
			public PrintWriter getWriter() throws IOException {

				return new PrintWriter(new ByteArrayOutputStream());
			}

			@Override
			public ServletOutputStream getOutputStream() throws IOException {

				return null;
			}

			@Override
			public Locale getLocale() {

				return null;
			}

			@Override
			public String getContentType() {

				return null;
			}

			@Override
			public String getCharacterEncoding() {

				return null;
			}

			@Override
			public int getBufferSize() {

				return 0;
			}

			@Override
			public void flushBuffer() throws IOException {

			}
		};
		IdAuthenticationAppException idex = new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER);
		Mockito.when(requestWrapper.getServletPath()).thenReturn("/vid/zxd");
		Mockito.when(keyManager.signResponse(Mockito.anyString())).thenReturn("signature");
		ReflectionTestUtils.invokeMethod(baseIDAFilter, "sendErrorResponse", respserv, responseWrapper, requestWrapper, DateUtils.getUTCCurrentDateTime(), idex);
	}

	@Test
	public void testDoFilterInvalid() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"0.8\"}";
		String responsewrapper = "{\"status\":\"Y\",\"errors\":[],\"responseTime\":\"2019-03-14T16:52:02.973+05:30\",\"transactionID\":\"1234567890\",\"version\":null,\"staticToken\":null,\"id\":null}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};
		HttpServletRequest requ = new HttpServletRequest() {

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
				return servletInputStream;
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
				return "/otp/zyx";
			}

			@Override
			public String getRequestedSessionId() {
				return null;
			}

			@Override
			public StringBuffer getRequestURL() {
				StringBuffer sb = new StringBuffer("http://localhost:8090/identity/auth/0.8/");
				return sb;
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

				return "/identity";
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
		};

		FilterChain chain = new FilterChain() {

			@Override
			public void doFilter(ServletRequest request, ServletResponse response)
					throws IOException, ServletException {
				String responseStr = ((CharResponseWrapper) response).getResponse().toString();
				byte[] bytes = responseStr.getBytes();
				for (byte b : bytes) {
					response.getOutputStream().write(b);
				}
			}
		};
		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlRZXcvUFpQSEgwSXdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016RXpNVEV5T1RFeldoY05ORFl3TnpJNE1URXlPVEV6V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUlPeFpXQ2VUNmtPaUNhMkRNSFFjZU1DTXR6eVE0empqbWZSaVRuN0RRdXB1TmJ1N0JPOTg1MWJCcWk0QmYxd2oyWFhBdTlVVFN5cm9ORW0vUTQvWE5QSzNPZzdiTHhQUTVhRVVraU5SYnNoY0JCMk9VeXB6ZG8zdk5wQjdnSzJoY2hBUjZHZm1ZcXZuRngyTXR6VGp3Zy9GdmhVNnI4T0hWQVU5SkluMy9xTStYS1hMRm81R3VNSnp5NUExLytiTXRwSFZiZ0NJWkhHTllCVk9MZkdZNThqWDN6eGgyVXQ1QjlEYjZ2R3hKZHhYcVRzMi8wb0V0ZHRrUU5tSmNNcC9vdUphb3g1REo2dXJlQkt0SytSQ0lzc0F3SkdIUFEweHptbDNEb0dNK2xsUHo3WVhGZy8rK3U2YkZBN1hjdSsyVmp3QjVkOFo2cHhJaU5XUlhrTGsxVUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQVM3Z3ZiTXpmV3duVExOSFhBM1VqU3ducXZ6cXp4WUV0dnpUdThDQkVRZllWUXhTOEpSTjBHZ3lEYnJBMmxjd1JMRm5IdHdKTDBIemI1aVNnREM4WGtsUHcxYkdyQUZHT2FhMGtKQlVFazR0ekFBQkkrNndyNmZQVmVoWTlnUUsxTysyQm01bW1CT1haNnBoSHN2Tnk3SGFlUkZJNFo0Mzg4V21DM25uTXZaQUNSWEhhWTcrb1FxS1Rac055VU93dEI0V0lsYnBFWGFKazEydEpCcVVmaG9RTTdJWXJ4VVk3SjR3RFVLVTVIMTlRMTc2QnNwYzI2UFF1NWppbk8zRUNSQ3RHeTA1NHhBdGpvN2czRDhLRWhTRHRPUG9EYldGWjIwaGdnbE1RTXpycWVUMjJkdFh1YlBlUno4dUVQeXNQVnhsNVA1NXM0RDFVT1h4K0RCNmRxZz09Il0sImFsZyI6IlJTMjU2In0.RTNCMEM0NDI5OEZDMUMxNDlBRkJGNEM4OTk2RkI5MjQyN0FFNDFFNDY0OUI5MzRDQTQ5NTk5MUI3ODUyQjg1NQ.BSxmFZMecWJuIuSlLw7ULzV2fewA9sOr8vtWyv3D2j0HK5sqCOuOUj7_NBRxYh6jXTlQZ8Ua6rsRC4fQvMTa51rWoxQfZgpHO4TTrVmRTDvOfmXjtNrS5LM7Gqywmfl9sgwUFzkC0mOrUoi6KLZxberornvKhjn1quXl-sTiCOliMq-N2ZUbPahLNSWk0XuUKdnNq7CiNtuBm739-aep_c1E6LJhj4QTJpIc55cCqaRtRUWbQlVzQEXB7z6Mu_dlGYqnvaIq3pqkQXPDVS4vnj4MdiJ4KbiEY8gDpgXomGNmM29foLH8JQpFeINQrdEs0SBOipvNgLJspyqtPbdKeA";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		Mockito.when(output.toString()).thenReturn(responsewrapper);
		Mockito.when(responseWrapper.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
		ReflectionTestUtils.invokeMethod(baseIDAFilter, "doFilter", requ, responseWrapper, chain);
	}
	
	@Test
	public void sendErrorResponseTest2() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException, IdAuthenticationAppException {
		String req = "{\"id\":\"mosip.identity.auth\",\"individualId\":\"2410478395\",\"individualIdType\":\"D\",\"request\":\"TAYl52pSVnojUJaNSfZ7f4ItGcC71r_qj9ZxCZQfSO8ELfIohJSFZB_wlwVqkZgK9A1AIBtG-xni5f5WJrOXth_tRGZJTIRbM9Nxcs_tb9yfspTloMstYnzsQXdwyqKGraJHjpfDn6NIhpZpZ5QJ1g\",\"requestTime\":\"2019-03-13T10:01:57.086+05:30\",\"requestedAuth\":{\"bio\":false,\"demo\":true,\"otp\":false,\"pin\":false},\"requestSessionKey\":\"cCsi1_ImvFMkLKfAhq13DYDOx6Ibri78JJnp3ktd4ZdJRTuIdWKv31wb3Ys7WHBfRzyBVwmBe5ybb-zIgdTOCKIZrMc1xKY9TORdKFJHLWwvDHP94UZVa-TIHDJPKxWNzk0sVJeOpPAbe6tmTbm8TsLs7WPBxCxCBhuBoArwSAIZ9Sll9qoNR3-YwgBIMAsDMXDiP3kSI_89YOyZxSb3ZPCGaU8HWkgv1FUMvD67u2lv75sWJ_v55jQJYUOng94_6P8iElnLvUeR8Y9AEJk3txmj47FWos4Nd90vBXW79qvpON5pIuTjiyP_rMZZAhH1jPkAhYXJLjwpAQUrvGRQDA\",\"transactionID\":\"1234567890\",\"version\":\"0.8\"}";
		String responsewrapper = "{\"status\":\"Y\",\"errors\":[],\"responseTime\":\"2019-03-14T16:52:02.973+05:30\",\"transactionID\":\"1234567890\",\"version\":null,\"staticToken\":null,\"id\":null}";
		ByteArrayInputStream bais = new ByteArrayInputStream(req.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}

			@Override
			public boolean isReady() {
				return bais.available() != 0;
			}

			@Override
			public boolean isFinished() {
				return bais.available() == 0;
			}
		};

		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlRZXcvUFpQSEgwSXdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016RXpNVEV5T1RFeldoY05ORFl3TnpJNE1URXlPVEV6V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUlPeFpXQ2VUNmtPaUNhMkRNSFFjZU1DTXR6eVE0empqbWZSaVRuN0RRdXB1TmJ1N0JPOTg1MWJCcWk0QmYxd2oyWFhBdTlVVFN5cm9ORW0vUTQvWE5QSzNPZzdiTHhQUTVhRVVraU5SYnNoY0JCMk9VeXB6ZG8zdk5wQjdnSzJoY2hBUjZHZm1ZcXZuRngyTXR6VGp3Zy9GdmhVNnI4T0hWQVU5SkluMy9xTStYS1hMRm81R3VNSnp5NUExLytiTXRwSFZiZ0NJWkhHTllCVk9MZkdZNThqWDN6eGgyVXQ1QjlEYjZ2R3hKZHhYcVRzMi8wb0V0ZHRrUU5tSmNNcC9vdUphb3g1REo2dXJlQkt0SytSQ0lzc0F3SkdIUFEweHptbDNEb0dNK2xsUHo3WVhGZy8rK3U2YkZBN1hjdSsyVmp3QjVkOFo2cHhJaU5XUlhrTGsxVUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQVM3Z3ZiTXpmV3duVExOSFhBM1VqU3ducXZ6cXp4WUV0dnpUdThDQkVRZllWUXhTOEpSTjBHZ3lEYnJBMmxjd1JMRm5IdHdKTDBIemI1aVNnREM4WGtsUHcxYkdyQUZHT2FhMGtKQlVFazR0ekFBQkkrNndyNmZQVmVoWTlnUUsxTysyQm01bW1CT1haNnBoSHN2Tnk3SGFlUkZJNFo0Mzg4V21DM25uTXZaQUNSWEhhWTcrb1FxS1Rac055VU93dEI0V0lsYnBFWGFKazEydEpCcVVmaG9RTTdJWXJ4VVk3SjR3RFVLVTVIMTlRMTc2QnNwYzI2UFF1NWppbk8zRUNSQ3RHeTA1NHhBdGpvN2czRDhLRWhTRHRPUG9EYldGWjIwaGdnbE1RTXpycWVUMjJkdFh1YlBlUno4dUVQeXNQVnhsNVA1NXM0RDFVT1h4K0RCNmRxZz09Il0sImFsZyI6IlJTMjU2In0.RTNCMEM0NDI5OEZDMUMxNDlBRkJGNEM4OTk2RkI5MjQyN0FFNDFFNDY0OUI5MzRDQTQ5NTk5MUI3ODUyQjg1NQ.BSxmFZMecWJuIuSlLw7ULzV2fewA9sOr8vtWyv3D2j0HK5sqCOuOUj7_NBRxYh6jXTlQZ8Ua6rsRC4fQvMTa51rWoxQfZgpHO4TTrVmRTDvOfmXjtNrS5LM7Gqywmfl9sgwUFzkC0mOrUoi6KLZxberornvKhjn1quXl-sTiCOliMq-N2ZUbPahLNSWk0XuUKdnNq7CiNtuBm739-aep_c1E6LJhj4QTJpIc55cCqaRtRUWbQlVzQEXB7z6Mu_dlGYqnvaIq3pqkQXPDVS4vnj4MdiJ4KbiEY8gDpgXomGNmM29foLH8JQpFeINQrdEs0SBOipvNgLJspyqtPbdKeA";
		Mockito.when(requestWrapper.getInputStream()).thenReturn(servletInputStream);
		StringBuffer sbf = new StringBuffer("http://localhost:8090/identity/auth/0.8/12213123/12321312");
		Mockito.when(requestWrapper.getRequestURL()).thenReturn(sbf);
		Mockito.when(requestWrapper.getContextPath()).thenReturn("/identity");
		Mockito.when(requestWrapper.getHeader("Authorization")).thenReturn(signature);
		Mockito.when(output.toString()).thenReturn(responsewrapper);
		Mockito.when(responseWrapper.toString()).thenReturn(responsewrapper);
		Mockito.when(responseWrapper.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
		ServletResponse respserv = new ServletResponse() {

			@Override
			public void setLocale(Locale loc) {

			}

			@Override
			public void setContentType(String type) {

			}

			@Override
			public void setContentLengthLong(long length) {

			}

			@Override
			public void setContentLength(int len) {

			}

			@Override
			public void setCharacterEncoding(String charset) {

			}

			@Override
			public void setBufferSize(int size) {

			}

			@Override
			public void resetBuffer() {

			}

			@Override
			public void reset() {

			}

			@Override
			public boolean isCommitted() {

				return false;
			}

			@Override
			public PrintWriter getWriter() throws IOException {

				return new PrintWriter(new ByteArrayOutputStream());
			}

			@Override
			public ServletOutputStream getOutputStream() throws IOException {

				return null;
			}

			@Override
			public Locale getLocale() {

				return null;
			}

			@Override
			public String getContentType() {

				return null;
			}

			@Override
			public String getCharacterEncoding() {

				return null;
			}

			@Override
			public int getBufferSize() {

				return 0;
			}

			@Override
			public void flushBuffer() throws IOException {

			}
		};
		IdAuthenticationAppException idex = new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER);
		Mockito.when(requestWrapper.getServletPath()).thenReturn("/vid/zxd");
		Mockito.when(keyManager.signResponse(Mockito.anyString())).thenThrow(new IdAuthenticationAppException());
		ReflectionTestUtils.invokeMethod(baseIDAFilter, "sendErrorResponse", respserv, responseWrapper, requestWrapper, DateUtils.getUTCCurrentDateTime(), idex);
	}

	@Test
	public void dateTest() {
		ReflectionTestUtils.invokeMethod(baseIDAFilter, "isDate", "");
	}
}