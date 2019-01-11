package io.mosip.kernel.idrepo.interceptor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idrepo.config.IdRepoLogger;
import io.mosip.kernel.idrepo.entity.Uin;
import io.mosip.kernel.idrepo.entity.UinHistory;
import io.mosip.kernel.idrepo.service.impl.IdRepoServiceImpl;

/**
 * The Class IdRepoEntityInterceptor.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoEntityInterceptor extends EmptyInterceptor {

	/** The mosip logger. */
	private transient Logger mosipLogger = IdRepoLogger.getLogger(IdRepoServiceImpl.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4985336846122302850L;

	/** The Constant DECRYPT_ENTITY. */
	private static final String DECRYPT_ENTITY = "decryptEntity";

	/** The Constant ENCRYPT_IDENTITY. */
	private static final String ENCRYPT_IDENTITY = "encryptIdentity";

	/** The Constant ID_REPO_SERVICE_IMPL. */
	private static final String ID_REPO_SERVICE_IMPL = "IdRepoServiceImpl";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The env. */
	@Autowired
	private transient Environment env;

	/** The mapper. */
	@Autowired
	private transient ObjectMapper mapper;

	/** The rest template. */
	@Autowired
	private transient RestTemplate restTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		try {

			if (entity instanceof Uin) {
				Uin uinEntity = (Uin) entity;
				byte[] encryptedData = encryptDecryptIdentity(
						CryptoUtil.encodeBase64(uinEntity.getUinData()).getBytes(), "encrypt");
				uinEntity.setUinData(encryptedData);
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int indexOfData = propertyNamesList.indexOf("uinData");
				state[indexOfData] = encryptedData;
				return super.onSave(uinEntity, id, state, propertyNames, types);
			}
			if (entity instanceof UinHistory) {
				UinHistory uinHEntity = (UinHistory) entity;
				uinHEntity.setUinData(
						encryptDecryptIdentity(CryptoUtil.encodeBase64(uinHEntity.getUinData()).getBytes(), "encrypt"));
				return super.onSave(uinHEntity, id, state, propertyNames, types);
			}
		} catch (IdRepoAppException e) {
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR, e);
		}
		return super.onSave(entity, id, state, propertyNames, types);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onLoad(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		try {
			if (entity instanceof Uin || entity instanceof UinHistory) {
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int indexOfData = propertyNamesList.indexOf("uinData");
				state[indexOfData] = CryptoUtil
						.decodeBase64(new String(encryptDecryptIdentity((byte[]) state[indexOfData], "decrypt")));
			}
		} catch (IdRepoAppException e) {
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR, e);
		}
		return super.onLoad(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		return false;
	}

	/**
	 * Encrypt identity.
	 *
	 * @param identity
	 *            the identity
	 * @param method
	 *            the method
	 * @return the byte[]
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private byte[] encryptDecryptIdentity(byte[] identity, String method) throws IdRepoAppException {
		ObjectNode request = new ObjectNode(mapper.getNodeFactory());
		request.put("applicationId", env.getProperty("application.id"));
		request.put("referenceId", env.getProperty("mosip.kernel.keymanager.refId"));
		request.put("timeStamp", DateUtils.formatDate(new Date(), env.getProperty("datetime.pattern")));
		request.put("data", new String(identity));

		ObjectNode response = restTemplate.postForObject(
				env.getProperty("mosip.kernel.cryptomanager." + method + ".url"), request, ObjectNode.class);

		if (response.has("data")) {
			return response.get("data").asText().getBytes();
		} else {
			throw new IdRepoAppException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR);
		}
	}

}
