package io.mosip.kernel.auth.adapter.filter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import io.mosip.kernel.auth.adapter.constant.AuthAdapterConstant;
import io.mosip.kernel.auth.adapter.model.AuthUserDetails;

/***********************************************************************************************************************
 * Adds latest token to the response headers before it is committed
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

@RestControllerAdvice
public class AuthControllerAdvice implements ResponseBodyAdvice<Object> {

	private AuthUserDetails getAuthUserDetails() {
		AuthUserDetails authUserDetails = null;
		Object details = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (details instanceof String) {

		} else {
			authUserDetails = (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		return authUserDetails;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		HttpHeaders headers = request.getHeaders();
		if(headers!=null)
		{
		System.out.println(" Auth Advice "+headers.get(AuthAdapterConstant.AUTH_HEADER_COOKIE));
		}
		if (getAuthUserDetails() != null) {
			System.out.println("token from security context : "+getAuthUserDetails().getToken());
			response.getHeaders().set(AuthAdapterConstant.AUTH_HEADER_SET_COOKIE,
					AuthAdapterConstant.AUTH_COOOKIE_HEADER + getAuthUserDetails().getToken());
		}
		return body;
	}
}