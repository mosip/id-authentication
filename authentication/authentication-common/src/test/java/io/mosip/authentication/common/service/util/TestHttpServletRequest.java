package  io.mosip.authentication.common.service.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

import io.mosip.authentication.core.dto.ObjectWithMetadata;

public class TestHttpServletRequest implements HttpServletRequest, ObjectWithMetadata {

	private ByteArrayInputStream bais;

	private Map<String, Object> metadata;

	public TestHttpServletRequest() {
		this(null);
	}

	public TestHttpServletRequest(String req) {
		if (req == null) {
			req = "";
		}
		bais = new ByteArrayInputStream(req.getBytes());
	}

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
	
	@Override
	public Map<String, Object> getMetadata() {
		return metadata;
	}
	
	@Override
	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	@Override
	public String getRequestId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProtocolRequestId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletConnection getServletConnection() {
		// TODO Auto-generated method stub
		return null;
	}

}
