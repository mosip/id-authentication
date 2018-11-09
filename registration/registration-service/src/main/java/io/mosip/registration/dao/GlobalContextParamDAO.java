package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.GlobalContextParam;

/**
 * DAO class for GlobalContextParam
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalContextParamDAO {
	
	List<GlobalContextParam> findInvalidLoginCount(List<String> loginParams);

}