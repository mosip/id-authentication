package io.kernel.core.idrepo.dao.impl;

import java.util.Date;

import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.kernel.core.idrepo.dao.IdRepoDao;
import io.kernel.core.idrepo.entity.Uin;
import io.kernel.core.idrepo.entity.UinDetail;
import io.kernel.core.idrepo.shard.ShradResolver;

@Repository
public class IdRepoDaoImpl implements IdRepoDao {
	
	@Autowired
	private Environment env;

	private DataSourceTransactionManager txnManager;

	private static final String UIN_INSERT = "INSERT INTO uin.uin(uin_ref_id, uin, status_code,"
			+ " cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes)" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UIN_DETAIL_INSERT = "INSERT INTO uin.uin_detail("
			+ "	uin_ref_id, uin_data, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes)"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UIN_SELECT = "SELECT uin_ref_id, trim(uin) as uin, status_code, cr_by as created_by, cr_dtimes as created_date_time,"
			+ " upd_by as updated_by, upd_dtimes as updated_date_time, is_deleted, del_dtimes as deleted_date_time FROM uin.uin where uin = ?";

	private static final String UIN_DETAIL_SELECT = "SELECT uin_ref_id, uin_data, cr_by as created_by, cr_dtimes as created_date_time, upd_by as updated_by, "
			+ "upd_dtimes as updated_date_time, is_deleted, del_dtimes as deleted_date_time	FROM uin.uin_detail where uin_ref_id = ?";

	@Autowired
	private ShradResolver shardResolver;

	public JdbcTemplate getJdbcTemplate(String uin) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		DataSource dataSource = shardResolver.getShrad(uin);
		txnManager = new DataSourceTransactionManager(dataSource);
		jdbcTemplate.setDataSource(dataSource);
		return jdbcTemplate;
	}

	@Transactional
	public Uin addIdentity(String uin, String uinRefId, JSONObject identityInfo) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate(uin);
		int sqlStatus = jdbcTemplate.update(UIN_INSERT, uinRefId, uin, env.getProperty("mosip.idrepo.status.registerd"), "cr_by", new Date(), "upd_by",
				new Date(), false, new Date());

		if (sqlStatus == 1) {
			sqlStatus = jdbcTemplate.update(UIN_DETAIL_INSERT, uinRefId, identityInfo.toString().getBytes(),
					"cr_by", new Date(), "upd_by", new Date(), false, new Date());
		}

		if (sqlStatus == 1) {
			return retrieveIdentity(uin);
		} else {
			return null;
		}
	}

	// @Transactional(readOnly=true)
	public Uin retrieveIdentity(String uin) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate(uin);

		Uin uinObject = jdbcTemplate.queryForObject(UIN_SELECT, new Object[] { uin },
				new BeanPropertyRowMapper<>(Uin.class));

		uinObject.setUinDetail(jdbcTemplate.queryForObject(UIN_DETAIL_SELECT, new Object[] { uinObject.getUinRefId() },
				new BeanPropertyRowMapper<>(UinDetail.class)));

		return uinObject;
	}

	// @Transactional
	public Uin updateIdenityInfo(String uin, JSONObject identityInfo) {
		return null;
	}

	// @Transactional
	public Uin updateUinStatus(String uin, String statusCode) {
		return null;
	}

}
