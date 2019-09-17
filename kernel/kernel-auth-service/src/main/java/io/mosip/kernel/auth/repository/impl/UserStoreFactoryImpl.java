/**
 * 
 */
package io.mosip.kernel.auth.repository.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.dto.DataBaseProps;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.repository.DataStore;
import io.mosip.kernel.auth.repository.UserStoreFactory;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class UserStoreFactoryImpl implements UserStoreFactory {

	@Autowired
	private MosipEnvironment mosipEnvironment;

	private Map<String, DataStore> dataStoreMap = null;
	
	@Value("${hikari.maximumPoolSize:25}")
	private int maximumPoolSize;
	@Value("${hikari.validationTimeout:3000}")
	private int validationTimeout;
	@Value("${hikari.connectionTimeout:60000}")
	private int connectionTimeout;
	@Value("${hikari.idleTimeout:200000}")
	private int idleTimeout;
	@Value("${hikari.minimumIdle:0}")
	private int minimumIdle;
	@Value("${iam.datastore.commonname:morocco}")
	private String commonname;

	UserStoreFactoryImpl() {

	}

	@PostConstruct
	private void init() {
		buildDataStoreMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.auth.factory.UserStoreFactory#getUserStores()
	 */
	@Override
	public Map<String, DataStore> getUserStores() {
		return dataStoreMap;
	}

	public void buildDataStoreMap() {
		dataStoreMap = new HashMap<>();
		String datasources = mosipEnvironment.getDataStores();
		List<String> dataStoreList = Arrays.asList(datasources.split("\\s*,\\s*"));

		for (String ds : dataStoreList) {
			if (dataStoreMap.get(ds) == null) {
				DataBaseProps dataBaseConfig = new DataBaseProps();
				dataBaseConfig.setUrl(mosipEnvironment.getUrl(ds));
				dataBaseConfig.setPort(mosipEnvironment.getPort(ds));
				dataBaseConfig.setUsername(mosipEnvironment.getUserName(ds));
				dataBaseConfig.setPassword(mosipEnvironment.getPassword(ds));
				dataBaseConfig.setDriverName(mosipEnvironment.getDriverName(ds));
				dataBaseConfig.setCommonName(commonname);
				dataBaseConfig.setSchemas(ds);
				if (ds.contains(AuthConstant.LDAP)) {
					DataStore idatastore = new LdapDataStore(dataBaseConfig);
					dataStoreMap.put(ds, idatastore);
				} else {
					DataStore idatastore = new DBDataStore(dataBaseConfig,maximumPoolSize,validationTimeout,connectionTimeout,idleTimeout,minimumIdle);
					dataStoreMap.put(ds, idatastore);
				}

			}

		}
	}

	@Override
	public DataStore getDataStoreBasedOnApp(String appId) {
		String datasource = null;
		if (appId != null) {
			datasource = mosipEnvironment.getDataStore(appId.toLowerCase() + AuthConstant.DATASOURCE);
		}
		if(datasource==null)
		{
			throw new AuthManagerException(AuthErrorCode.INVALID_DATASOURCE_ERROR.getErrorCode(),AuthErrorCode.INVALID_DATASOURCE_ERROR.getErrorMessage());
		}
		return dataStoreMap.get(datasource);
	}

}
