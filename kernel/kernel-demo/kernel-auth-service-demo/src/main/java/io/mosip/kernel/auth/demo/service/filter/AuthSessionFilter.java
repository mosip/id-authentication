/*package io.mosip.kernel.auth.demo.service.filter;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import io.mosip.kernel.auth.adapter.AuthAdapterConstant;
import io.mosip.kernel.auth.adapter.MosipUserDto;

public class AuthSessionFilter extends OncePerRequestFilter {
	
	@Autowired
	private RestTemplate restTemplate; 
	
	@Value("${auth.server.validate.url}")
	private String validateUrl;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Cookie[] cookie = request.getCookies();
		String token = null;
		
		for (Cookie co : cookie) {
			if (co.getName().contains("Authorization")) {
				token = co.getValue();
			}
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(AuthAdapterConstant.AUTH_HEADER_COOKIE, AuthAdapterConstant.AUTH_COOOKIE_HEADER + token);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<MosipUserDto> user=null;
		try {
		user=restTemplate.exchange(validateUrl, HttpMethod.POST, entity,
				MosipUserDto.class);
		}
		catch(HttpClientErrorException |HttpServerErrorException exception) {
			System.out.println(exception.getResponseBodyAsString());
		}
		 Cookie co=null;
			if (user != null) {
				List<HttpCookie> cookies= HttpCookie.parse(user.getHeaders().get("Set-Cookie").get(0));
				for(HttpCookie cook:cookies) {
					if(cook.getName().contains("Authorization")) {
						co = new Cookie("Authorization", cook.getValue());
						co.setMaxAge(6000000);
						co.setHttpOnly(true);
						co.setSecure(false);
						co.setPath("/");
					}
				}
				
		response.addCookie(co);
		filterChain.doFilter(request, response);
	}
	}
	

}
*/