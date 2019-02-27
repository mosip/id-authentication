/**
 * 
 */
package io.mosip.kernel.auth.factory;

import java.sql.Connection;
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
import io.mosip.kernel.auth.entities.AuthZResponseDto;
import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.MosipUserDtoToken;
import io.mosip.kernel.auth.entities.UserOtp;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.entities.otp.OtpValidateRequestDto;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class DBDataStore implements IDataStore {
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private static final String NEW_USER_OTP = "INSERT INTO user_details( user_name,name,email,mobile,langcode,created_date,password) VALUES ( :userName,:name,:email,:phone,:langcode,NOW(),:password);";
	
	private static final String GET_USER="select user.user_name,user.password,user.name,user.email,user.mobile,user.langcode,role.role from user_details user,user_roles role "
			+ " where user.user_id=role.user_id and user.user_name = :userName ";
	
	private static final String GET_PASSWORD="select password from user_details where user_name = :userName ";
	
	private static final String NEW_ROLE_OTP="insert into user_roles(user_id,role,created_date,description) values(:userId,:role,NOW(),:description)";
	
	
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
							return rs.getString("password").getBytes();
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
					mosipUserDto.setName(rs.getString("user_name"));
					mosipUserDto.setRole(rs.getString("role"));
					mosipUserDto.setMail(rs.getString("email"));
					mosipUserDto.setMobile(rs.getString("mobile"));
					mosipUserDto.setUserName(rs.getString("user_name"));
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
			createRole(userId,otpUser);
		}
		return getUser(otpUser.getUserId());
	}

	private void createRole(int userId, OtpUser otpUser) {
		jdbcTemplate.update(NEW_ROLE_OTP, 
				new MapSqlParameterSource().addValue("userId", userId)
				.addValue("role", "individual")
				.addValue("description", "Individual User"));
		
	}

	private int createUser(OtpUser otpUser) {
		 KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(NEW_USER_OTP, 
				new MapSqlParameterSource().addValue("userName", otpUser.getUserId())
				.addValue("name", otpUser.getUserId())
				.addValue("langcode", otpUser.getLangCode())
				.addValue("password", "")
				.addValue("email", AuthConstant.EMAIL.equals(otpUser.getOtpChannel())?otpUser.getUserId():"")
				.addValue("phone", AuthConstant.PHONE.equals(otpUser.getOtpChannel())?otpUser.getUserId():""),keyHolder,new String[]{"user_id"});
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
