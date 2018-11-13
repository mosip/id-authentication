package io.mosip.registration.processor.quality.check.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

@Component
public class ApplicantInfoDao {
	/** The registration information. */

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

	@Autowired
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;


	public QcuserRegistrationIdEntity save(QcuserRegistrationIdEntity qcUserRegistrationIdEntity) {

		return qcuserRegRepositary.save(qcUserRegistrationIdEntity);
	}

	public QcuserRegistrationIdEntity update(QcuserRegistrationIdEntity qcUserRegistrationIdEntity) {

		return qcuserRegRepositary.save(qcUserRegistrationIdEntity);
	}

	public QcuserRegistrationIdEntity findById(String qcUserId, String regId) {
		Map<String, Object> params = new HashMap<>();
		String className = QcuserRegistrationIdEntity.class.getSimpleName();

		String alias = QcuserRegistrationIdEntity.class.getName().toLowerCase().substring(0, 1);

		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".id=:QCUserId" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON + ISACTIVE + EMPTY_STRING
				+ AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;

		QcuserRegistrationIdPKEntity pkEntity = new QcuserRegistrationIdPKEntity();
		pkEntity.setUsrId(qcUserId);
		pkEntity.setRegId(regId);

		params.put("QCUserId", pkEntity);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);

		List<QcuserRegistrationIdEntity> qCuserRegistrationIdEntityList = qcuserRegRepositary
				.createQuerySelect(queryStr, params);

		return !CollectionUtils.isEmpty(qCuserRegistrationIdEntityList) ? qCuserRegistrationIdEntityList.get(0) : null;
	}
}
