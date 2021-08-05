package io.mosip.authentication.internal.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_MOSIP_INTERNAL_AUTH_FILTER_CLASSES_IN_EXECUTION_ORDER;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.common.service.factory.MosipAuthFilterFactory;

@Configuration
public class InternalAuthFilterFactory extends MosipAuthFilterFactory {
	
	/** The mosip auth filter classes. */
	@Value("${" + IDA_MOSIP_INTERNAL_AUTH_FILTER_CLASSES_IN_EXECUTION_ORDER + "}")
	private String [] mosipAuthFilterClasses;

	@Override
	protected String[] getMosipAuthFilterClasses() {
		return mosipAuthFilterClasses;
	}

}
