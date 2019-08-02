/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.dto.AuthToken;
import io.mosip.kernel.auth.dto.TimeToken;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.service.TokenService;

/**
 * Class used for storing token
 * 
 * @author Ramadurai Pandian
 *
 */
@Repository
public class TokenServicesImpl implements TokenService {

	public static final String INSERT_TOKEN = "insert into iam.oauth_access_token(user_id,auth_token,refresh_token,expiration_time,cr_dtimes,is_active,cr_by) values(:userName,:token,:refreshToken,:expTime,:crdTimes,true,'Admin')";

	public static final String SELECT_TOKEN = "select user_id,auth_token,refresh_token,expiration_time from iam.oauth_access_token where auth_token like :token ";

	public static final String UPDATE_TOKEN = "update iam.oauth_access_token set user_id=:userName,auth_token=:token,refresh_token=:refreshToken,expiration_time=:expTime,cr_dtimes=:crdTimes "
			+ " where user_id = :userName";

	public static final String CHECK_USER = "select user_id from iam.oauth_access_token where user_id like :userName";

	public static final String UPDATE_NEW_TOKEN = "update iam.oauth_access_token set auth_token=:token,expiration_time=:expTime where user_id like :userName ";

	public static final String SELECT_TOKEN_NAME = "select user_id,auth_token,refresh_token,expiration_time from iam.oauth_access_token where user_id like :userName ";

	public static final String DELETE_ACCESS_TOKEN = "delete from iam.oauth_access_token where user_id like :userName";

	public static final String DELETE_REFRESH_TOKEN = "delete from iam.oauth_access_token where user_id like :userName";

	private final String insertTokenSQL = INSERT_TOKEN;

	private final String selectTokenSQL = SELECT_TOKEN;

	private final String updateTokenSQL = UPDATE_TOKEN;

	private final String checkUserTokenSQL = CHECK_USER;

	private final String updateNewTokenSQL = UPDATE_NEW_TOKEN;

	private final String selectTokenFromName = SELECT_TOKEN_NAME;

	private final String deleteAccessToken = DELETE_ACCESS_TOKEN;

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public TokenServicesImpl(DataSource datasource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(datasource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.CustomTokenServices#StoreToken(io.mosip.kernel.
	 * auth.entities.AuthToken)
	 */
	@Override
	public void StoreToken(AuthToken token) {
		String userName = checkUser(token.getUserId());
		if(userName!=null)
		{
			UpdateToken(token);
			
		}
		else
		{
			jdbcTemplate.update(insertTokenSQL, new MapSqlParameterSource()
					.addValue("userName", token.getUserId())
					.addValue("token",token.getAccessToken() ).addValue("refreshToken", token.getRefreshToken())
					.addValue("expTime", new Date(token.getExpirationTime()))
					.addValue("crdTimes", new Date()));
		}
	}
	
	@Override
	public void UpdateToken(AuthToken token) {
		String userName = checkUser(token.getUserId());
		if (userName != null) {
			jdbcTemplate.update(updateTokenSQL,
					new MapSqlParameterSource().addValue("userName", token.getUserId())
							.addValue("token", token.getAccessToken()).addValue("refreshToken", token.getRefreshToken())
							.addValue("expTime", new Date(token.getExpirationTime())).addValue("crdTimes", new Date()));
		}
	}
	
	

	private String checkUser(String userId) {
		return jdbcTemplate.query(checkUserTokenSQL, new MapSqlParameterSource().addValue("userName", userId),
				new ResultSetExtractor<String>() {

					@Override
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs != null) {
							while (rs.next()) {
								return new String(rs.getString("user_id"));
							}
						}
						return null;
					}

				});
	}

	@Override
	public AuthToken getTokenDetails(String token) {

		return jdbcTemplate.query(selectTokenSQL, new MapSqlParameterSource().addValue("token", token.trim()),
				new ResultSetExtractor<AuthToken>() {

					@Override
					public AuthToken extractData(ResultSet rs) throws SQLException, DataAccessException {
						while (rs.next()) {
							AuthToken authToken = new AuthToken();
							authToken.setAccessToken(rs.getString("auth_token"));
							authToken.setUserId(rs.getString("user_id"));
							authToken.setExpirationTime(rs.getTimestamp("expiration_time").getTime());
							authToken.setRefreshToken(rs.getString("refresh_token"));
							return authToken;
						}
						return null;
					}

				});
	}
	
	

	@Override
	@Transactional
	public AuthToken getUpdatedAccessToken(String token, TimeToken newAccessToken, String userName) {
		jdbcTemplate.update(updateNewTokenSQL,
				new MapSqlParameterSource().addValue("userName", userName).addValue("token", newAccessToken.getToken())
						.addValue("expTime", new Date(newAccessToken.getExpTime())));
		return getTokenBasedOnName(userName);
	}
	
	@Override
	public AuthToken getTokenBasedOnName(String userName)
	{
		return jdbcTemplate.query(selectTokenFromName, new MapSqlParameterSource().addValue("userName", userName.trim()),
				new ResultSetExtractor<AuthToken>() {

			@Override
			public AuthToken extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					AuthToken authToken = new AuthToken();
					authToken.setAccessToken(rs.getString("auth_token"));
					authToken.setUserId(rs.getString("user_id"));
					authToken.setExpirationTime(rs.getTimestamp("expiration_time").getTime());
					authToken.setRefreshToken(rs.getString("refresh_token"));
					return authToken;
				}
				return null;
			}

		});
	}

	@Override
	public void revokeToken(String token) {
		AuthToken authToken = getTokenDetails(token);
		if(authToken!=null)
		{
		removeAccessToken(authToken.getUserId());
		}
		else
		{
			throw new AuthManagerException(AuthErrorCode.TOKEN_DATASTORE_ERROR.getErrorCode(),AuthErrorCode.TOKEN_DATASTORE_ERROR.getErrorMessage());
		}
	}

	private void removeAccessToken(String userId) {
		jdbcTemplate.update(deleteAccessToken, new MapSqlParameterSource().addValue("userName", userId));

	}

}
