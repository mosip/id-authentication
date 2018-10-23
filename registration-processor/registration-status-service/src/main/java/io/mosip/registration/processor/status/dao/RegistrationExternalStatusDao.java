package io.mosip.registration.processor.status.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;
@Component
public class RegistrationExternalStatusDao {

	@Autowired
	RegistrationRepositary<RegistrationStatusEntity, String> registrationStatusRepositary;

	public static final String AND = "AND";

	public static final String EMPTY_STRING = " ";

	public static final String SELECT_DISTINCT = "SELECT DISTINCT ";

	public static final String FROM = " FROM  ";

	public static final String WHERE = " WHERE ";

	public static final String ISACTIVE = "isActive";

	public static final String ISDELETED = "isDeleted";

	public static final String ISACTIVE_COLON = ".isActive=:";

	public static final String ISDELETED_COLON = ".isDeleted=:";

	public List<RegistrationStatusEntity> getByIds(List<String> ids) {

		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();

		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);
		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".id IN :ids" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON + ISACTIVE + EMPTY_STRING
				+ AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;
		params.put("ids", ids);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);

		return registrationStatusRepositary.createQuerySelect(queryStr, params);
	}

}
