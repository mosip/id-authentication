package io.mosip.authentication.common.service.factory;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_MOSIP_AUTH_FILTER_CLASSES_IN_EXECUTION_ORDER;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.ReflectionUtils;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.exception.InvalidAuthFilterJarSignatureException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.common.service.config.DemoAuthConfig;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

@Configuration
public class MosipAuthFilterFactory {
	
	private static Logger logger = IdaLogger.getLogger(MosipAuthFilterFactory.class);
	
	@Value("${" + IDA_MOSIP_AUTH_FILTER_CLASSES_IN_EXECUTION_ORDER + "}")
	private String [] mosipAuthFilterClasses;

	private List<IMosipAuthFilter> authFilters;
	
	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	
	@PostConstruct
	public void init() {
		if(mosipAuthFilterClasses.length == 0) {
			logger.error("Missing configuration: {}", IDA_MOSIP_AUTH_FILTER_CLASSES_IN_EXECUTION_ORDER);
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					String.format("%s : Missing configuration %s",
							IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(),
							IDA_MOSIP_AUTH_FILTER_CLASSES_IN_EXECUTION_ORDER));
		}
		authFilters = Stream.of(mosipAuthFilterClasses)
				.map(this::getAuthFilterInstance)
				.collect(Collectors.toUnmodifiableList());
		
	}
	
	private IMosipAuthFilter getAuthFilterInstance(String authFilterClassName) throws IdAuthUncheckedException {
		 try {
			return doGetAuthFilterInstance(authFilterClassName);
		} catch (Exception e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, DemoAuthConfig.class.getSimpleName(),
					"Didn't find Mosip Auth Filter instance", authFilterClassName);
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), "Error loading AuthFilter class :" + authFilterClassName, e);
		}
	}

	private IMosipAuthFilter doGetAuthFilterInstance(String authFilterClassName) throws ClassNotFoundException, InstantiationException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException, IdAuthenticationFilterException, InvalidAuthFilterJarSignatureException {
		Class<?> clazz = Class.forName(authFilterClassName);
		validateTrust(clazz);
		Object[] args = new Object[0];
		Optional<Constructor<?>> result = ReflectionUtils.findConstructor(clazz, args);
		if (result.isPresent()) {
			Constructor<?> constructor = result.get();
			constructor.setAccessible(true);
			IMosipAuthFilter newInstance = (IMosipAuthFilter) constructor.newInstance(args);
			beanFactory.autowireBean(newInstance);
			newInstance.init();
			return newInstance;
		} else {
			logger.error(IdAuthCommonConstants.SESSION_ID, DemoAuthConfig.class.getSimpleName(),
					"Didn't find Mosip Auth Filter instance", authFilterClassName);
			return null;
		}
	}
	
	private void validateTrust(Class<?> object) throws InvalidAuthFilterJarSignatureException{
		// TODO Validate the signature of the jar of the class
	}

	public List<IMosipAuthFilter> getEnabledAuthFilters() {
		return authFilters;
	}
	
	
}