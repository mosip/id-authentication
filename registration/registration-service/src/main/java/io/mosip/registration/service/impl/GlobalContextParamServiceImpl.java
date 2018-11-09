package io.mosip.registration.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dao.GlobalContextParamDAO;
import io.mosip.registration.entity.GlobalContextParam;
import io.mosip.registration.service.GlobalContextParamService;

/**
 * Class for implementing GlobalContextParam service
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Service
public class GlobalContextParamServiceImpl implements GlobalContextParamService {

	/**
	 * Class to retrieve Global parameters of application
	 */
	@Autowired
	private GlobalContextParamDAO globalContextParamDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.GlobalContextParamService#findInvalidLoginCount
	 * (java.util.List)
	 */
	public List<GlobalContextParam> findInvalidLoginCount(List<String> loginParams) {
		return globalContextParamDAO.findInvalidLoginCount(loginParams);
	}
}
