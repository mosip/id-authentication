package io.mosip.registration.service;

import java.util.List;

import io.mosip.registration.entity.GlobalContextParam;


/**
 * Service Class for GlobalContextParameters
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalContextParamService {

	/**
	 * Fetching list of Global parameters of application
	 * @return
	 */
	List<GlobalContextParam> findInvalidLoginCount(List<String> loginParams);
	
	/**
	 * Fetching Rejection and on hold comments
	 * @param status
	 * @return
	 */
	GlobalContextParam findRejectionOnholdComments(String status);

}