/**
 * 
 */
package io.mosip.kernel.auth.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bouncycastle.util.Arrays;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.dto.AuthZResponseDto;
import io.mosip.kernel.auth.dto.ClientSecret;
import io.mosip.kernel.auth.dto.DataBaseProps;
import io.mosip.kernel.auth.dto.LoginUser;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserListDto;
import io.mosip.kernel.auth.dto.MosipUserSaltListDto;
import io.mosip.kernel.auth.dto.PasswordDto;
import io.mosip.kernel.auth.dto.RIdDto;
import io.mosip.kernel.auth.dto.RolesListDto;
import io.mosip.kernel.auth.dto.UserDetailsDto;
import io.mosip.kernel.auth.dto.UserDetailsResponseDto;
import io.mosip.kernel.auth.dto.UserNameDto;
import io.mosip.kernel.auth.dto.UserOtp;
import io.mosip.kernel.auth.dto.UserPasswordRequestDto;
import io.mosip.kernel.auth.dto.UserPasswordResponseDto;
import io.mosip.kernel.auth.dto.UserRegistrationRequestDto;
import io.mosip.kernel.auth.dto.UserRegistrationResponseDto;
import io.mosip.kernel.auth.dto.ValidationResponseDto;
import io.mosip.kernel.auth.dto.otp.OtpUser;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.repository.DataStore;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class DBDataStore implements DataStore {

	private NamedParameterJdbcTemplate jdbcTemplate;

	private static final String NEW_USER_OTP = "INSERT INTO iam.user_detail(id,name,email,mobile,lang_code,cr_dtimes,is_active,status_code,cr_by)VALUES ( :userName,:name,:email,:phone,:langcode,NOW(),true,'ACT','Admin')";

	private static final String GET_USER = "select use.id,use.name,use.email,use.mobile,use.lang_code,role.code from iam.user_detail use left outer join iam.user_role userrole on use.id=userrole.usr_id left outer join iam.role_list role on role.code =userrole.role_code where use.id like :userName ";

	private static final String GET_PASSWORD = "select pwd from iam.user_pwd where usr_id like :userName ";

	private static final String GET_ROLE = "select code from iam.role_list where code like :role ";

	private static final String NEW_ROLE_OTP = "insert into iam.role_list(code,descr,lang_code,cr_dtimes,is_active,cr_by) values(:role,:description,:langCode,NOW(),true,'Admin')";

	private static final String USER_ROLE_MAPPING = "insert into iam.user_role(role_code,usr_id,lang_code,cr_dtimes,is_active,cr_by) values(:roleId,:userId,'eng',NOW(),true,'Admin');";

	public DBDataStore() {

	}

	public DBDataStore(DataBaseProps dataBaseConfig) {
		setUpConnection(dataBaseConfig);
	}

	private void setUpConnection(DataBaseProps dataBaseConfig) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dataBaseConfig.getDriverName());
		dataSource.setUrl(dataBaseConfig.getUrl());
		dataSource.setUsername(dataBaseConfig.getUsername());
		dataSource.setPassword(dataBaseConfig.getPassword());
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.auth.service.AuthNDataService#authenticateUser(io.mosip.
	 * kernel.auth.dto.LoginUser)
	 */

	@Override
	public MosipUserDto authenticateUser(LoginUser loginUser) throws Exception {
		MosipUserDto mosipUserDto = getUser(loginUser.getUserName());
		byte[] password = getPassword(loginUser.getUserName());
		byte[] test = loginUser.getPassword().getBytes();
		if (mosipUserDto != null && (Arrays.areEqual(password, test))) {
			return mosipUserDto;
		} else {
			throw new AuthManagerException(AuthErrorCode.PASSWORD_VALIDATION_ERROR.getErrorCode(),
					AuthErrorCode.PASSWORD_VALIDATION_ERROR.getErrorMessage());
		}
	}

	private String getRole(String role) {
		return jdbcTemplate.query(GET_ROLE, new MapSqlParameterSource().addValue("role", role),
				new ResultSetExtractor<String>() {

					@Override
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						while (rs.next()) {
							return rs.getString("code");
						}
						return null;
					}

				});
	}

	private byte[] getPassword(String userName) {
		return jdbcTemplate.query(GET_PASSWORD, new MapSqlParameterSource().addValue("userName", userName),
				new ResultSetExtractor<byte[]>() {

					@Override
					public byte[] extractData(ResultSet rs) throws SQLException, DataAccessException {
						while (rs.next()) {
							return rs.getString("pwd").getBytes();
						}
						return null;
					}

				});
	}

	private MosipUserDto getUser(String userName) {
		return jdbcTemplate.query(GET_USER, new MapSqlParameterSource().addValue("userName", userName),
				new ResultSetExtractor<MosipUserDto>() {

					@Override
					public MosipUserDto extractData(ResultSet rs) throws SQLException, DataAccessException {
						while (rs.next()) {
							MosipUserDto mosipUserDto = new MosipUserDto();
							mosipUserDto.setName(rs.getString("name"));
							mosipUserDto.setRole(rs.getString("code"));
							mosipUserDto.setMail(rs.getString("email"));
							mosipUserDto.setMobile(rs.getString("mobile"));
							mosipUserDto.setUserId(rs.getString("id"));
							return mosipUserDto;
						}
						return null;
					}

				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.AuthNDataService#authenticateWithOtp(io.mosip.
	 * kernel.auth.dto.otp.OtpUser)
	 */
	@Override
	public MosipUserDto authenticateWithOtp(OtpUser otpUser) throws Exception {
		MosipUserDto mosipUserDto = getUser(otpUser.getUserId());
		String roleId = null;
		if (mosipUserDto == null) {
			String userId = createUser(otpUser);
			roleId = getRole(AuthConstant.INDIVIDUAL);
			if (roleId == null) {
				roleId = createRole(userId, otpUser);
			}
			createMapping(userId, roleId);
		}
		return getUser(otpUser.getUserId());
	}

	private void createMapping(String userId, String roleId) {
		jdbcTemplate.update(USER_ROLE_MAPPING,
				new MapSqlParameterSource().addValue("userId", userId).addValue("roleId", roleId));
	}

	private String createRole(String userId, OtpUser otpUser) {
		jdbcTemplate.update(NEW_ROLE_OTP, new MapSqlParameterSource().addValue("role", AuthConstant.INDIVIDUAL)
				.addValue("description", "Individual User").addValue("langCode", "eng"));
		return AuthConstant.INDIVIDUAL;

	}

	private String createUser(OtpUser otpUser) {
		jdbcTemplate.update(NEW_USER_OTP,
				new MapSqlParameterSource().addValue("userName", otpUser.getUserId())
						.addValue("name", otpUser.getUserId()).addValue("langcode", "eng")
						.addValue("email",
								AuthConstant.EMAIL.equals(otpUser.getOtpChannel().get(0)) ? otpUser.getUserId() : "")
						.addValue("phone",
								AuthConstant.PHONE.equals(otpUser.getOtpChannel().get(0)) ? otpUser.getUserId() : ""));
		return otpUser.getUserId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.AuthNDataService#authenticateUserWithOtp(io.
	 * mosip.kernel.auth.dto.UserOtp)
	 */
	@Override
	public MosipUserDto authenticateUserWithOtp(UserOtp loginUser) throws Exception {
		MosipUserDto mosipUserDto = getUser(loginUser.getUserId());
		return mosipUserDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.AuthNDataService#authenticateWithSecretKey(io.
	 * mosip.kernel.auth.dto.ClientSecret)
	 */
	@Override
	public MosipUserDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception {
		MosipUserDto mosipUserDto = getUser(clientSecret.getClientId());
		return mosipUserDto;
	}

	@Override
	public RolesListDto getAllRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserListDto getListOfUsersDetails(List<String> userDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserSaltListDto getAllUserDetailsWithSalt() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RIdDto getRidFromUserId(String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthZResponseDto unBlockAccount(String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserRegistrationResponseDto registerUser(UserRegistrationRequestDto userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPasswordResponseDto addPassword(UserPasswordRequestDto userPasswordRequestDto) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public AuthZResponseDto changePassword(PasswordDto passwordDto) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthZResponseDto resetPassword(PasswordDto passwordDto) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserNameDto getUserNameBasedOnMobileNumber(String mobileNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
  
  @Override
	public MosipUserDto getUserRoleByUserId(String username) {
		// TODO Auto-generated method stub
		return null;
	}

@Override
public MosipUserDto getUserDetailBasedonMobileNumber(String mobileNumber) throws Exception {
	// TODO Auto-generated method stub
	return null;
}

@Override
public ValidationResponseDto validateUserName(String userId) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public UserDetailsResponseDto getUserDetailBasedOnUid(List<String> userId) {
	// TODO Auto-generated method stub
	return null;
}

}
