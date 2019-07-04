package io.mosip.admin.configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * @author Urvil Joshi
 *
 */
@RestControllerAdvice
public class ResponseBodyAdviceConfig implements ResponseBodyAdvice<ResponseWrapper<?>> {

	@Autowired
	private ObjectMapper objectMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice#
	 * supports(org.springframework.core.MethodParameter, java.lang.Class)
	 */
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.hasMethodAnnotation(ResponseFilter.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice#
	 * beforeBodyWrite(java.lang.Object, org.springframework.core.MethodParameter,
	 * org.springframework.http.MediaType, java.lang.Class,
	 * org.springframework.http.server.ServerHttpRequest,
	 * org.springframework.http.server.ServerHttpResponse)
	 */
	@Override
	public ResponseWrapper<?> beforeBodyWrite(ResponseWrapper<?> body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {

		RequestWrapper<?> requestWrapper = null;
		String requestBody = null;

		try {
			HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();

			if (httpServletRequest instanceof ContentCachingRequestWrapper) {
				requestBody = new String(((ContentCachingRequestWrapper) httpServletRequest).getContentAsByteArray());
			} else if (httpServletRequest instanceof HttpServletRequestWrapper
					&& ((HttpServletRequestWrapper) httpServletRequest)
							.getRequest() instanceof ContentCachingRequestWrapper) {
				requestBody = new String(
						((ContentCachingRequestWrapper) ((HttpServletRequestWrapper) httpServletRequest).getRequest())
								.getContentAsByteArray());
			}

			objectMapper.registerModule(new JavaTimeModule());
			if (!EmptyCheckUtils.isNullEmpty(requestBody)) {
				requestWrapper = objectMapper.readValue(requestBody, RequestWrapper.class);
				body.setId(requestWrapper.getId());
				body.setVersion(requestWrapper.getVersion());
			}
			body.setErrors(null);
			return body;
		} catch (Exception e) {
			Logger mosipLogger = LoggerConfiguration.logConfig(ResponseBodyAdviceConfig.class);
			mosipLogger.error("", "", "", e.getMessage());
		}
		return body;
	}

}
