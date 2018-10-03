package org.mosip.registration.dao.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mosip.registration.dao.RegistrationUserDetailDAO;
import org.mosip.registration.entity.RegistrationUserDetail;
import org.mosip.registration.repositories.RegistrationUserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * The implementation class of {@link RegistrationUserDetailDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationUserDetailDAOImpl implements RegistrationUserDetailDAO {
	
	/** The registrationUserDetail repository. */
	@Autowired
	private RegistrationUserDetailRepository registrationUserDetailRepository;
	
	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationUserDetailDAO#getUserDetail(java.lang.String)
	 */
	public Map<String,String> getUserDetail(String userId){
		
		List<RegistrationUserDetail> registrationUserDetail =registrationUserDetailRepository.findByIdAndIsActiveTrue(userId); 
		LinkedHashMap<String,String> userDetails = new LinkedHashMap<String,String>();
		
		if(!registrationUserDetail.isEmpty()) {
			userDetails.put("name",registrationUserDetail.get(0).getName());
			userDetails.put("centerId", registrationUserDetail.get(0).getCntrId());
		}
		
		return userDetails;
	}

}
