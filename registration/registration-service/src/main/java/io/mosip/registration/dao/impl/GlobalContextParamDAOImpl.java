package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.registration.dao.GlobalContextParamDAO;
import io.mosip.registration.entity.GlobalContextParam;
import io.mosip.registration.repositories.GlobalContextParamRepository;

/**
 * The implementation class of {@link GlobalContextParamDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class GlobalContextParamDAOImpl implements GlobalContextParamDAO {

	@Autowired
	private GlobalContextParamRepository globalContextParamRepository;
	
	public List<GlobalContextParam> findInvalidLoginCount(List<String> loginParams){
		return globalContextParamRepository.findByNameIn(loginParams);
	}
}
