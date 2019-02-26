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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.auth.entities.AuthToken;
import io.mosip.kernel.auth.entities.TimeToken;
import io.mosip.kernel.auth.service.CustomTokenServices;

/**
 * @author Ramadurai Pandian
 *
 */
@Repository
public class CustomTokenServicesImpl implements CustomTokenServices {
	
	public static final String INSERT_TOKEN="insert into oauth_access_token(user_name,token,refresh_token,expiration_time) values(:userName,:token,:refreshToken,:expTime)";
	
	public static final String SELECT_TOKEN="select user_name,token,refresh_token,expiration_time from oauth_access_token where token like :token ";
	
	public static final String UPDATE_TOKEN="update oauth_access_token set user_name=:userName,token=:token,refresh_token=:refreshToken,expiration_time=:expTime "
			+ " where user_name = :userName";
	
	public static final String CHECK_USER="select user_name from oauth_access_token where user_name like :userName";
	
	public static final String UPDATE_NEW_TOKEN="update oauth_access_token set token=:token,expiration_time=:expTime where user_name like :userName ";
	
	public static final String SELECT_TOKEN_NAME="select user_name,token,refresh_token,expiration_time from oauth_access_token where user_name like :userName ";
	
	public static final String DELETE_ACCESS_TOKEN="delete token from oauth_access_token where user_name like :userName";
	
	public static final String DELETE_REFRESH_TOKEN="delete refresh_token from oauth_access_token where user_name like :userName";
	
	private final String insertTokenSQL=INSERT_TOKEN;
	
	private final String selectTokenSQL=SELECT_TOKEN;
	
	private final String updateTokenSQL=UPDATE_TOKEN;
	
	private final String checkUserTokenSQL=CHECK_USER;
	
	private final String updateNewTokenSQL=UPDATE_NEW_TOKEN;
	
	private final String selectTokenFromName=SELECT_TOKEN_NAME;
	
	private final String deleteAccessToken=DELETE_ACCESS_TOKEN;
	
	private final String deleteRefreshToken=DELETE_REFRESH_TOKEN;
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public CustomTokenServicesImpl(DataSource datasource)
	{
		this.jdbcTemplate=new NamedParameterJdbcTemplate(datasource);
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.CustomTokenServices#StoreToken(io.mosip.kernel.auth.entities.AuthToken)
	 */
	@Override
	public void StoreToken(AuthToken token) {
		String userName = checkUser(token.getUserId());
		if(userName!=null)
		{
			jdbcTemplate.update(updateTokenSQL, new MapSqlParameterSource()
					.addValue("userName", token.getUserId())
					.addValue("token",token.getAccessToken() ).addValue("refreshToken", token.getRefreshToken())
					.addValue("expTime", new Date(token.getExpirationTime())));
			
		}
		else
		{
			jdbcTemplate.update(insertTokenSQL, new MapSqlParameterSource()
					.addValue("userName", token.getUserId())
					.addValue("token",token.getAccessToken() ).addValue("refreshToken", token.getRefreshToken())
					.addValue("expTime", new Date(token.getExpirationTime())));
		}
	}

	private String checkUser(String userId) {
		// TODO Auto-generated method stub
		return jdbcTemplate.query(checkUserTokenSQL, new MapSqlParameterSource().addValue("userName", userId), new ResultSetExtractor<String>()
				{

					@Override
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						if(rs!=null)
						{
						while(rs.next())
						{
							return new String(rs.getString("user_name"));
						}
						}
						return null;
					}
			
				});
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.CustomTokenServices#refreshtoken(java.lang.String)
	 */
	@Override
	public void refreshtoken(String token) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.CustomTokenServices#invalidateToken(java.lang.String)
	 */
	@Override
	public void invalidateToken(String token) {
		// TODO Auto-generated method stub

	}

	@Override
	public AuthToken getTokenDetails(String token) {

		return jdbcTemplate.query(selectTokenSQL,new MapSqlParameterSource().addValue("token", token), new ResultSetExtractor<AuthToken>(){

			@Override
			public AuthToken extractData(ResultSet rs) throws SQLException, DataAccessException {
				while(rs.next())
				{
					AuthToken authToken = new AuthToken();
					authToken.setAccessToken(rs.getString("token"));
					authToken.setUserId(rs.getString("user_name"));
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
		jdbcTemplate.update(updateNewTokenSQL, new MapSqlParameterSource()
				.addValue("userName", userName)
				.addValue("token",newAccessToken.getToken()).addValue("expTime", new Date(newAccessToken.getExpTime())));
		return jdbcTemplate.query(selectTokenFromName,new MapSqlParameterSource().addValue("userName", userName), new ResultSetExtractor<AuthToken>(){

			@Override
			public AuthToken extractData(ResultSet rs) throws SQLException, DataAccessException {
				while(rs.next())
				{
					AuthToken authToken = new AuthToken();
					authToken.setAccessToken(rs.getString("token"));
					authToken.setUserId(rs.getString("user_name"));
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
		if(authToken.getRefreshToken()!=null)
		{
			removeRefreshToken(authToken.getUserId());
		}
		removeAccessToken(authToken.getUserId());
	}

	private void removeAccessToken(String userId) {
		jdbcTemplate.update(deleteAccessToken, new MapSqlParameterSource().addValue("userName", userId));
		
	}

	private void removeRefreshToken(String userId) {
		jdbcTemplate.update(deleteRefreshToken, new MapSqlParameterSource().addValue("userName", userId));		
	}

}
