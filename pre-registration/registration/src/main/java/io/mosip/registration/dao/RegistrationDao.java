package io.mosip.registration.dao;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.entity.RegistrationEntity;
import io.mosip.registration.repositary.RegistrationRepositary;



@Component
public class RegistrationDao {

	/** The registration status repositary. */
	@Autowired
	RegistrationRepositary registrationRepositary;

	/** The Constant AND. */
	public static final String AND = "AND";

	/** The Constant EMPTY_STRING. */
	public static final String EMPTY_STRING = " ";

	/** The Constant SELECT_DISTINCT. */
	public static final String SELECT_DISTINCT = "SELECT DISTINCT ";

	/** The Constant FROM. */
	public static final String FROM = " FROM  ";

	/** The Constant WHERE. */
	public static final String WHERE = " WHERE ";

	/** The Constant ISACTIVE. */
	public static final String ISACTIVE = "isActive";

	/** The Constant ISDELETED. */
	public static final String ISDELETED = "isDeleted";

	/** The Constant ISACTIVE_COLON. */
	public static final String ISACTIVE_COLON = ".isActive=:";

	/** The Constant ISDELETED_COLON. */
	public static final String ISDELETED_COLON = ".isDeleted=:";

	/**
	 * Save.
	 *
	 * @param registrationEntity
	 *            the registration status entity
	 * @return the registration status entity
	 */
	public RegistrationEntity save(RegistrationEntity registrationEntity) {

		return registrationRepositary.save(registrationEntity);
	}

	/**
	 * Update.
	 *
	 * @param registrationEntity
	 *            the registration status entity
	 * @return the registration status entity
	 */
	public RegistrationEntity update(RegistrationEntity registrationEntity) {

		return registrationRepositary.save(registrationEntity);
	}

	/**
	 * Find by id.
	 *
	 * @param enrolmentId
	 *            the enrolment id
	 * @return the registration status entity
	 */
	public RegistrationEntity findById(String groupId) {
		Map<String, Object> params = new HashMap<>();
		String className = RegistrationEntity.class.getSimpleName();
		
		System.out.println("class name "+className);

		String alias = RegistrationEntity.class.getName().toLowerCase().substring(0, 1);
		
		System.out.println("alias "+alias);

		String queryStr = SELECT_DISTINCT + " * " + FROM + "prereg.applicant_demographic" + EMPTY_STRING + WHERE 
				+ "group_Id=:groupId";

		
		System.out.println("query "+queryStr);

		params.put("groupId", groupId);
		
		List<RegistrationEntity> registrationEntityList = registrationRepositary
				.createQuerySelect(queryStr, params);

		
	//	List<RegistrationEntity> registrationEntityList = registrationRepositary.findBygroupId(groupId);
		System.out.println("list------- "+registrationEntityList.get(0));
		return !registrationEntityList.isEmpty() ? registrationEntityList.get(0) : null;
	}


}