/**
 * 
 */
package io.mosip.kernel.auth.factory;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bouncycastle.util.Arrays;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.UserOtp;
import io.mosip.kernel.auth.entities.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class DBDataStore implements IDataStore {
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private static final String NEW_USER_OTP = "INSERT INTO iam.user_detail(id,name,email,mobile,lang_code,cr_dtimes,is_active,status_code,cr_by)VALUES ( :userName,:name,:email,:phone,:langcode,NOW(),true,'ACT','Admin')";
	
	private static final String GET_USER="select use.id,use.name,use.email,use.mobile,use.lang_code,role.code from iam.user_detail use join iam.user_role userrole on use.id=userrole.usr_id join iam.role_list role on role.code =userrole.role_code where use.id = :userName ";
	
	private static final String GET_PASSWORD="select pwd from iam.user_pwd where usr_id = :userName ";
	
	private static final String NEW_ROLE_OTP="insert into iam.role_list(code,descr,lang_code,cr_dtimes,is_active,cr_by) values(:role,:description,'eng',NOW(),true,'Admin')";
	
	private static final String USER_ROLE_MAPPING="insert into iam.user_role(role_code,usr_id,lang_code,cr_dtimes,is_active,cr_by) values(:roleId,':userId,'eng',NOW(),true,'Admin');";
	
	
	public DBDataStore()
	{
		
	}
	
	public DBDataStore(DataBaseConfig dataBaseConfig)
	{
		setUpConnection(dataBaseConfig);
	}

	private void setUpConnection(DataBaseConfig dataBaseConfig) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dataBaseConfig.getDriverName());
		dataSource.setUrl(dataBaseConfig.getUrl());
		dataSource.setUsername(dataBaseConfig.getUsername());
		dataSource.setPassword(dataBaseConfig.getPassword());
		this.jdbcTemplate=new NamedParameterJdbcTemplate(dataSource);
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.AuthNDataService#authenticateUser(io.mosip.kernel.auth.entities.LoginUser)
	 */
	
	
	@Override
	public MosipUserDto authenticateUser(LoginUser loginUser) throws Exception {
		MosipUserDto mosipUserDto = getUser(loginUser.getUserName());
		byte[] password = getPassword(loginUser.getUserName());
		byte[] test = loginUser.getPassword().getBytes();
		if(mosipUserDto!=null && (Arrays.areEqual(password, test)))
		{
			return mosipUserDto;
		}
		else
		{
			throw new RuntimeException("Incorrect Password");
		}
	}

	private byte[] getPassword(String userName) {
		return jdbcTemplate.query(GET_PASSWORD,new MapSqlParameterSource().addValue("userName", userName),new ResultSetExtractor<byte[]>()
				{

					@Override
					public byte[] extractData(ResultSet rs) throws SQLException, DataAccessException {
						while(rs.next())
						{
							return rs.getString("pwd").getBytes();
						}
						return null;
					}
			
				});
	}

	private MosipUserDto getUser(String userName) {
		return jdbcTemplate.query(GET_USER,new MapSqlParameterSource().addValue("userName", userName), new ResultSetExtractor<MosipUserDto>(){

			@Override
			public MosipUserDto extractData(ResultSet rs) throws SQLException, DataAccessException {
				while(rs.next())
				{
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

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.AuthNDataService#authenticateWithOtp(io.mosip.kernel.auth.entities.otp.OtpUser)
	 */
	@Override
	public MosipUserDto authenticateWithOtp(OtpUser otpUser) throws Exception {
		MosipUserDto mosipUserDto = getUser(otpUser.getUserId());
		if(mosipUserDto==null)
		{
			int userId =createUser(otpUser);
			int roleId=createRole(userId,otpUser);
			createMapping(userId,roleId);
		}
		return getUser(otpUser.getUserId());
	}

	private void createMapping(int userId, int roleId) {
		jdbcTemplate.update(USER_ROLE_MAPPING, 
				new MapSqlParameterSource().addValue("userId", userId)
				.addValue("roleId", roleId));
	}

	private int createRole(int userId, OtpUser otpUser) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(NEW_ROLE_OTP, 
				new MapSqlParameterSource()
				.addValue("role", "individual")
				.addValue("description", "Individual User"),keyHolder,new String[]{"code"});
		return keyHolder.getKey().intValue();
		
	}

	private int createUser(OtpUser otpUser) {
		 KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(NEW_USER_OTP, 
				new MapSqlParameterSource().addValue("userName", otpUser.getUserId())
				.addValue("name", otpUser.getUserId())
				.addValue("langcode", otpUser.getLangCode())
				.addValue("email", AuthConstant.EMAIL.equals(otpUser.getOtpChannel())?otpUser.getUserId():"")
				.addValue("phone", AuthConstant.PHONE.equals(otpUser.getOtpChannel())?otpUser.getUserId():""),keyHolder,new String[]{"id"});
		return keyHolder.getKey().intValue();
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.AuthNDataService#authenticateUserWithOtp(io.mosip.kernel.auth.entities.UserOtp)
	 */
	@Override
	public MosipUserDto authenticateUserWithOtp(UserOtp loginUser) throws Exception {
		MosipUserDto mosipUserDto = getUser(loginUser.getUserId());
		return mosipUserDto;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.AuthNDataService#authenticateWithSecretKey(io.mosip.kernel.auth.entities.ClientSecret)
	 */
	@Override
	public MosipUserDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception {
		MosipUserDto mosipUserDto = getUser(clientSecret.getClientId());
		return mosipUserDto;
	}

}
