package io.mosip.authentication.common.service.factory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
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

/**
 * A factory for creating MosipAuthFilter objects.
 * 
 * @author Loganathan Sekar
 * 
 */
public abstract class MosipAuthFilterFactory {
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(MosipAuthFilterFactory.class);
	
	/** The auth filters. */
	private List<IMosipAuthFilter> authFilters;
	
	/** The bean factory. */
	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	
	/**
	 * Initializes the auth filters
	 */
	@PostConstruct
	public void init() {
		if(getMosipAuthFilterClasses().length == 0) {
			logger.info("No Auth Filter Classes configured.");
			authFilters = List.of();
		} else {
			authFilters = Stream.of(getMosipAuthFilterClasses())
					.map(this::getAuthFilterInstance)
					.collect(Collectors.toUnmodifiableList());
			logger.info("Auth Filter Classes configured count: {}", authFilters.size());
		}
		
	}
	
	protected abstract String[] getMosipAuthFilterClasses();

	/**
	 * Gets the auth filter instance.
	 *
	 * @param authFilterClassName the auth filter class name
	 * @return the auth filter instance
	 * @throws IdAuthUncheckedException the id auth unchecked exception
	 */
	private IMosipAuthFilter getAuthFilterInstance(String authFilterClassName) throws IdAuthUncheckedException {
		 try {
			return doGetAuthFilterInstance(authFilterClassName);
		} catch (Exception e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, DemoAuthConfig.class.getSimpleName(),
					"Didn't find Mosip Auth Filter instance", authFilterClassName);
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), "Error loading AuthFilter class :" + authFilterClassName, e);
		}
	}

	/**
	 * Do get auth filter instance.
	 *
	 * @param authFilterClassName the auth filter class name
	 * @return the i mosip auth filter
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws IdAuthenticationFilterException the id authentication filter exception
	 * @throws InvalidAuthFilterJarSignatureException the invalid auth filter jar signature exception
	 */
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
	
	/**
	 * Validate trust.
	 *
	 * @param object the object
	 * @throws InvalidAuthFilterJarSignatureException the invalid auth filter jar signature exception
	 */
	private void validateTrust(Class<?> object) throws InvalidAuthFilterJarSignatureException{
		// TODO Validate the signature of the jar of the class
	}

	/**
	 * Gets the enabled auth filters.
	 *
	 * @return the enabled auth filters
	 */
	public List<IMosipAuthFilter> getEnabledAuthFilters() {
		return authFilters;
	}
	
	
}