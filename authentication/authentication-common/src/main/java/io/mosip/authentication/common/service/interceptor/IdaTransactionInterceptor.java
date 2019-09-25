package io.mosip.authentication.common.service.interceptor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.transaction.manager.IdAuthTransactionManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * The Class IdaTransactionInterceptor.
 *
 * @author ArunBose
 */
@Component
public class IdaTransactionInterceptor extends EmptyInterceptor {
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6676971191224044259L;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthTransactionManager.class);
	
	/** The id auth transaction manager. */
	@Autowired
	private transient IdAuthTransactionManager idAuthTransactionManager;
	
	/* (non-Javadoc)
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		try {
			if (entity instanceof AutnTxn) {
				AutnTxn authTxn = (AutnTxn) entity;
				if (authTxn.getUin() != null) {
					List<String> uinList = Arrays.asList(authTxn.getUin().split(IdRepoConstants.SPLITTER.getValue()));
					byte[] encryptedUinByteWithSalt = idAuthTransactionManager
							.encryptWithSalt(uinList.get(1).getBytes(), CryptoUtil.decodeBase64(uinList.get(2)));
					String encryptedUinWithSalt = uinList.get(0) + IdAuthCommonConstants.UIN_MODULO_SPLITTER
							+ new String(encryptedUinByteWithSalt);
					authTxn.setUin(encryptedUinWithSalt);
					List<String> propertyNamesList = Arrays.asList(propertyNames);
					int indexOfData = propertyNamesList.indexOf("uin");
					state[indexOfData] = encryptedUinWithSalt;
				}
				return super.onSave(authTxn, id, state, propertyNames, types);
			}

		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(idAuthTransactionManager.getUser(), "IdaTransactionInterceptor", "onSave",
					"\n" + e.getMessage());
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.INVALID_ENCRYPTION.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_ENCRYPTION.getErrorMessage(), e);
		}
		return super.onSave(entity, id, state, propertyNames, types);
	}

}
