package io.mosip.registration.util;

public class DbQueries {
	public static final String GET_SYNC_PACKETIDs = "select id from reg.registration where client_status_code in('APPROVED','REJECTED','SYNCED','EXPORTED')";
	public static final String GET_SERVER_SYNC_PACKETIDs = "select id from reg.registration where server_status_code in('RESEND')";
	public static final String GET_PACKETIDs = "select id from reg.registration where client_status_code in('APPROVED','REJECTED','SYNCED','EXPORTED','RESEND')";
	public static final String GET_UPLOAD_PACKETIDs = "select id from reg.registration where client_status_code in('SYNCED', 'EXPORTED', 'resend', 'E')";
	public static final String UPDATE_CR_BY = "update reg.registration set cr_by='110017'";
	public static final String ADD_USERDETAIL = "insert into reg.user_detail(id,name,status_code,lang_code,is_active,cr_by,cr_dtimes)values('RandomUserID','test1','00','ENG',true,'mosip',CURRENT_TIMESTAMP)";
	public static final String ADD_USRPWD = "insert into reg.user_pwd(USR_ID,PWD,STATUS_CODE,LANG_CODE,IS_ACTIVE,CR_BY,CR_DTIMES) values('RandomUserID','E2E488ECAF91897D71BEAC2589433898414FEEB140837284C690DFC26707B262','00','ENG',true,'mosip',CURRENT_TIMESTAMP)";
	public static final String ADD_USER_ROLE = "insert into reg.user_role (usr_id,role_code,lang_code,is_active,cr_by,cr_dtimes,upd_by,upd_dtimes) values ('RandomUserID','REGISTRATION_SUPERVISOR','ENG',true,'mosip',CURRENT_TIMESTAMP,'',CURRENT_TIMESTAMP)";
	public static final String ADD_REGCENTER = "insert into reg.reg_center_user(REGCNTR_ID,USR_ID,LANG_CODE,IS_ACTIVE,CR_BY,CR_DTIMES) values ('10014','RandomUserID','eng',true,'mosip',CURRENT_TIMESTAMP)";
	public static final String UPDATE_DELETE_VAL = "update reg.global_param set val='0' where name='mosip.registration.reg_deletion_configured_days'";
	public static final String UPDATE_AUDITLOG = "update reg.global_param set val='value' where code='mosip.registration.audit_log_deletion_configured_days'";
	public static final String UPDATE_USER = "update reg.registration set cr_by='110017'";
	public static final String UPDATE_EOD_PROCESS = "update reg.global_param set val='y/n' where name='EOD_PROCESS_CONFIG_FLAG'";
	public static final String GETREGID = "select id from reg.registration where id= ";
	public static final String INSERTREGDATA = "insert into reg.registration (id, reg_type, ref_reg_id, status_code, client_Status_code, reg_usr_id, lang_code, regcntr_id, approver_usr_id, is_active, cr_by, cr_dtimes) values ('10011100110018820190311171', 'N', '12345', 'REGISTERED', 'PUSHED', 'mosip', 'ENG', '20916', 'mosip', 'true', 'mosip', '2019-03-11 11:45:01.406'";

}
