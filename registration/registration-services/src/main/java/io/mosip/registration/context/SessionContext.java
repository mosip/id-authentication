package io.mosip.registration.context;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;

/**
 * Class for SessionContext details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public class SessionContext {

	/**
	 * instance of sessionContext
	 */
	private static SessionContext sessionContext;

	/**
	 * constructor of sessionContext
	 */
	private SessionContext() {

	}

	private UUID id;
	private static UserContext userContext;
	private Date loginTime;
	private long refreshedLoginTime;
	private long timeoutInterval;
	private long idealTime;
	private Map<String, Object> mapObject;
	private AuthTokenDTO authTokenDTO;

	/**
	 * making sessionContext as singleton
	 * 
	 * @return sessionContext
	 */
	public static SessionContext getInstance() {
		if (sessionContext == null) {
			sessionContext = new SessionContext();
			sessionContext.setId(UUID.randomUUID());
			sessionContext.setMapObject(new HashMap<>());
			userContext = sessionContext.new UserContext();
			sessionContext.authTokenDTO = new AuthTokenDTO();
			return sessionContext;
		} else {
			return sessionContext;
		}
	}

	/**
	 * Reading map from sessioncontext
	 * 
	 * @return map
	 */
	public static Map<String, Object> map() {
		return sessionContext.getMapObject();
	}
	
	/**
	 * Return the Type casted object based on the input
	 * 
	 * @param map
	 * @param key
	 * @param returnType
	 * @return
	 */
	public static <T> T getValue(Map<String,Object> map, String key, Class<T> returnType){
		return returnType.cast(map.get(key));
	}

	/**
	 * Reading userContext from sessioncontext
	 * 
	 * @return userContext
	 */
	public static UserContext userContext() {
		return sessionContext.getUserContext();
	}

	/**
	 * Reading userMap from sessioncontext
	 * 
	 * @return userMap
	 */
	public static Map<String, Object> userMap() {
		return sessionContext.getUserContext().getUserMap();
	}

	/**
	 * Reading refreshedLoginTime from sessioncontext
	 * 
	 * @return refreshedLoginTime
	 */
	public static long refreshedLoginTime() {
		return sessionContext.getRefreshedLoginTime();
	}

	/**
	 * Reading loginTime from sessioncontext
	 * 
	 * @return loginTime
	 */
	public static Date loginTime() {
		return sessionContext.getLoginTime();
	}

	/**
	 * Reading timeoutInterval from sessioncontext
	 * 
	 * @return timeoutInterval
	 */
	public static long timeoutInterval() {
		return sessionContext.getTimeoutInterval();
	}

	/**
	 * Reading idealTime from sessioncontext
	 * 
	 * @return idealTime
	 */
	public static long idealTime() {
		return sessionContext.getIdealTime();
	}

	/**
	 * Setting authTokenDTO to sessionContext
	 *
	 * @param authTokenDTO
	 *            DTO for auth token
	 */
	public static void setAuthTokenDTO(AuthTokenDTO authTokenDTO) {
		sessionContext.getInstance().authTokenDTO = authTokenDTO;
	}

	/**
	 * Reading authTokenDTO from sessioncontext
	 * 
	 * @return authTokenDTO
	 */
	public static AuthTokenDTO authTokenDTO() {
		return sessionContext.authTokenDTO;
	}

	/**
	 * Reading userId from sessioncontext
	 * 
	 * @return userId
	 */
	public static String userId() {
		if (sessionContext == null || sessionContext.getUserContext().getUserId() == null) {
			return RegistrationConstants.AUDIT_DEFAULT_USER;
		} else {
			return sessionContext.getUserContext().getUserId();
		}
	}

	/**
	 * Reading userName from sessioncontext
	 * 
	 * @return userName
	 */
	public static String userName() {
		if (sessionContext == null || sessionContext.getUserContext().getName() == null) {
			return RegistrationConstants.AUDIT_DEFAULT_USER;
		} else {
			return sessionContext.getUserContext().getName();
		}
	}

	/**
	 * Reading isSessionContextAvailable from sessioncontext
	 * 
	 * @return boolean
	 */
	public static boolean isSessionContextAvailable() {
		return sessionContext != null;
	}

	/**
	 * Getter for id
	 * 
	 * @return id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * Setter for id
	 *
	 * @param id
	 *            session id
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * Getter for userContext
	 * 
	 * @return userContext
	 */
	public UserContext getUserContext() {
		return userContext;
	}

	/**
	 * Getter for loginTime
	 * 
	 * @return loginTime
	 */
	public Date getLoginTime() {
		return loginTime;
	}

	/**
	 * Setter for loginTime
	 * 
	 * @param loginTime
	 *            time of login
	 */
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	/**
	 * Getter for refreshedLoginTime
	 * 
	 * @return refreshedLoginTime
	 */
	public long getRefreshedLoginTime() {
		return refreshedLoginTime;
	}

	/**
	 * Setter for refreshedLoginTime
	 * 
	 * @param refreshedLoginTime
	 *            time of login when to refresh
	 */
	public void setRefreshedLoginTime(long refreshedLoginTime) {
		this.refreshedLoginTime = refreshedLoginTime;
	}

	/**
	 * Getter for timeoutInterval
	 * 
	 * @return timeoutInterval
	 */
	public long getTimeoutInterval() {
		return timeoutInterval;
	}

	/**
	 * Setter for timeoutInterval
	 * 
	 * @param timeoutInterval
	 *            time for logout
	 */
	public void setTimeoutInterval(long timeoutInterval) {
		this.timeoutInterval = timeoutInterval;
	}

	/**
	 * Getter for idealTime
	 * 
	 * @return idealTime
	 */
	public long getIdealTime() {
		return idealTime;
	}

	/**
	 * Setter for idealTime
	 * 
	 * @param idealTime
	 *            ideal time
	 */
	public void setIdealTime(long idealTime) {
		this.idealTime = idealTime;
	}

	/**
	 * Getter for mapObject
	 * 
	 * @return mapObject
	 */
	public Map<String, Object> getMapObject() {
		return mapObject;
	}

	/**
	 * Setter for mapObject
	 * 
	 * @param mapObject
	 *            session map
	 */
	public void setMapObject(Map<String, Object> mapObject) {
		this.mapObject = mapObject;
	}

	/**
	 * destroys the session
	 */
	public static void destroySession() {
		sessionContext = null;
	}

	/**
	 * class for User context
	 *
	 */
	public class UserContext {
		private String userId;
		private String name;
		private RegistrationCenterDetailDTO registrationCenterDetailDTO;
		private List<String> roles;
		private AuthorizationDTO authorizationDTO;
		private Map<String, Object> userMap;

		/**
		 * Constructor for User context
		 */
		private UserContext() {

		}

		/**
		 * Getter for userId
		 * 
		 * @return userId
		 */
		public String getUserId() {
			if(userId==null) {
				return RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;
			}
			return userId;
		}

		/**
		 * Setter for userId
		 * 
		 * @param userId
		 *            id of the user
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}

		/**
		 * Getter for name
		 * 
		 * @return name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Setter for name
		 * 
		 * @param name
		 *            user name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Getter for registrationCenterDetailDTO
		 * 
		 * @return registrationCenterDetailDTO
		 */
		public RegistrationCenterDetailDTO getRegistrationCenterDetailDTO() {
			return registrationCenterDetailDTO;
		}

		/**
		 * Setter for registrationCenterDetailDTO
		 * 
		 * @param registrationCenterDetailDTO
		 *            registration center details
		 */
		public void setRegistrationCenterDetailDTO(RegistrationCenterDetailDTO registrationCenterDetailDTO) {
			this.registrationCenterDetailDTO = registrationCenterDetailDTO;
		}

		/**
		 * Getter for roles
		 * 
		 * @return list of roles
		 */
		public List<String> getRoles() {
			return roles;
		}

		/**
		 * Setter for roles
		 * 
		 * @param roles
		 *            user roles
		 */
		public void setRoles(List<String> roles) {
			this.roles = roles;
		}

		/**
		 * Getter for authorizationDTO
		 * 
		 * @return authorizationDTO
		 */
		public AuthorizationDTO getAuthorizationDTO() {
			return authorizationDTO;
		}

		/**
		 * Setter for authorizationDTO
		 * 
		 * @param authorizationDTO
		 *            DTO for authorization details
		 */
		public void setAuthorizationDTO(AuthorizationDTO authorizationDTO) {
			this.authorizationDTO = authorizationDTO;
		}

		/**
		 * Getter for userMap
		 * 
		 * @return userMap
		 */
		public Map<String, Object> getUserMap() {
			return userMap;
		}

		/**
		 * Setter for userMap
		 * 
		 * @param userMap
		 *            user map
		 */
		public void setUserMap(Map<String, Object> userMap) {
			this.userMap = userMap;
		}

	}

}
