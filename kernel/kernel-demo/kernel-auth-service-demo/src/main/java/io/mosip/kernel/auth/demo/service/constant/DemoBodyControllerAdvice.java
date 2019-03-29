/*
package io.mosip.kernel.auth.demo.service.constant;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import io.mosip.kernel.auth.adapter.AuthAdapterConstant;
import io.mosip.kernel.auth.adapter.MosipUserDto;



@RestControllerAdvice
public class DemoBodyControllerAdvice implements ResponseBodyAdvice<Object> {

  
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${auth.server.validate.url}")
	private String validateUrl;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
    	HttpServletRequest req=((ServletServerHttpRequest)request).getServletRequest();
    	Cookie[] cookie = req.getCookies();
		String token = null;
		
		for (Cookie co : cookie) {
			if (co.getName().contains("Authorization")) {
				token = co.getValue();
			}
		}
		
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.set(AuthAdapterConstant.AUTH_HEADER_COOKIE, AuthAdapterConstant.AUTH_COOOKIE_HEADER + token);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<MosipUserDto> user=restTemplate.exchange(validateUrl, HttpMethod.POST, entity,
				MosipUserDto.class);
		
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
				
		response.getHeaders().add("Set-Cookie", co.getValue());
		
    }
			try {
				return response.getBody();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return null;
    }}*/