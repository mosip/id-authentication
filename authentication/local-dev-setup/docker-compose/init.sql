CREATE DATABASE mosip_ida
	ENCODING = 'UTF8'
	LC_COLLATE = 'en_US.UTF-8'
	LC_CTYPE = 'en_US.UTF-8'
	TABLESPACE = pg_default
	OWNER = postgres
	TEMPLATE  = template0;
COMMENT ON DATABASE mosip_ida IS 'ID Authorization related requests, transactions and mapping related data like virtual ids, tokens, etc. will be stored in this database';

\c mosip_ida 

CREATE SCHEMA ida;
ALTER SCHEMA ida OWNER TO postgres;
ALTER DATABASE mosip_ida SET search_path TO ida,pg_catalog,public;

CREATE TABLE ida.anonymous_profile(
	id character varying(36) NOT NULL,
	profile character varying NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_profile PRIMARY KEY (id)
);

ALTER TABLE ida.anonymous_profile SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 500,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 500
);

CREATE TABLE ida.api_key_data (
	api_key_id character varying(36) NOT NULL,
	api_key_commence_on timestamp NOT NULL,
	api_key_expires_on timestamp,
	api_key_status character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT api_key_data_pk PRIMARY KEY (api_key_id)
);

CREATE INDEX ind_akd_apkeyid ON ida.api_key_data (api_key_id);

ALTER TABLE ida.api_key_data SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 50
);

CREATE TABLE ida.auth_transaction(
	id character varying(36) NOT NULL,
	request_dtimes timestamp NOT NULL,
	response_dtimes timestamp NOT NULL,
	request_trn_id character varying(64),
	auth_type_code character varying(128) NOT NULL,
	status_code character varying(36) NOT NULL,
	status_comment character varying(1024),
	lang_code character varying(3) NOT NULL,
	ref_id_type character varying(36),
	ref_id character varying(64),
	token_id character varying(128) NOT NULL,
	requested_entity_type character varying(64),
	requested_entity_id character varying(36),
	requested_entity_name character varying(128),
	static_tkn_id character varying(64),
	request_signature character varying,
	response_signature character varying,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean NOT NULL DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_authtrn_id PRIMARY KEY (id)
);

CREATE INDEX ind_reqtrnid_dtimes_tknid ON ida.auth_transaction (request_trn_id, request_dtimes, token_id,cr_dtimes, auth_type_code);

CREATE INDEX idx_autntxn_refid_dtimes
ON ida.auth_transaction (ref_id, request_dtimes);

CREATE INDEX CONCURRENTLY idx_auth_txn_entityid_request_dtimes
ON ida.auth_transaction (requested_entity_id, request_dtimes DESC);

CREATE INDEX idx_autn_txn_refid_time_desc
ON ida.auth_transaction (ref_id, request_dtimes DESC);

CREATE INDEX idx_autntxn_reqtrnid_authtype_crdtimes_desc
ON ida.auth_transaction (request_trn_id, auth_type_code, cr_dtimes DESC);

CREATE INDEX idx_autntxn_token_crdtimes_desc
ON ida.auth_transaction (token_id, cr_dtimes DESC);

CREATE INDEX idx_autntxn_token_reqdtimes
ON ida.auth_transaction (token_id, request_dtimes);

CREATE INDEX IF NOT EXISTS idx_auth_txn_entityid_request_dtimes_cover ON ida.auth_transaction USING btree (requested_entity_id, request_dtimes) INCLUDE (id);

ALTER TABLE ida.auth_transaction SET (
    autovacuum_vacuum_scale_factor = 0.002,
    autovacuum_vacuum_threshold = 5000,
    autovacuum_analyze_scale_factor = 0.002,
    autovacuum_analyze_threshold = 5000
);

CREATE TABLE ida.batch_job_instance  (
    JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
	VERSION BIGINT ,
	JOB_NAME VARCHAR(100) NOT NULL,
	JOB_KEY VARCHAR(32) NOT NULL,
	constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
)
WITH (
    OIDS = FALSE
);

CREATE INDEX IF NOT EXISTS idx_job_name ON ida.batch_job_instance(JOB_NAME);
CREATE INDEX IF NOT EXISTS idx_job_key ON ida.batch_job_instance(JOB_KEY);

ALTER TABLE ida.batch_job_instance SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

CREATE TABLE ida.batch_job_execution  (
    JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
  	VERSION BIGINT  ,
  	JOB_INSTANCE_ID BIGINT NOT NULL,
  	CREATE_TIME TIMESTAMP NOT NULL,
  	START_TIME TIMESTAMP DEFAULT NULL ,
  	END_TIME TIMESTAMP DEFAULT NULL ,
  	STATUS VARCHAR(10) ,
  	EXIT_CODE VARCHAR(2500) ,
  	EXIT_MESSAGE VARCHAR(2500) ,
  	LAST_UPDATED TIMESTAMP,
  	constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
  	references ida.BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE ida.batch_job_execution SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

CREATE INDEX IF NOT EXISTS idx_job_exec_instance ON ida.batch_job_execution USING btree (job_instance_id);

CREATE TABLE ida.batch_job_execution_context
(
    JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT ,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
    references ida.BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE ida.batch_job_execution_context SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

CREATE TABLE ida.batch_job_execution_params  (
    JOB_EXECUTION_ID BIGINT NOT NULL ,
    PARAMETER_NAME VARCHAR(100) NOT NULL ,
    PARAMETER_TYPE VARCHAR(100) NOT NULL ,
    PARAMETER_VALUE VARCHAR(2500) ,
    IDENTIFYING CHAR(1) NOT NULL ,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
    references ida.BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE ida.batch_job_execution_params SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

CREATE TABLE ida.batch_step_execution  (
    STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
	VERSION BIGINT NOT NULL,
	STEP_NAME VARCHAR(100) NOT NULL,
	JOB_EXECUTION_ID BIGINT NOT NULL,
	CREATE_TIME TIMESTAMP NOT NULL,
	START_TIME TIMESTAMP DEFAULT NULL ,
	END_TIME TIMESTAMP DEFAULT NULL ,
	STATUS VARCHAR(10) ,
	COMMIT_COUNT BIGINT ,
	READ_COUNT BIGINT ,
	FILTER_COUNT BIGINT ,
	WRITE_COUNT BIGINT ,
	READ_SKIP_COUNT BIGINT ,
	WRITE_SKIP_COUNT BIGINT ,
	PROCESS_SKIP_COUNT BIGINT ,
	ROLLBACK_COUNT BIGINT ,
	EXIT_CODE VARCHAR(2500) ,
	EXIT_MESSAGE VARCHAR(2500) ,
	LAST_UPDATED TIMESTAMP,
	constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
	references ida.BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE ida.batch_step_execution SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 2000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 2000
);

CREATE INDEX IF NOT EXISTS idx_step_exec_jobid_stepname ON ida.batch_step_execution USING btree (job_execution_id, step_name);

CREATE TABLE ida.batch_step_execution_context
(
    STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT ,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
    references ida.BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE ida.batch_step_execution_context SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 5000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 5000
);

CREATE TABLE ida.ca_cert_store(
	cert_id character varying(36) NOT NULL,
	cert_subject character varying(500) NOT NULL,
	cert_issuer character varying(500) NOT NULL,
	issuer_id character varying(36) NOT NULL,
	cert_not_before timestamp,
	cert_not_after timestamp,
	crl_uri character varying(120),
	cert_data character varying,
	cert_thumbprint character varying(100),
	cert_serial_no character varying(50),
	partner_domain character varying(36),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	ca_cert_type character varying(25),
	CONSTRAINT pk_cacs_id PRIMARY KEY (cert_id),
	CONSTRAINT cert_thumbprint_unique UNIQUE (cert_thumbprint,partner_domain)
);

ALTER TABLE ida.ca_cert_store SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE TABLE ida.cred_subject_id_store(
    id character varying(36) NOT NULL,
    id_vid_hash character varying(128) NOT NULL,
    token_id character varying(128) NOT NULL,
    cred_subject_id character varying(2000) NOT NULL,
    csid_key_hash character varying(128) NOT NULL,
    oidc_client_id character varying(128),
    csid_status character varying(36),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    CONSTRAINT key_hash_unique UNIQUE (id_vid_hash, csid_key_hash)
);

ALTER TABLE ida.cred_subject_id_store SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE INDEX ind_csid_key_hash ON ida.cred_subject_id_store (csid_key_hash);

CREATE TABLE ida.credential_event_store(
	event_id character varying(36) NOT NULL,
	event_topic character varying(256) NOT NULL,
	credential_transaction_id character varying(64) NOT NULL,
	publisher character varying(128),
	published_on_dtimes timestamp,
	event_object character varying,
	status_code character varying(36),
	retry_count smallint,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_ces_id PRIMARY KEY (event_id)
);

CREATE INDEX ind_ces_id ON ida.credential_event_store (cr_dtimes);

CREATE INDEX idx_cred_evt_pending
ON ida.credential_event_store (retry_count, cr_dtimes)
WHERE status_code IN ('NEW', 'FAILED');

ALTER TABLE ida.credential_event_store SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 1000
);

CREATE INDEX IF NOT EXISTS cred_event_store_status_cr_dtimes ON ida.credential_event_store USING btree (status_code desc, retry_count, cr_dtimes) WHERE status_code in ('NEW','FAILED');

CREATE TABLE ida.data_encrypt_keystore(
	id bigint NOT NULL,
	key character varying(64) NOT NULL,
	key_status character varying(16),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	CONSTRAINT pk_dekstr_id PRIMARY KEY (id)
);

ALTER TABLE ida.data_encrypt_keystore SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE INDEX IF NOT EXISTS encrypt_id ON ida.data_encrypt_keystore USING btree (id);

CREATE TABLE ida.hotlist_cache (
	id_hash character varying(128) NOT NULL,
	id_type character varying(128) NOT NULL,
	status character varying(64),
	start_timestamp timestamp,
	expiry_timestamp timestamp,
	CONSTRAINT "pk_idHashidType" PRIMARY KEY (id_hash,id_type)
);

CREATE INDEX ind_hc_idhsh_etp ON ida.hotlist_cache (id_hash, expiry_timestamp);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hotlist_idhash_idtype
ON ida.hotlist_cache (id_hash, id_type);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hotlist_active
ON ida.hotlist_cache (id_hash, id_type, status)
WHERE status = 'Blocked';

ALTER TABLE ida.hotlist_cache SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 10,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 10
);

CREATE TABLE ida.ident_binding_cert_store (
	cert_id character varying(36) NOT NULL,
	id_vid_hash character varying(256) NOT NULL,
	token_id character varying(128) NOT NULL,
	certificate_data character varying NOT NULL,
	public_key_hash character varying(1024) NOT NULL,
	cert_thumbprint character varying(100) NOT NULL,
	partner_name character varying(128) NOT NULL,
	auth_factor character varying(100) NOT NULL,
	cert_expire timestamp NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT uni_public_key_hash_const UNIQUE (public_key_hash)
);

ALTER TABLE ida.ident_binding_cert_store SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 10,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 10
);

CREATE TABLE ida.identity_cache(
	id character varying(256) NOT NULL,
	token_id character varying(128) NOT NULL,
	demo_data bytea NOT NULL,
	bio_data bytea NOT NULL,
	expiry_timestamp timestamp,
	transaction_limit smallint,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	identity_expiry timestamp,
	CONSTRAINT pk_idcache_id PRIMARY KEY (id)
);

CREATE INDEX ind_id ON ida.identity_cache (id);
CREATE INDEX IF NOT EXISTS idx_identity_cache_cr_dtimes ON ida.identity_cache USING btree (cr_dtimes);

ALTER TABLE ida.identity_cache SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 500,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 500
);

CREATE TABLE ida.key_alias(
    id character varying(36) NOT NULL,
    app_id character varying(36) NOT NULL,
    ref_id character varying(128),
    key_gen_dtimes timestamp,
    key_expire_dtimes timestamp,
    status_code character varying(36),
    lang_code character varying(3),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    cert_thumbprint character varying(100),
    uni_ident character varying(50),
    CONSTRAINT pk_keymals_id PRIMARY KEY (id),
    CONSTRAINT uni_ident_const UNIQUE (uni_ident)
);

ALTER TABLE ida.key_alias SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 5,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 5
);

CREATE TABLE ida.key_policy_def(
    app_id character varying(36) NOT NULL,
    key_validity_duration smallint,
    is_active boolean NOT NULL,
    pre_expire_days smallint,
    access_allowed character varying(1024),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    CONSTRAINT pk_keypdef_id PRIMARY KEY (app_id)
);

ALTER TABLE ida.key_policy_def SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 2,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 2
);

CREATE TABLE ida.key_policy_def_h(
    app_id character varying(36) NOT NULL,
    eff_dtimes timestamp NOT NULL,
    key_validity_duration smallint,
    is_active boolean NOT NULL,
    pre_expire_days smallint,
    access_allowed character varying(1024),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    CONSTRAINT pk_keypdefh_id PRIMARY KEY (app_id,eff_dtimes)
);

ALTER TABLE ida.key_policy_def_h SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 2,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 2
);

CREATE TABLE ida.key_store(
	id character varying(36) NOT NULL,
	master_key character varying(36) NOT NULL,
	private_key character varying(2500) NOT NULL,
	certificate_data character varying(2500) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_keystr_id PRIMARY KEY (id)
);

ALTER TABLE ida.key_store SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 2,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 2
);

CREATE TABLE ida.kyc_token_store(
    id character varying(36) NOT NULL,
    id_vid_hash character varying(128) NOT NULL,
    kyc_token character varying(128),
    psu_token character varying(128),
    oidc_client_id character varying(128),
    request_trn_id character varying(64),
    token_issued_dtimes timestamp,
    auth_req_dtimes timestamp,
    kyc_token_status character varying(36),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    CONSTRAINT pk_key_id PRIMARY KEY (id),
    CONSTRAINT kyc_token_const UNIQUE (kyc_token)
);

ALTER TABLE ida.kyc_token_store SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 100,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 100
);

CREATE TABLE ida.misp_license_data (
	misp_id character varying(36) NOT NULL,
	license_key character varying(128) NOT NULL,
	misp_commence_on timestamp NOT NULL,
	misp_expires_on timestamp,
	misp_status character varying(36) NOT NULL,
	policy_id character varying(50),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT misp_license_data_pk PRIMARY KEY (misp_id)
);

CREATE INDEX ind_mld_lk ON ida.misp_license_data (license_key);

ALTER TABLE ida.misp_license_data SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE TABLE ida.oidc_client_data (
	oidc_client_id character varying(100) NOT NULL,
	oidc_client_name character varying(128) NOT NULL,
	oidc_client_status character varying(36) NOT NULL,
	user_claims character varying(1024) NOT NULL,
	auth_context_refs character varying(1024) NOT NULL,
	client_auth_methods character varying(1024) NOT NULL,
	partner_id character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT oidc_client_data_pk PRIMARY KEY (oidc_client_id)
);

ALTER TABLE ida.oidc_client_data SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE TABLE ida.otp_transaction(
	id character varying(36) NOT NULL,
	ref_id character varying(64) NOT NULL,
	otp_hash character varying(512) NOT NULL,
	generated_dtimes timestamp,
	expiry_dtimes timestamp,
	validation_retry_count smallint,
	status_code character varying(36),
	lang_code character varying(3),
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_otpt_id PRIMARY KEY (id)
);

CREATE INDEX ind_refid ON ida.otp_transaction (ref_id,status_code);

CREATE INDEX idx_otp_txn_ref_status_gen
ON ida.otp_transaction (ref_id, status_code, generated_dtimes DESC);

CREATE INDEX IF NOT EXISTS idx_is_deleted ON ida.otp_transaction USING btree (is_deleted);
CREATE INDEX IF NOT EXISTS idx_refid_generated ON ida.otp_transaction USING btree (ref_id, generated_dtimes);

ALTER TABLE ida.otp_transaction SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 100,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_analyze_threshold = 100
);

CREATE TABLE ida.partner_data (
	partner_id character varying(36) NOT NULL,
	partner_name character varying(128) NOT NULL,
	certificate_data bytea ,
	partner_status character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT partner_data_pk PRIMARY KEY (partner_id)
);

CREATE INDEX ind_pd_pid ON ida.partner_data (partner_id);

ALTER TABLE ida.partner_data SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE TABLE ida.partner_mapping (
	partner_id character varying(36) NOT NULL,
	policy_id character varying(36) NOT NULL,
	api_key_id character varying(100) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT partner_mapping_pk PRIMARY KEY (partner_id,policy_id,api_key_id)
);

CREATE INDEX ind_pm_pid ON ida.partner_mapping (partner_id);

ALTER TABLE ida.partner_mapping SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE TABLE ida.policy_data (
	policy_id character varying(36) NOT NULL,
	policy_data bytea NOT NULL,
	policy_name character varying(128) NOT NULL,
	policy_status character varying(36) NOT NULL,
	policy_description character varying(256),
	policy_commence_on timestamp NOT NULL,
	policy_expires_on timestamp,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted bool DEFAULT false,
	del_dtimes timestamp,
	CONSTRAINT policy_data_pk PRIMARY KEY (policy_id)
);

CREATE INDEX ind_pl_pid ON ida.policy_data (policy_id);

ALTER TABLE ida.policy_data SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE TABLE ida.uin_auth_lock(
	token_id character varying(128) NOT NULL,
	auth_type_code character varying(36) NOT NULL,
	lock_request_datetime timestamp NOT NULL,
	lock_start_datetime timestamp NOT NULL,
	lock_end_datetime timestamp,
	status_code character varying(36) NOT NULL,
	lang_code character varying(3) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	unlock_expiry_datetime timestamp,
	CONSTRAINT pk_uinal PRIMARY KEY (token_id,auth_type_code,lock_request_datetime)
);

CREATE INDEX ind_ual_id ON ida.uin_auth_lock (token_id);
CREATE INDEX IF NOT EXISTS idx_ual_token_auth_crd ON ida.uin_auth_lock USING btree (token_id, auth_type_code, cr_dtimes DESC);
CREATE INDEX IF NOT EXISTS idx_uin_auth_lock_token_auth_crd_desc ON ida.uin_auth_lock USING btree (token_id, auth_type_code, cr_dtimes DESC) INCLUDE (status_code, unlock_expiry_datetime);

ALTER TABLE ida.uin_auth_lock SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE TABLE ida.uin_hash_salt(
	id bigint NOT NULL,
	salt character varying(36) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	CONSTRAINT pk_uinhs PRIMARY KEY (id)
);

CREATE INDEX ind_uhs_id ON ida.uin_hash_salt (id);

ALTER TABLE ida.uin_hash_salt SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_vacuum_threshold = 50,
    autovacuum_analyze_scale_factor = 0.1,
    autovacuum_analyze_threshold = 50
);

CREATE SEQUENCE ida.BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE ida.BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE ida.BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;

GRANT usage, SELECT ON ALL SEQUENCES
   IN SCHEMA ida
   TO postgres;


-- Combined SQL dump - 2026-04-24 17:18:02

-- ============================================================
-- File: api_key_data_202604241713.sql
-- ============================================================

INSERT INTO ida.api_key_data (api_key_id,api_key_commence_on,api_key_expires_on,api_key_status,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes) VALUES
	 ('198833','2026-04-15 19:46:01.985','2041-04-15 19:46:01.985','ACTIVE','PartnerManagementServiceImpl','2026-04-15 19:46:28.835','',NULL,false,NULL);


-- ============================================================
-- File: ca_cert_store_202604241713.sql
-- ============================================================

INSERT INTO ida.ca_cert_store (cert_id,cert_subject,cert_issuer,issuer_id,cert_not_before,cert_not_after,crl_uri,cert_data,cert_thumbprint,cert_serial_no,partner_domain,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes,ca_cert_type) VALUES
	 ('1708425a-8103-4837-9cfd-018b96b0a1d7','CN=Partner-CA,OU=Partner-OU,O=Partner-Org,L=Bangalore,ST=KA,C=IN','CN=Partner-CA,OU=Partner-OU,O=Partner-Org,L=Bangalore,ST=KA,C=IN','1708425a-8103-4837-9cfd-018b96b0a1d7','2026-04-15 17:44:49','2046-04-15 17:44:49',NULL,'-----BEGIN CERTIFICATE-----
MIIDijCCAnKgAwIBAgIUdx+OZb+bY3uPS9FCvQQBnZCHdHYwDQYJKoZIhvcNAQEL
BQAwbjELMAkGA1UEBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCYW5nYWxv
cmUxFDASBgNVBAoMC1BhcnRuZXItT3JnMRMwEQYDVQQLDApQYXJ0bmVyLU9VMRMw
EQYDVQQDDApQYXJ0bmVyLUNBMB4XDTI2MDQxNTA5NDQ0OVoXDTQ2MDQxNTA5NDQ0
OVowbjELMAkGA1UEBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCYW5nYWxv
cmUxFDASBgNVBAoMC1BhcnRuZXItT3JnMRMwEQYDVQQLDApQYXJ0bmVyLU9VMRMw
EQYDVQQDDApQYXJ0bmVyLUNBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC
AQEAm3pY8RzyemSEr7DsuVf5+OjdpoE0AHLfnX7S/vMokoNEQw7ycNGJeb3QHpHK
/M/nlLOaWETAnvqjVMJp8VirdgfrJQuXa2DcRY3jLAWPJFREyyjzVhPP1XVAUltK
yp+BLe18PcDLmP2LVnpNGU3Os8trlVSHYsIWlQbY3XNjNCqyWwEFe22mJwFtrGQ5
u+bEltkeGPz37Xn/2cXmyD3o/afLpz0qKCATPKfnBfU+NAX+jV9a5ygd7G6+OqNG
lQO9DuOj2PoI1Oz+Hm1RoEt3x8+WIKCFbUvE08LtEIBt9lon2oAcA8/KL2+gMHRp
qba68QCAMB67PHHMvf240CwC/QIDAQABoyAwHjAPBgNVHRMECDAGAQH/AgECMAsG
A1UdDwQEAwIChDANBgkqhkiG9w0BAQsFAAOCAQEARrC6gkpihq9psmjIzr1ksacF
Q0zfOt71mMITddedtbadkdIZVDaNgYaptkHhM00zDcoSq8r2Kwg4lwYFk45vldvy
ALgHXm9UoemXJ4up5LhYauu6yAP1RiFJx/j2p2e5Rv4E5aKxZ7zYckpcaaey/FEs
GYb47PtVZFB4Id2EOAnmxFXW4KUyVslFeNnswvsTPVRtfytjJgtMMuKtENP3hl7p
zfw3qnLrIsML70O4u+7HMsbENmAHqrVJzRszNw7tEAGv/oO09php+/XPQgQPwAyN
dEY6v1HUKRpMLu7xE8P7AE3aIiB/RKBru5j11XFkfOMaf6wVzGHlnOmjEGSizw==
-----END CERTIFICATE-----
','2623db763f30a014cb296261c9728a93eecc2987','680073629397002092626894953942534848185591362678','AUTH','SYSTEM','2026-04-15 10:50:10.072',NULL,NULL,false,NULL,'ROOT'),
	 ('ac4c2231-e4b6-4a2a-83c2-ea8c27b6615a','CN=Partner-SubCA,OU=Partner-SubOU,O=Partner-SubOrg,L=Bangalore,ST=KA,C=IN','CN=Partner-CA,OU=Partner-OU,O=Partner-Org,L=Bangalore,ST=KA,C=IN','1708425a-8103-4837-9cfd-018b96b0a1d7','2026-04-15 17:51:07','2046-04-15 17:51:07',NULL,'-----BEGIN CERTIFICATE-----
MIIDkzCCAnugAwIBAgIUbuYop6mW76prk73i+LYBnZCNONcwDQYJKoZIhvcNAQEL
BQAwbjELMAkGA1UEBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCYW5nYWxv
cmUxFDASBgNVBAoMC1BhcnRuZXItT3JnMRMwEQYDVQQLDApQYXJ0bmVyLU9VMRMw
EQYDVQQDDApQYXJ0bmVyLUNBMB4XDTI2MDQxNTA5NTEwN1oXDTQ2MDQxNTA5NTEw
N1owdzELMAkGA1UEBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCYW5nYWxv
cmUxFzAVBgNVBAoMDlBhcnRuZXItU3ViT3JnMRYwFAYDVQQLDA1QYXJ0bmVyLVN1
Yk9VMRYwFAYDVQQDDA1QYXJ0bmVyLVN1YkNBMIIBIjANBgkqhkiG9w0BAQEFAAOC
AQ8AMIIBCgKCAQEApM4U/nRo6NnZgnKaJnOCmIIdDv85ww3GWHNKWnMXbs0WMhgY
h920qr+Q5yGtDqB2+TArPYo9CHBtXJqfB/N/F0mMbf6391gZHkMup3nOic+Zb3o+
SvSKneh4HjnvaxqST8JOseLkVPUwN4qaQMOOBwxiWKCP1NsbMwZKtwGU6A8bOWYX
mikgCvehYOLYhO/xcj/tcsxMw3drCNmfMg7oJjX+8ITxn9ASJ3CvA/iz8CYFddYM
vBjfGtrrQQXcbjIWb/aIyZRs5XSS5osz91veYWsjxlDWF6nTcLvl4+FJkhoPEOYc
ftc0XgX3G9mTuRdSy9eiNoXtxdWL/idovv03uwIDAQABoyAwHjAPBgNVHRMECDAG
AQH/AgEBMAsGA1UdDwQEAwIChDANBgkqhkiG9w0BAQsFAAOCAQEATy4fLZ7FY8dK
ICTYzsalgxntvU6qE5slv3SwbVp7I0yx56vDdZsRMLMldBQbOdSPPWk2r7zKnWL4
jCPnPer/mDVXD8mx3FAz3+C8xSA6r67DvPwwVMj4Po/Edl8PdJxCklNwk1mn5vtM
UTIMzestDRMhUYxj1XO2pwXJoFg23ZUS3SzKMnHhKwm+MvGdFyAqqUyWE+daHpBe
WHAZdBaVQw6dK9CYi8PEjgcgUHyOH95rCsygyPcRbidxchv1Q8VCDw8FQAaK9XrA
if8vzj4yYK1W7MCeZymhAb/O1akfxSg3TK8Eo+i5DGYkd1DRx1ZbpZbeMlhyHDUd
JAP2JVWglw==
-----END CERTIFICATE-----
','3ecf093af132b083873b1b933cc91d599c970d8c','633121697730300174009195716799736416905802365143','AUTH','SYSTEM','2026-04-15 10:50:42.7',NULL,NULL,false,NULL,'INTERMEDIATE'),
	 ('8972c52e-cc2b-40b2-9296-5e20d16ba342','CN=www.mosip.io (ROOT),OU=IIITB,O=mosip,L=BANGALORE,ST=KA,C=IN','CN=www.mosip.io (ROOT),OU=IIITB,O=mosip,L=BANGALORE,ST=KA,C=IN','8972c52e-cc2b-40b2-9296-5e20d16ba342','2026-04-15 09:43:06','2051-04-15 09:43:06',NULL,'-----BEGIN CERTIFICATE-----
MIIDoTCCAomgAwIBAgIIv4DwsGv6zCAwDQYJKoZIhvcNAQELBQAwbDELMAkGA1UE
BhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoM
BW1vc2lwMQ4wDAYDVQQLDAVJSUlUQjEcMBoGA1UEAwwTd3d3Lm1vc2lwLmlvIChS
T09UKTAgFw0yNjA0MTUwMTQzMDZaGA8yMDUxMDQxNTAxNDMwNlowbDELMAkGA1UE
BhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoM
BW1vc2lwMQ4wDAYDVQQLDAVJSUlUQjEcMBoGA1UEAwwTd3d3Lm1vc2lwLmlvIChS
T09UKTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAO+7qcUmYN2ek5cb
2q+JL6RqGtHMM6gQjPHGCGBTo+zXs8o2Bx+a8uaZuE25qEDr+dV6MUWm6ALaHsLp
2uD6/dqDKGXIW/d4FYVoi3wR9GG3yBgSObKtz7d4RpXLvN4a8lwY9PmTyAoczqQ6
dMDXpQMXA9Nyr1Sbgip4S71ScMf1q/Bkl+VjuonlP6gTZXfsBRJq5X09DBKDqWBj
DlTxS8iYbIflsx4PP9WSsJwHlgw6PzSs5bIca4ysh5/XS/9+lBbGDWe4UYyGeSrX
fzakLQkCa3rj3hYGc9HcmH2aO1/whXOx/yjOUyF/5onenMxObfCi06cOT+BGpvxp
DOX5oh8CAwEAAaNFMEMwEgYDVR0TAQH/BAgwBgEB/wIBAjAdBgNVHQ4EFgQU836z
uERHjkNz/Ha7Qro3M/O4UcgwDgYDVR0PAQH/BAQDAgKEMA0GCSqGSIb3DQEBCwUA
A4IBAQAzDOV4SspRhLGjlOI6Au+tLvJPLxRbEfE2aFPUwFiEuDrCChLVSArBvPVI
pIuv208hgh1kCiNNjJVyYj7Sd0ESGr/5Kq1uKOH7wOE6sxVSSFBHjLi4P/oNMPv8
00fNfFrF5pMLUDPyCuNriIMifJl+eWJOMsU+5+xkh2zu3m7uv6icD1ehd2eDBNj2
Uty7weWQCbOPYYA/2peSqZpRjAwbKhOsIFjCpxgqnn55b4ldQLB49L7D+i+O7Bsk
K5xBfU1jaUCTWaaZWIBXoBAAOE8oc/6vLhEFflXaPsW3dXw5n9ZjghFleAmRMdmj
92at873L2l3/Yblqruee/L7RXhy6
-----END CERTIFICATE-----
','279407061b08eb8872de23aea2ace310cf3c637f','-4647450174929843168','AUTH','SYSTEM','2026-04-15 10:52:11.419',NULL,NULL,false,NULL,'ROOT'),
	 ('f00c02be-710e-408a-9170-4b02fbc18819','CN=www.mosip.io (PMS),OU=IIITB,O=mosip,L=BANGALORE,ST=KA,C=IN','CN=www.mosip.io (ROOT),OU=IIITB,O=mosip,L=BANGALORE,ST=KA,C=IN','8972c52e-cc2b-40b2-9296-5e20d16ba342','2026-04-15 09:43:23','2046-04-15 09:43:23',NULL,'-----BEGIN CERTIFICATE-----
MIIDnjCCAoagAwIBAgIIahfRl+kWggAwDQYJKoZIhvcNAQELBQAwbDELMAkGA1UE
BhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoM
BW1vc2lwMQ4wDAYDVQQLDAVJSUlUQjEcMBoGA1UEAwwTd3d3Lm1vc2lwLmlvIChS
T09UKTAeFw0yNjA0MTUwMTQzMjNaFw00NjA0MTUwMTQzMjNaMGsxCzAJBgNVBAYT
AklOMQswCQYDVQQIDAJLQTESMBAGA1UEBwwJQkFOR0FMT1JFMQ4wDAYDVQQKDAVt
b3NpcDEOMAwGA1UECwwFSUlJVEIxGzAZBgNVBAMMEnd3dy5tb3NpcC5pbyAoUE1T
KTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKDrhwXsH845hfaDfA7F
8tLMLe1tapFOOSoexZr8yX2F6cMRnfn7dnQ5ZUIWJmL+HIiIT5G+stMWTpbDXBWT
yYiyOX7eE0Q+HB3l/OikVdj3Rn6N8TJnmz6Y1jHIRu4AaqCxqmvWzEGVZk1yC0Rv
eqY3P3bMrdj5b5AbJYS+UNjWd6ce7Ddj24lWcihvHdoy/rhbigsQ7doLX6Q47M6r
C96GzhAE1ykejkbpHVxKrntoKlwfJ5Sj0ttDJfFSIMIoSnra9TJaDyzsDYYdS7D+
VLxRkuP0i/l9egx9i3yjsVbaJI0glDsHMhm9Os3bzwvnEJf26A1JtZG7Bkg0L4AO
ZasCAwEAAaNFMEMwEgYDVR0TAQH/BAgwBgEB/wIBATAdBgNVHQ4EFgQUZsJcc9wf
87tyjj5Sd9l444X7pcwwDgYDVR0PAQH/BAQDAgKEMA0GCSqGSIb3DQEBCwUAA4IB
AQAVTOQ9o05ZWlwGRQgq/5FnYecj29O0GzwsVJA7djURK1PriQRw6tIbWe+4UJXV
1RCg/b/ssWr4WUgXzak9f7iq7bGy9usM42/l3DL/MkwnQRQTtM1KBl/oOcoru229
NndoHDAD2EprrFiUgwsxGwanv4uoNclpWjNe/16QQChTQG+3XRqDtymJVhnVExQN
y9kVQNEGD0QzaSAOWfjynynseslYEejksaEOdcUhFRFrczn7V+MNkFEkfOZoQK8Y
42zroAuwaAxdKDt+dO7nRkpQqfT1cnV94hron5pbAytLs/5YJ7slDGiuWwPyn6sQ
2JK1otnhz514EYXnAKRJxsvr
-----END CERTIFICATE-----
','0860f32c1dffa0e0d85a37a5928d83ce9842fa4a','7644809342865539584','AUTH','SYSTEM','2026-04-15 10:52:11.426',NULL,NULL,false,NULL,'INTERMEDIATE'),
	 ('e0080a22-fda2-4408-b221-669fab4bd17e','CN=Partner-CN,OU=Partner-OU,O=Partner-Org,L=Bangalore,ST=KA,C=IN','CN=www.mosip.io (PMS),OU=IIITB,O=mosip,L=BANGALORE,ST=KA,C=IN','f00c02be-710e-408a-9170-4b02fbc18819','2026-04-15 10:52:11','2041-04-11 10:52:11',NULL,'-----BEGIN CERTIFICATE-----
MIIDnTCCAoWgAwIBAgII2JwkweNNsy8wDQYJKoZIhvcNAQELBQAwazELMAkGA1UE
BhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoM
BW1vc2lwMQ4wDAYDVQQLDAVJSUlUQjEbMBkGA1UEAwwSd3d3Lm1vc2lwLmlvIChQ
TVMpMB4XDTI2MDQxNTAyNTIxMVoXDTQxMDQxMTAyNTIxMVowbjELMAkGA1UEBhMC
SU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCYW5nYWxvcmUxFDASBgNVBAoMC1Bh
cnRuZXItT3JnMRMwEQYDVQQLDApQYXJ0bmVyLU9VMRMwEQYDVQQDDApQYXJ0bmVy
LUNOMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqZcHf2qClKQa8Hn8
b8QlaT1osoosHELAd0X7S2MUWqU1FPs4oB1KCDdkuHrx6AejOMr5Bs2H5Vix3BtV
/owDwG3RI7q5I9MZ9oFjM/VnXOwymrdzVrYt7ibfrktLafw+Wk4kzUnUdwxm4WoB
jMk00dt/LntYa1pJg+vJexRsW30znS9uUZbRU/uT2ImR1EZevzLTTrdA2fBpGz3a
1y1KOwt+UHZUdDE2FV86oHuoz21wgYTIKX2aGL5zpjZvG6F9IuSaG5h2FN2222yP
tZSjpdJ2c7VtKsMZ9GqqH2irfjMNvNaSUeHRyMKe/2aaZ3wEILNayspzsJSgx0ta
S1e8+QIDAQABo0IwQDAPBgNVHRMBAf8EBTADAQH/MB0GA1UdDgQWBBRzCyAZgO5i
VWyieyQag3W2qMhdRDAOBgNVHQ8BAf8EBAMCAqQwDQYJKoZIhvcNAQELBQADggEB
AEq0ujgocNoizU4vL6RS2xFiJwOvJmOxGEVU5g3DcszA9Xac3sDJ5ZEGj+pp+cZB
qG6Enow7tN7CCSxcLH5sYDWOgqzRM0tn5Fo14QHqnHzcwYG94zJ8gRQQc4/TY/nr
H/416EhuXs76mkL7HSfzd4v76hMRDprihT+zeBv+kqQXiZbLsVImp/J64RDQ/O5N
n49KYEdhLlSNvBXgG3kkDk5QA9bC+JuKYnvnoZt1T4J0SHz2Sc2Xb4gWJCbagdcB
g4HPXuvPzJY37+3eBSAkmrM7gUH/qL50J1lABe5qyJBB0uLpoNJUCE19+JEAMuxF
U1fs+kMA0HJFkwIueCENMjo=
-----END CERTIFICATE-----
','7884f8e398f7768cab7edf4002ceac16ca890d21','-2838353249989446865','AUTH','SYSTEM','2026-04-15 10:52:11.433',NULL,NULL,false,NULL,'INTERMEDIATE');


-- ============================================================
-- File: credential_event_store_202604241713.sql
-- ============================================================

INSERT INTO ida.credential_event_store (event_id,event_topic,credential_transaction_id,publisher,published_on_dtimes,event_object,status_code,retry_count,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes) VALUES
	 ('0f1e86c8-cae5-4f73-8ce9-9a966393fc9d','mpartner-default-auth/CREDENTIAL_ISSUED','582e3acb-316b-44ab-838c-61252fa0b665','CREDENTIAL_SERVICE','2026-04-21 15:51:51.505','{"publisher":"CREDENTIAL_SERVICE","topic":"mpartner-default-auth/CREDENTIAL_ISSUED","publishedOn":"2026-04-21T15:51:51.505Z","event":{"id":"0f1e86c8-cae5-4f73-8ce9-9a966393fc9d","transactionId":"582e3acb-316b-44ab-838c-61252fa0b665","type":{"namespace":"mosip","name":"mosip"},"timestamp":"2026-04-21T15:51:51.505Z","dataShareUri":"https://api-internal.dev.mosip.net/v1/datashare/get/mpolicy-default-auth/mpartner-default-auth/mpartner-default-authmpolicy-default-auth202604211551513PP8QkfROsg","data":{"demoEncryptedRandomKey":"0_71qS8ZA-9Hh8jlX1KmQIvWfoOvHSC07x4W9JwAPDon3brhcGQR6hUagIJs5DMpw1tPRjjMAe5UcRLCtPu3Cn0H9uORZhwW63qnPYkj-4miG5HeYffF7Dycpov1iralXl_UAaK27ZZAfgX107wfo0LpdyYVVxHee-F_ygvJBhNT6cktNZ79hnAwpP-d9U6wURFI-CxypKeup4yIxGq68WTeKTOHHcu5K40BOHMLmgmAGv9ZhGvEcNYYksXwx7584K-aqabggbmQWZAT2e_5Y8A98s1gwvYBah8L1LHMrTOZqdir9kMWY-Tk5HNTqbhpK_l2hIdFp9E6oguiInmEm4DZmmQ2YnhYDQqCca5ZeUb7D3V5c1l3W-twOPhPm8EP","demoRankomKeyIndex":"2702","credentialType":"auth","protectionKey":null,"MODULO":"120","SALT":"BV49P/7F4lBSVc21ZCf2Wg==","TOKEN":"77203FE8EF08E2B9E7602952414951124FE88858CFECC74C49553D0B228D2848","id_hash":"DE1963FCFC113881D3916372028848D7BA8F2943FC8858C03C6BFE37F8842E06","transaction_limit":null,"expiry_timestamp":"2041-04-18T15:51:51.505Z","proof":{"signature":"eyJhbGciOiAiUlMyNTYiLCAieDVjIjogWyJMUzB0TFMxQ1JVZEpUaUJEUlZKVVNVWkpRMEZVUlMwdExTMHRDazFKU1VSdFZFTkRRVzlIWjBGM1NVSkJaMGxKVlRSdVZXaHpiMDVEXG5UVFIzUkZGWlNrdHZXa2xvZG1OT1FWRkZURUpSUVhka2FrVk1UVUZyUjBFeFZVVUtRbWhOUTFOVk5IaERla0ZLUW1kT1ZrSkJaMDFCXG5hM1JDVFZKSmQwVkJXVVJXVVZGSVJFRnNRMUZWTlVoUlZYaFFWV3RWZUVSVVFVeENaMDVXUWtGdlRRcENSV3hLVmtWSmVFbEVRV1ZDXG5aMDVXUWtGelRVWXdNVkJWTUd4UlRGWlNSbEV3WjNSUk1GWlBWa1ZXVTBsRGFGRlVWazF3VFZKVmQwVjNXVVJXVVZGRUNrUkJlRE5rXG5NMk4xWWxjNWVtRllRWFZoVnpoM1NHaGpUazFxU1hoTlZFVXpUVlJGZUU1cVVYcFhhR05PVFdwTmVFMVVSVE5OVkVWNFRtcFJlbGRxXG5RbVlLVFZGemQwTlJXVVJXVVZGSFJYZEtTbFJxUlV4TlFXdEhRVEZWUlVOQmQwTlZNVkY0UlZSQlVFSm5UbFpDUVdOTlEwVkthR0p0XG5aSE5pTTBwc1RWRXdkd3BEZDFsRVZsRlJTMFJCVWxWYVdFNHdUVkU0ZDBSUldVUldVVkZNUkVGYVZWcFlUakJVTVZWNFJVUkJUMEpuXG5UbFpDUVUxTlFqTkNhR051VW5WYVdFbDNDbWRuUldsTlFUQkhRMU54UjFOSllqTkVVVVZDUVZGVlFVRTBTVUpFZDBGM1oyZEZTMEZ2XG5TVUpCVVVSUlJFUmtVWGhsYVdOdVRrNWlOMXB5WjI0NFJXTUtRMnBXVG1WVWNGcHdSSEZsT1cxaFUxWnFibGQ0VWtaUk5uUnlVbWczXG5lVk40U1ZsYWNHOWphVXhoTm5JeGJtNXpiMGd4T1hrME5Wb3pORWsyZUdaVldncHhUbWhJV25KRk5qYzVXazh5V1RBMk0wUktjMGhaXG5aVTVuV0hwWE9UTjZhMWszYVdsaVFqVTJMMVEyUjBWWGRYQTJabkl3VlZoVlIwWTNTVVY1V1ZCeENqUXlha2xsYm10WWIyVTVPRWx3XG5OU3R0TUVwRU1sVTNVekF5ZDNWa0wyUlZURGxKZDBKSU1HZHpNRk15T0dveE0yRlRjMEl2Y1UxVVdFWnBiMmRJY3pVS05ETkVURTh3XG5ZVU00V2twUEt6aHpaVlJ6SzFsdEszVXlibXhQVFVOeGJpOXhNQ3NyTTBsc2VHWjZRVTl1V1U5RlIwSktVV3RVZUM5c2VXbFlla2RNXG5iZ3BVTlVkbGRVNHZPVGRFZW5wdGRIUXZRamhoTUhBM1QwTlVjbkJUU0VFNFNYUTJORUpQUlV0S1EyVnJTRkE1WVRSMVJUZDVSMEZpXG5WbTV2YXpRMVpVVjJDa0ZuVFVKQlFVZHFVV3BDUVUxQk9FZEJNVlZrUlhkRlFpOTNVVVpOUVUxQ1FXWTRkMGhSV1VSV1VqQlBRa0paXG5SVVpQYldGTGVGRkljek0yUkV0S2IxZ0tNbEZKWVRkeFdXSkhkblI0VFVFMFIwRXhWV1JFZDBWQ0wzZFJSVUYzU1VOd1JFRk9RbWRyXG5jV2hyYVVjNWR6QkNRVkZ6UmtGQlQwTkJVVVZCV0hGVFdBcG5XVWhVUkd0MVVYQTVXSEV2Y0c5TVdFZHljakZYZFdNMGJGY3JibE4wXG5keXRqZERkVWVIRTVNVzFZUzJwUWRIUkNTSEF3TW1KV1FXSXJXVmRNVm5sM0NuUkxhWFI2WlVsb2FHcEtaVmhIYm1OVU9GcHVTVU55XG5PRGhNUmxsTmVYcE9aMVV5YldoT1VscE1PVUpOWkdaaVNsbG1hRkoyVlhkTVFqZHlaM0ZSYm1nS1JUQk1WR1VyU0dwUmJDOTFiazl5XG5WQ3N6UzI5SGFYZFBTREJHZGpkS2RUQnVPSE5qYnpKdk5HcEpjRkJuZG1sVFIxYzFjM28wUlRSbU4yWlFWRUZVUndwd00xZHphSE5MXG5ObFF6VDJwaFYxVkhTbnBOVEdOdk9HbE9RMnByWTFGWlZ6SkhORkJDWVZRd2VubHVRVUZ0Wm5aM1NuWkxNRFZCU1VwUlQwUjZXa3h4XG5DamxDWTIxNVVtRTFVa2RzV2pZd01sVkZVVWg2WW1GWGRIUm1NVnBQSzJwNE1FWkZNbkk1TUV0eGFHd3dTMWt2T1hjM2JscFlkMWN5XG5LMUpGWkV0V1NXNEtVWGRRZDBNM2QwVk1Sa3d6Vm1remNWTkJQVDBLTFMwdExTMUZUa1FnUTBWU1ZFbEdTVU5CVkVVdExTMHRMUW89XG4iXX0..ZQ_NFJxBU3SVB_mdW7QU7gQzYypvr2F6B7bB41-JsuoSktoN_TDy9y_2obUMQgYiCScI6GtOdlYT75MoBjCqSmWPiqcedpXU-yqWu5qEKKU_knzTbCYy4rQaQyaqq53tn7eE1VUAMnWj0M5YE23BtSN5pkL5g_Azv4d8q8ZTMzuzqRz5uItIHRk42_x3pblFZeAw35zr1jRYtnk5SxMtRRqvazsK_KvSUvyBhIDa59QJ06FPa1UGdbAKlZJq7PecLzFQBr4q1QAIcGgjzNgkhNbutG39C0cgRKBwweqlkIAzK57IUnmbO3_9x3T9pLChWwvWM7k6XH9YOVdZMjoW-Q"}}}}','STORED',0,'IDA','2026-04-21 15:51:51.625428','IDA','2026-04-21 15:52:26.645224',false,NULL);


-- ============================================================
-- File: data_encrypt_keystore_202604241713.sql
-- ============================================================

INSERT INTO ida.data_encrypt_keystore (id,"key",key_status,cr_by,cr_dtimes,upd_by,upd_dtimes) VALUES
	 (3656,'Oc+DEGlT/zvjxcs/J17Mc721ejuv6x7OSUgJo2UBnsg=',NULL,'IDA','2026-04-08 15:27:50.941705',NULL,NULL),
	 (4497,'QDpi2Tx1X/uuM6qH3UH3YAAhOWP0wUhDXdQ/SvoXH8o=',NULL,'IDA','2026-04-08 15:27:57.137727',NULL,NULL),
	 (4848,'ZqIma5jSz/E3LFKF8V6GJYCNax20HgJ5voATRcD8xqg=',NULL,'IDA','2026-04-14 14:06:59.775996',NULL,NULL),
	 (7469,'9en0mbCApbUOVGGMTHogetQjdHjTod/+pnH18jynYeE=',NULL,'IDA','2026-04-14 14:07:24.976001',NULL,NULL),
	 (3211,'gu/2F4JhaUmWowXFq6GChtDjrrMg0UC2reCYo5Sy6F4=',NULL,'IDA','2026-04-15 06:57:50.481987',NULL,NULL),
	 (8176,'2VHaobpcKngUyiZZevfg4kE408LELJ68zSKnzY8jlbc=',NULL,'IDA','2026-04-15 06:57:55.576718',NULL,NULL),
	 (1585,'jwLhLKLF0V/iutAjy6hxKBHiXT+4pnYu+QzAt9ec0vw=',NULL,'IDA','2026-04-15 07:01:26.318117',NULL,NULL),
	 (5430,'mw2QZhFxPjPjNef6pIlVhzG5GwAYe+++4O8C0RhJMKM=',NULL,'IDA','2026-04-15 07:01:33.491466',NULL,NULL),
	 (2702,'Qk2p1DCxcuOeDQppNYxDbpNk5UUhSXpf+K46aHS6YpQ=',NULL,'IDA','2026-04-21 15:52:26.338383',NULL,NULL);


-- ============================================================
-- File: identity_cache_202604241713.sql
-- ============================================================

INSERT INTO ida.identity_cache (id,token_id,demo_data,bio_data,expiry_timestamp,transaction_limit,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes,identity_expiry) VALUES
	 ('DE1963FCFC113881D3916372028848D7BA8F2943FC8858C03C6BFE37F8842E06','77203FE8EF08E2B9E7602952414951124FE88858CFECC74C49553D0B228D2848',decode('7B2266697273744E616D65223A224141414B6A7676523659314C53396451394E354E5F756F50756E336D654230746A56456B367467434951416764372D77787268784F4B572D57764F613242557375644A745557315774304B454D7570636E626335754D7A6E67566F51724A4656724463466F367445414B654C376756524F5665415948546E764275624F534B7A436237677A41554863642D7767677657446C443231696F4C6775316B79564151446B744347783273646F454931377343336777443550644A51776254633063615F324D544478574F697A6D3379413D3D222C2267656E646572223A224141414B6A693132614559714D346B415954424D7A3766387265705F364D6D596B4671785066466F643974636B527A76654432345073674445792D6549366F6E304B666466465F3852576679456F3668526678516F614441534B73685049785F48626937734F574B4A5355685071375448576342695336674B67593978554455366F625F394B63447749306947626A5A7A754D523078693479575F374E584132444878642D30734B4C4C434F642D5A616133695F3369706C3658555165585F3164674936754D444B56766D4861773D3D222C2263697479223A224141414B6A70636847504351486244744356514367714A5A4D386C6954724E356378535A5177556B5431352D43774B4A5F6C6B757A4157486A476B58364F796C46612D646768484A474D364E2D3679717A5466555230686E46334D754238673171466D6E473657386D4F646B422D52337A543550384636464B6A354C7667635552556163626633627A623248795347555F75325A556B4D3366527378515550484F32383370453730544B4F4977576143306A75727457666A4972426D4F736634387247597153514E63374E454F6E5431483748336861732D352D594E5A636C304F5461586F4B4A3579354C32456D665968746B3D222C22706F7374616C436F6465223A224141414B6A7443553549706C724E4B777161546C71664646636B6C4E386F58394A6A5130766761617A38546949376B6F30696A47484B65514D4157746D446E454E34663249474D6F6B3556546C54434742654F46776F7666556D5239222C226D6964646C654E616D65223A224141414B6A70496C79756A335934506D6866596E665F506970647A6936663571496A484866334E4279765174425238735452625365437A564C747971584C4B64596A35305A77715430334D774E5A54375866636561304F6E4F5873524178784F6B47567759676A57664674487A4A42336D6F346557516D336657743353626544635A615F3661375635646E51766C4F5462466B474E555A557456727053433752776C334C5864704B492D3379585F6C4A78463152784F516C583746366F39356271497A5A505667614F4F5F7446344655676F6676464D2D5A452D44623372773D222C22646174654F664269727468223A224141414B6A6C336D71566E2D6941546D35566A754271333752796359316675695530596667463437385F466F6D622D34596F58437A70664E4869547A6A71596E64457A52334F4A4E734C7966467371744732535A66376749767658706C5F4B656461493D222C226C6173744E616D65223A224141414B6A675164656E47322D476F5A79454D675730726A66724543373946694A4F676E72776849546D305869662D50546272694F61614A7837527A6B79354D524C31496278565151335A56427A6B4878484D2D7A2D3456332D305749775835634F68556179766D784454505A543753755F503047554E702D7052684F62392D743749625447634D444A3839584D4734785171776F435A78717A745F6F674E3837777944697853725A323841313350314B4E324E356A7738352D76425147666D3563422D64314F486935734950413373726C52754446673D222C2270726F76696E6365223A224141414B6A6F5F6A586A6253565F7331794331752D37367A313835316734335165416B59425A56694133595A704D367A4A594F794436425832357861652D6D48624171425F424755774473686A794A4D534B6F6273464761494531766C55526B6D5F71336270796B7A704E564A3667434A355272554863505F6E3353336E3257704E4472336B34717642534A4672733439633178487475306750626F647863396265444D4B4154686A4B6832744C70334F74336F55416354377A49445734666A36316C38586D335877304630324C6472346D53755546614377754361324E7A45586F4C524C7A496566456149222C2270686F6E65223A224141414B6A6F6B496473395177544C6B716636745479423750517A5F35496F53456F5A526C7A756945504C72734B7135577554344750737A6B616474745539457548317151585869624D743148433470527144565347365052596F74664C4A4B6F50343D222C227A6F6E65223A224141414B6A716F743231694B457856344B42356F2D51496163566F366F54384A4D62525F394434332D6F66304B2D4868704275303473726D3843446664794F7A5F4B74356364745A502D3439576C3961793630306B7979303133524C39676464464457454A527A4F696F444B625F3977456D65473749706A674A75554337344E566E776C4F554C2D427A70734A703068576834494C54684D54625857367A54365A55586F304C67767545592D3930793564556B694F7A6A42222C22616464726573734C696E6531223A224141414B6A76365F6347507955744867735935715542306F36733545495A6D4B46452D6B4677445F6F52764D3958544A476B514F4345397043334646464754787A3446684C4544644651614D7554494D7435563656486E4E61316D6F486C76796E41314B6C673172532D33673868425A616D42714D34397241735558384A782D2D2D5A367A4E56696F2D4750665476484F337A6750564C386152364F6B7734797745796C5A5672695A654C41446D4A6F756C435F5542654672346E6C7974514F4B2D765769355438534956315857644873335944314D44436654674442504A446366357833573153363476535959707667626C304464373664623341776E55785A686B5F677A727130766C3531734F4344774434353652596D4A52794D617669662D6F4B675834756671386D4C6D46356B46454576567A54716A553D222C22616464726573734C696E6532223A224141414B6A7673305A425F6849503272466A3974577A596E685942334963413657577A55524B305675557832654265325447755830766A4B76496F3253362D6F7970446C616C72794A7236375747307064693277787562473134656952504D7A4E354D4D4F5451466C4C357A79446144785356735F4837766A7730734664317A4D4251355345745A35417A746A3376736B444C633257464A327A73634C4F523042363374787748467737766F7146644879474B6E54756637395477476C48626B61786443354A367630556B43775152475A6A455563752D48445F74643741586D48566C5074425A565F4E2D2D7A55776D6E664A413355514E466946767066747362684D6F684D414335324874396B6B31717731335F592D6B646E4D30652D65576977302D55796D72572D543775784A3235375A6B6231337A45773D3D222C22616464726573734C696E6533223A224141414B6A743162534A4F304A4F38436B596E635652656F53666752346C6759706657737937416F684A7969344F61632D4D4338556F7973356C51503166614F526C4F75394363336A6B5F666765614A76324D47644361516C537042394C4730563037314367774771576B325042384B38705A303433346E6749302D4E4350332D476B7A633079586E496533616F39354E4559382D4D6D576F6D376545695235435546666A5453766C79335F324943646A7933576A39536D7962315F656C4A596E3444713861505875495F39395152754F634A794A57464F5158434F512D67774854554747586C39784E315778464D6F58636D556135433230426C794C5F72747A393368676B69615431476B4B47597833396B7647356A61747A70616543685671636A2D6844734362326B627836633D222C22726567696F6E223A224141414B6A6D6E6173514D4133704761464F31515A476D616752546A666D38635855384C4C5F503434374D455567614B4B3737585576386256793836476C397434617A396E67663261536F7A41496A4B4855376F3975684F685A79365A662D61465648556456323346696A76704451653549304C595A6366456A346131764E3048726A743568586952335A585655596647316162694C5774546B72464834536872484C6C4E65636C38376D494A41305F595033793256533967697143434A344F4D2D595A685A74413836747867626E5A47576D737955447339566A6D31724631796C4F5A34506D5954796C6B374158504E5A4964452D65543142325A6D797353436D6E37544459446E47716F39644A6C3870757573773D3D222C22656D61696C223A224141414B6A67742D446270566D7259644472307346735434785942464D764E4871573257426C745A33714C4F30443962647948496F69337858425162564E434A564B6C58304C65614D505F7A4A734835736E5A7167536367374351526C796A6956476E547561616F774745354A4144664C53453D227D','hex'),decode('7B7D','hex'),'2041-04-18 21:21:51.505',NULL,'IDA','2026-04-21 15:52:26.645005',NULL,NULL,false,NULL,NULL);


-- ============================================================
-- File: key_alias_202604241713.sql
-- ============================================================

INSERT INTO ida.key_alias (id,app_id,ref_id,key_gen_dtimes,key_expire_dtimes,status_code,lang_code,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes,cert_thumbprint,uni_ident) VALUES
	 ('582b6c75-119e-4291-8e2c-068d40e87cdb','ROOT','','2026-04-04 11:05:14.666662','2051-04-04 11:05:14.666662',NULL,NULL,'SYSTEM','2026-04-04 11:05:15.054976',NULL,NULL,false,NULL,'3205E1BB4E3ED4BCCB980EC169202ECA0C45A081122088F6386EA9BDCE40C920','74567C5D9BA5A82ADDDB5181E2DEC01A59530A4D'),
	 ('ce2df447-c17b-447f-ada9-5e67b7738431','IDA','SIGN','2026-04-04 11:14:23.558613','2046-04-04 11:14:23.558613',NULL,NULL,'SYSTEM','2026-04-04 11:16:25.578466',NULL,NULL,false,NULL,'66A9CA3C963F6987B0732DD29CEE63BDF407EB02BE797EF128B4B787E4833C9D','5C3570EC42EBBABF8EF4F738FBD052A4CAF40C59'),
	 ('36682983-ffb2-4538-afa0-ae25a6e9ad7a','IDA','','2026-04-04 11:16:35.147055','2046-04-04 11:16:35.147055',NULL,NULL,'SYSTEM','2026-04-04 11:16:35.442113',NULL,NULL,false,NULL,'E5C2BA5A0696555FF3FCC89CC45D21E7452D67F4CE43730ECE8F5799CCF6B38B','B45AC9B65699A9724D29A70C8ACB91EE368E832D'),
	 ('73380c0a-4430-4447-81ed-85ff757b24fd','IDA_KEY_BINDING','','2026-04-04 11:16:36.46999','2046-04-04 11:16:36.46999',NULL,NULL,'SYSTEM','2026-04-04 11:16:36.56019',NULL,NULL,false,NULL,'A028C477E09E300B41965510913A8A0290195E908744A9145928B0FD7DE2A6FF','7FBA2E446F96FE1D0FF169D0327B89D5B8E0223E'),
	 ('71bfa356-0fc7-4f58-a223-4c48464ea398','IDA_KYC_EXCHANGE','','2026-04-04 11:16:37.353366','2046-04-04 11:16:37.353366',NULL,NULL,'SYSTEM','2026-04-04 11:16:37.416199',NULL,NULL,false,NULL,'D30A63390855E0D7AD6AEC2782CE5451E94EF551598D07178249303E7117E89F','DC409D306BEFE393B8BA7EEE23C166BDE52BED77'),
	 ('ffea4d6a-e063-42b6-b512-76f1eb1902c7','IDA_VCI_EXCHANGE','','2026-04-04 11:16:37.935284','2046-04-04 11:16:37.935284',NULL,NULL,'SYSTEM','2026-04-04 11:16:38.107074',NULL,NULL,false,NULL,'E6B5E91FE93F0B717A1845DF47CC7785590D59613CAC0CC9BA2F5B2BAB55AB21','AC6148D495C419EE4BA31154AAFDED8372D23091'),
	 ('c5339537-4d8f-4cae-beca-6438a5e310b7','IDA','IDENTITY_CACHE','2026-04-04 11:16:38.566129','2031-04-03 11:16:38.566129',NULL,NULL,'SYSTEM','2026-04-04 11:16:38.581993',NULL,NULL,false,NULL,NULL,NULL),
	 ('2e44072b-8925-4ca6-afcd-553d4efb7de4','IDA','PARTNER','2026-04-04 11:16:44.265586','2046-04-04 11:16:44.265586',NULL,NULL,'SYSTEM','2026-04-04 11:17:04.728338',NULL,NULL,false,NULL,'10E2B04F5FB8F8AF21FBC669468FC6FBDBE51661F51047E2F2DB986F8A3C15BB','455279A4A62B278421B57225BFF949D0F66B0C99'),
	 ('dec0aac2-8ba9-4563-8c2f-b9130e95dc7e','IDA','IDA-FIR','2026-04-04 11:17:04.739692','2046-04-04 11:17:04.739692',NULL,NULL,'SYSTEM','2026-04-04 11:17:10.195609',NULL,NULL,false,NULL,'3C304C67F8E3E12E883013F99152B716E760B487317E78C8BF95810469E15933','A9025C61D15C479BC11E3D8B508B5DAAD68B6822'),
	 ('2ba2b695-4636-44e6-a25d-398f11b64300','IDA','INTERNAL','2026-04-04 11:17:10.205366','2046-04-04 11:17:10.205366',NULL,NULL,'SYSTEM','2026-04-04 11:17:21.255933',NULL,NULL,false,NULL,'B92C5AE21808292AF0A98FD2E3F9BCCE3AB4C59E3081C4BF367D581C18D7D9E7','956B4AA578B94050C8524F1E27C91E0BE4279AB1');
INSERT INTO ida.key_alias (id,app_id,ref_id,key_gen_dtimes,key_expire_dtimes,status_code,lang_code,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes,cert_thumbprint,uni_ident) VALUES
	 ('614a35ca-77ed-467a-a4dd-2b6b93376569','IDA','CRED_SERVICE','2026-04-08 06:01:58.009705','2046-04-08 06:01:58.009705',NULL,NULL,'SYSTEM','2026-04-08 06:01:58.179402',NULL,NULL,false,NULL,'D3FEF5A92F1903EF4787C8E55F52A6408BD67E83AF1D20B4EF1E16F49C003C3A','D1F99B26B25DFA8B6934C6F7380B356869757045'),
	 ('4245e0e1-4e72-467d-ad39-ac755bc7b9b7','IDA','mpartner-default-auth','2026-04-08 06:01:58.183335','2046-04-08 06:01:58.183335',NULL,NULL,'SYSTEM','2026-04-08 06:01:58.324768',NULL,NULL,false,NULL,'977134B29AA2BC664FDD67EE303C9758AF989756DA3A648542D80BEF2B97099A','54DABA24018FB4B32C590E8D08E93E068B047522');


-- ============================================================
-- File: key_policy_def_202604241713.sql
-- ============================================================

INSERT INTO ida.key_policy_def (app_id,key_validity_duration,is_active,pre_expire_days,access_allowed,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes) VALUES
	 ('IDA',7305,true,60,'NA','mosipadmin','2026-04-03 16:56:32.303345',NULL,NULL,false,NULL),
	 ('ROOT',9131,true,90,'NA','mosipadmin','2026-04-03 16:56:32.303345',NULL,NULL,false,NULL),
	 ('BASE',5478,true,30,'NA','mosipadmin','2026-04-03 16:56:32.303345',NULL,NULL,false,NULL),
	 ('IDA_KEY_BINDING',7305,true,60,'NA','mosipadmin','2026-04-03 16:56:32.303345',NULL,NULL,false,NULL),
	 ('IDA_KYC_EXCHANGE',7305,true,60,'NA','mosipadmin','2026-04-03 16:56:32.303345',NULL,NULL,false,NULL),
	 ('IDA_VCI_EXCHANGE',7305,true,60,'NA','mosipadmin','2026-04-03 16:56:32.303345',NULL,NULL,false,NULL);


-- ============================================================
-- File: key_store_202604241713.sql
-- ============================================================

INSERT INTO ida.key_store (id,master_key,private_key,certificate_data,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes) VALUES
	 ('2e44072b-8925-4ca6-afcd-553d4efb7de4','36682983-ffb2-4538-afa0-ae25a6e9ad7a','Lf2dACIRCP6Xis8svo2NKN7jOxqp_LeDfxlrIq0PdMsVZGrdJ8X3JSYcmn_UpbciEhDJ-xAiwcABbfnWti__cLI3PWvH2zjDyUMxv0h7BSOLyQYS603Mko0rzomxfWoaIZxos1TIKQqk-jkZJKqXtYMfLv1msSi8u0EhTfaW9OKqk2WbRYXh1gA5Gg5OFvYHyaeQBhjSggKtsNmwTeKo1kuDkMVIhXFtBKFzp90h9Kv8W3AuZ-eUAF0wnYPvJ15t4pldOWfBMq4YIeFTp17V5l-5DYwcH3iw2wIACBg5vc-tx0r9gGa5e8UMCccA4GKYWsqagPXqrsR3Iz2_AqKyqiNLRVlfU1BMSVRURVIjlYBYkIJ52CyZOGmGjTzcIcUDUdq7ZDRgh8bQaBYB8-Z3qUdQmBzo_Vhdi0AVzRqbQ-cDvxcTM6rwqCJPplxKxIyq_OWlQmHqAocuHGkRMOOKyh79eqzqFZc-SlvNoVyVSfxAVnAV3cCTDJ7KkQGyNZxNE03LWGumyHhyWT8gecYlTR9BqUyOxX6Rk0RWLgVVol4Pt2EolBDMyyjHEJ7CnOXYfXrz5882UChVvkNA3x_jIxbJAPhJ8NrEYIQVEoA_o48K_u_KJotvpixQbQ0NNfWj1-Hc9J32_IHRiAv_MqVzy9yU_6OcdPFZf6TGFfOllzR83_F0BFd4WSAbiD8fldgU-Y_uhue0BzTXCXiQWFGjEFi5JJK0nKKiBVJPoJ8PyMcOssVxY7G-I18Ou1Ex_XMksV5KhJqKIGqqer3TzXHTsC_KAM8ZtaALtV1VQHdy93UFn3GUfSEd0aX0a3c87GjbPjR2ZkVJaPqp57Se40CnCzQl4Svar1gNeTUpIiyapIVzUKlyCyQ3HcBs1GRUgWqDaUEii2JUNtSlYfXuJ34Y2LNj4eCWe4gesHQp_Pz8exXQvcTAQZpux-3L72Nwk628swXk3fODbrr7GTlwvXiIJydcsutf4p9ZzN1avdWuIh_41cVkkr5jeUBa0R9MjnWkiAveBziak5WMXbsGzF4oFu9JSezyOn0dq0Ofe5gNqeb51jwO4ueClNs6YX1K32kAD303r7dBjzRIQVbBNKBaDJvqwhtu4WSljp9GZwGRCNQHJp-n1xvm-RYxwZm4np8aKUeR2zljrq1ol3xPvxiV4x3kf1K2lWDi3FFzx33j8f0LsBDn85TjVPl0Y2LKszW3trrYHUsoHuWmTrStBzujEqoKQO7r8QF-EEXRJl6plrm8C2PetfW9_mlQauHbZ_Oc9wmcos7aVZz-ObQ_ymXxbVqiqpgVOEoeAD0D-zOXYdc-DyuzzDDmflDkzJdg7Eftr1DkcgoKJ5JMKHtdnPyARvnd2ABX9rfzGSeOQKeJR7_LBwoUg8k6SmYAyg8n1KkaTQg1iRy3mqmQ8NNes9CMOkIYo0u5fF1X6gfChylWfSI6HkUVTJr3feqalDG4dqWIFRJYWgu5qwju-ova9VORVsH3cf1t4O-942oi47dSA9_aceB1HGSwy0X8gaNc3Mf1IymQQsX9tnNftIe5T92SH4MvCGOPcbLJ3f73tJPJw_muOFyn8YaGItYrIyM6EUgc0PqNBHfP65BP7_MBH3a5xhAxFgUU6J6f-arNypikcd6JKrCOp7QgqLDxt1VJKdnhfNdDsHs-TYhQLQ4uFy3mRq-6HPZoSEIg9PWx1f8loFUUEP4Xl6oWWnVaEzLD1Me-9i7AXMqtAY4oJxKg8ELoViIMCmnlCqqfVDa5qfzEUQndRwmHXfVo08UYrXcwdcedlT5calg2GKIo8QyA9tQQSj2x7vjZcaC0kpaMYBZs8G5G1rLzeiXtmu7jElP45dKPbdCRmRthaVurHG1_vTzWYKFSyDSQqF3V925YXt7dquN0ssQgNTy9p1TvVFz9CG0EGADa-HVUSZhcclSVgSk7n4DKrvsxZW6TwMPhuIwimmn42nMVAdhFUdqtroLaTPh0t_L4rTRKdQnPpQT_x5fLbXh6ZQeh5Bw5yaMRwrD4bA','-----BEGIN CERTIFICATE-----
MIIDkDCCAnigAwIBAgII+1QLqq50j/QwDQYJKoZIhvcNAQELBQAwazELMAkGA1UE
BhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoM
BW1vc2lwMQ4wDAYDVQQLDAVJSUlUQjEbMBkGA1UEAwwSd3d3Lm1vc2lwLmlvIChJ
REEpMB4XDTI2MDQwNDA0MTY0NFoXDTQ2MDQwNDA0MTY0NFowZDELMAkGA1UEBhMC
SU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoMBW1v
c2lwMQ4wDAYDVQQLDAVJSUlUQjEUMBIGA1UEAwwLSURBLVBBUlRORVIwggEiMA0G
CSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCllgBOSbAlUgK6/gbGmh3AipswtMjc
SFL0gM6aqV4V4PZDoho/fMaYfmXTl9xr+uXk0LvamyQRgSZ0LTh4qD2s6/yHwiZl
mPYtNRyBq9lqMwUk8Yy5bXN7O/2fUM8RRZrBLNLTPOSIqVdCgRmVvLg92fSzUffs
eBli1HXC8SypvltlLnIKVV3a+3izBoefZm1M5s881fUZWavyiDQeWVoKmbFykLPg
6d/0HRhXXCWcxOArQe06/iJ2vBEZrgxgfAXuICC7pox/19qmB8kHa8qerCjI5TJr
O5dQoX545seVeWbxXwglwZE2K23nl/EwdDyHheg3MHjCGj9wk/YRfCULAgMBAAGj
PzA9MAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFEinZJbSJGH6AI2VprImwn7Gxl9E
MA4GA1UdDwEB/wQEAwIFIDANBgkqhkiG9w0BAQsFAAOCAQEAULI0qiCOiAyNzPyQ
JN+5jurIWqAT5Ds4kyzVe+GF9/cyO1t2TrxcP7wu9IlKYuZnuW4vdr0m2nlwnBp3
maq/c3aY6JHfJyy4XRMbuM9+ZKYqQrIpHgrlf59E4ib2olhHE+P4HLx6P2DJNbsz
DDICOj3tgqIS0wG2hs6ISbPALmdlUa9rHJK4AGBwMpCfre6SINBfsjR/JnVipZ+q
pQEoXBIEc/LbRBHwFnJkXfSkDwRtMYjFaub+xHsskoUrMMCmr6KPB2pdJ1kSSHCr
cKBlo+xsc4zXwjbCUPu6muoT0aOZf/HnD8b2UXGG8pBZ92YfYk0Zk+UfG7gmJr9e
KLGTyw==
-----END CERTIFICATE-----
','SYSTEM','2026-04-04 11:17:04.713259',NULL,NULL,false,NULL),
	 ('dec0aac2-8ba9-4563-8c2f-b9130e95dc7e','36682983-ffb2-4538-afa0-ae25a6e9ad7a','CGY74G_rA1cl2uaiy6-xMEKfnXi_Q4roL0oEB0Va5Xt9yJezDYjndgzuCdNhrrNK_4EHam65F1_Nq60r9t22_eAuT_dnftrR3h-9LT-pEvUmd18A7ppw6zfFkowimScZyV_o7OJRiNYgkImBQdThaU2a_v-q9pM_9N0ZUW7LOrHrRDYny1PsgdOOGHzA62k-JKzVRvNhMVBTVXtuoeJbO3Wkm2cZaGzE47IxByWEo3BISxMJ1aLw3Ud-HDYdcppgwRcL_vC6fjSXETeZVtjx0mynSZY6bvQMVHnFjZbFN1n99_DwdFX30_yqVy2B66PwbyrUppuQ7Ptn9MkEzsEJMSNLRVlfU1BMSVRURVIj4oyGOT4b8DpZOwh1ySw7w8f4sRb_b88hokNmhfth7skSCXlTkjISZx0PSoTitzDv-tjdF25NSbong-P7cLFnjK6yQuQoruVuxodpPCtJcoxCqcAbxm3jzWU03cbtDAgjBGim1Z2FA0L6DxoIZIg-ReO0TXvACvqnTbdVkpEEv281UvxykvjZFFpeHs-MdYgmPGMBFQez9H2UOBaaQCWc6H9hf9b14Igv4bmg2hQrYhSs_QbmXJpuipKJpKE_HtNJj56whAv5Wy8K9RK4taqZRqAdWj3w_KSJ4pEppkgPl2-NAb4n4NOWU1u7WH9QVAW2pMK5SEqkwjkEq-rb3sJxaJVTpTA7XeIFzp1bohscm8O64oMURsSSHbyY2h7e0coakT63yqYODkgwPP5iVBwW9o0NEHzKSC8eOVwATQd5rAskpSLRzU1Mu6WKESR8HOF-GclrwqbqdCQMguY2j4AE40EbmRzbYhU2122eYvPBek-Lyxz02dwrJ-OYOmkQW0hXIorAXrru2gFI906ptWF8pQ2QEbnDUwqb-gmTtDWscfQJiPPfNBN6qqb7TYo1BrKAl1cQXoGO3DxV-vFoOBn5u5yapRcqQ1XTO9PSQai8Vf-5TbvwAyn8K5IdlUXn8393Qj_ZBpEiAfM0O52NzkMQPJ70rL011sxrC1-4awEBcV1yQI_7KSBIy1l0H98-rKRLaw_Mx5Nm1pOvdYRjPMyeRIyW3_K6LRBPC5bWMXquifJkgNwxBfdoxNNC9INjOQRH9OjoTbbqPee-YQxnCcmpruz3PzwHCzHv8uJ5_IiRQALvztL9YKTRhJHp3Sn2l35sG7nDTVc5RBSModXVNozTipUd2T5X9h5hWpSdF4r66PwE94bIdWikJ8XggxfkRcar5fGQCpyfr-Kl1J5JesXP8X1aQpXSqPCPpVDMve074pa9F5VBuVwMQ2VxAorldFWCH4VGIOUZhlWKxwFPb0WHangbsgHoRBFb7vVLYrruTea4eHAvL0atqbCi-lqjs3av0YqOsiAiCcWsRrvTYSL8C4IUyMtoy25Ybxn0CAYjdXaUsdJgPT3a7eERpW2scYdTLxLIIJ_XPfVNwFZ4qJaaXQm5X7F4obFNGOrJVZUF3I9Ct_eIL8eUnMCsjj0c1ebyAhxl-aTZNbsYOX5TCd5egpfCQkNqbBMUmtqUDE_S-6YWdA-vtwNZaT6GyvP_aiHQ6YEQzlg3PIo-hq6yg7sVdZ0eFG-7kxwOmHA_2IrK4i9_3_oopONHdkTpAwt7IFrsOJ91bA2VMVl3B7Eg8SNb_F1NLuELyJXZXWFqzusuU3YqRW85c4sYMUODEPFMFU5FGw0IWbDj0OMxGHLVRAdfwb0e7FYYOEfzKc-wgZZLC1utOdPEyGuTTTq1LdOI3qZ2ke3TybuvmucjEmnNUEzUoXKjp1_m7TUPDSVxv6kKaCJkMXXPPu769VRpyyqy0-lweoCWzMCgVqKpkMVR97mAkOW4iiDc1Cy9MLyIQcI97WJgWOwFqgkQmskp2kv3v0X_rp6QxNvH8IH-dIV0N3H6KHcLoMX85BwRzOlnYuKaKro7tz7A4amUDzBi9MC0ss3d8_NqRdNkSTrs8Ygo0BaSNhlH5hTWsZVFe_xRw4_Q0Wvtp1TqFDcK19wCCY4D-3CC7w','-----BEGIN CERTIFICATE-----
MIIDkDCCAnigAwIBAgIIDzy+CbOOT/swDQYJKoZIhvcNAQELBQAwazELMAkGA1UE
BhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoM
BW1vc2lwMQ4wDAYDVQQLDAVJSUlUQjEbMBkGA1UEAwwSd3d3Lm1vc2lwLmlvIChJ
REEpMB4XDTI2MDQwNDA0MTcwNFoXDTQ2MDQwNDA0MTcwNFowZDELMAkGA1UEBhMC
SU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoMBW1v
c2lwMQ4wDAYDVQQLDAVJSUlUQjEUMBIGA1UEAwwLSURBLUlEQS1GSVIwggEiMA0G
CSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDjeneaPTKhC/rF84xeMmnM41fOMC/6
GTTY3NQtJHH4wuRtJm2O8IDhoNNUo/xvDhE2bVK1EAaMlkk0Bk7WWpXYu8PclAGu
Nie8sUrJSq0Zu4acAH2/OgmqDesSkSk+CGrsN44G6IB0bG9M6Do0TQsgUNHHEN3R
kdegl0jfJ1+GLBMpg2kB/MkShjbF6j7VpNg4Gi50ocwWWXdtzQ+IEgGEydPt3274
JTUH4gejB8OvKpOf0gkcvnH/jxKZWT45W66lnyzJyYzBRQ4lqvShT1gRocwHvetV
3mYgHc+TB3OsnaG4D4E0oEPvExfvH921Hj341Zt0GwGj/MHClJrk8rbJAgMBAAGj
PzA9MAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFIqAH7OFnQ/GIX8xZ+24PDHHO/HV
MA4GA1UdDwEB/wQEAwIFIDANBgkqhkiG9w0BAQsFAAOCAQEAXYIAweKK9iy6D09c
F1LZsKJ22bNmDCXFpjPcPi16Cyh+8w2pa9hFt+9bXq+j1nUYYQuaozf7fRZbO7CT
vVXn6W3T6R+3PEbVDG3cTQGPF6gQp3jdIvY3TkTPOuoc+Gu1znHM0t8+tfJGsdwu
jd9ITXQyi7tAY5gwuuG1nyJedW02Ho95q9WvIzFHPR9PgLXcggf1l/LydwsH3u5D
JUzVqtfS2IsAJhmv+A3WpcKdBkfcuWl6v4li0Ps4eiG95ItG2IceAXY2F/bPNMRB
lGH2x7jlC1uVsHlwdnzSgMSbHQ8LqP2ZTDDzF7boTZUz7uPH9AVo4XuTpGDOglZ6
tT7C6A==
-----END CERTIFICATE-----
','SYSTEM','2026-04-04 11:17:10.188549',NULL,NULL,false,NULL),
	 ('2ba2b695-4636-44e6-a25d-398f11b64300','36682983-ffb2-4538-afa0-ae25a6e9ad7a','NauSCckFLrCZChJRxmUErGiJgWClnzBmnIvYiRety03R9TBGAKLYJ58V5u05ApwtDimrgeDTRY1TFW3UU4H2_Tl6QVDFMvlHy3JuXF1rAVlcFLZGMpvr55PEjNPPGuc-SptRYhP3E033FmncB7EhOUddzRcKG3M5hXxyWIwH-ZUcjUUJy46Bjbv-GZqPU7aRmCWgofZ3_Yu54r26zHlBVw6b6jh2GkPdRAwr2JPsVlhsE42kiL6QGN97fWJgbndjSXKxM_s27DXs5ImtnnUbDCugit9Rsmzvs5e7r-CG57pszIzS8hQGdGglZcY-VUqSngX7dFKR7M_W6B-t3duNRSNLRVlfU1BMSVRURVIjSid-F1WHj4YGtBRPoSKTSp9bf3W7_o5v9JwtUwQ_7obX9wT2F1Z2oY_zRDkfAEZk199aajmFGwlaO7PlAfA-JkCHSfr7YssTh15EZPCrn6-Ngmq1eoEtI7_wo1ZVKzL-FmhzVDyRdxbuyCe7GxINjZp41eD9H6EV5hGOSEnwReWp0BWLQ5MlqMdRGxDDMHstUsT4wqIo8B88mtPWyrkwEZ9nxxA6fttzenqUKuozsIoAk4ga6nsZCvG8ZD5IH2poDjgeSGpP4dHgr4DPm7MqijabGNFRsP7rKPa0BGw811Z0UniBjAgkJjqZI6vFPLb5yInEKYwP0gyFhcRVACU05HoIWpxDtvtH7-p9B_P18wVoPuN8YffKj_N1uJppXjpRz_w4GHo7uynZEY19zYPwlMsrgZZv0HjTcLUrF8Uq5y8wrhu7asdr5GJk66hiGNWCJ-m2-kLICjiSndUqVGumEnn-w2HZd2K5wwPLINFXJWLJ8seHdBbTPNhhWyHhnjzPM7HbEdCB3QUYe-xSuVpqEzgtqNx6sEHj9TLNd2oOdQQ72Qfsg6JSVQmpOO2JYkEbNcjotjKYwbnymu5TBDabc3tVJcNkmbEI6YqrPQ405EjjDfldVGN-uGm5CbDQQiB3yxzY7OB6Oo4PwHCW04qw5niYMsrXPs7CLUqblrk5RBxRDZjij-XKU8USlroC6gOcuAX-4w3n3hni8cDLYdEFDIpKTVW3iFM4dWyAWJgIX0mOU4IKcjTobueFVEAPyKhzw3zzAkvxfBbzeI8qVvlvafZD_Kx5cGCTwB4iTN1KtdDyD8wtrOjEx_nphDQT48Ir2midj7pQY0KmEvQwuGOHUp_-jE77QhFk0kkU4as9IW30n3l30R9xpIj-l_Hef4UbxgyA9189mBGPODmw-yu4QcWBZqz73wWAyg-5l02FAZ3NUvPjjKuUmgcujTw7WcCc2x4NFojQnZ_hG5o0-lj04dlrpbTrp6aTMprShqlqTIb7g-m8-C3zTxcZY0Y-poYfHj58UmXvXaWk2vVdPqallp9M3wXNOVAkUlRrtKHf01qWCwyRj6YfwRuUsUbmnnFaKi3ySZNzMQ-BVtPimsIBYLpz5HJWkKxVEpqpf3_YLv5LeIDT76qoHA9s0tmd1AD1CGjHgWNSGl2RKWySsrolcvWsFtRYV6iXBMEpVkrjItNaMhRB1Q84HQCOeHmLp_CvB1TLf6fXIBqVqyBM4YTzYnGwBdI4Objuw8cZ_fyK6bOijHD1h0i_2R7sUQ7RmDFeeadjzxJqst2bfxQQRc4dW8DjRJThile65WNt0eobpbPrx-Wkshxqr1ZZnPc9G-AxHkGIXC9hwesEqQyFz7Iv9pUlT_Z-OBa18fZhb9pp0LL8dCFT7_RFPzKXzNB93WENB5hgb5l8OvivEM817HZmkZmqoC5F5yj2IOXQwvGuNhk_wEiWlqQcvHmfxh4n0-4DpTEHFi9RmvQwr5RV2KoczYCMHfxsL9L0L3QEYuti8bU13o_4yoEp-SRZRtL6QFZSKsmm5HsYDivuNKMYK9bwZQHitWcfny2SXlOBx9O58tclJ0Aw-vTZhd5BkH4kTDgtPAUusGnQz3tiNeZkhRs6IXnQuV89LmbFZ6ZfVVBRnYfe5tFJykM3__N9ezP74tow2ZI','-----BEGIN CERTIFICATE-----
MIIDkTCCAnmgAwIBAgIIGSmreT7DAXswDQYJKoZIhvcNAQELBQAwazELMAkGA1UE
BhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoM
BW1vc2lwMQ4wDAYDVQQLDAVJSUlUQjEbMBkGA1UEAwwSd3d3Lm1vc2lwLmlvIChJ
REEpMB4XDTI2MDQwNDA0MTcxMFoXDTQ2MDQwNDA0MTcxMFowZTELMAkGA1UEBhMC
SU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoMBW1v
c2lwMQ4wDAYDVQQLDAVJSUlUQjEVMBMGA1UEAwwMSURBLUlOVEVSTkFMMIIBIjAN
BgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvu/DN/e+p/2DT9UGTRH6a/dSey0k
twli2CoYBbf4N5LyTLPhtCSczZipVwUGliGRdAYIDPum740OMxHeqQUQk6CqPMt2
6yOLr8fqFseCMQveVjamLolbhysT+/P5FwHSYFb3RUgqK9AUycxMPajk/8s9eXd6
h1/XNy7Em5X971WYfdpB3jA32A9aILpvP2WUq/Bly0f3vJLUnypDf1g8NY4TSDfh
xXuOl+n0XolgsSO4VC8IkKcTA39yJWisEg1yteLffg5vOUdydBxFlTGbkzdzV0TC
ToeJyh0dimhzs6pPSvtCSCOGVdhpr54a+uPI9MaEVjuNXjty8fS52Xj2MQIDAQAB
oz8wPTAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBTIHUbBkd0wxnzyoVRuRRVKEuwF
ODAOBgNVHQ8BAf8EBAMCBSAwDQYJKoZIhvcNAQELBQADggEBAIDdOwYLB8UCMnFB
zMmteP+L+QSwabvqZL1EgdX9rB/3QMfGymw98f4tSxMDpxUb3vgoDoVyADGkqPZq
dKy1A/9u7PM7itdEo6jbMx78gj20J2n5NT7MUqNP+Rec7divCWayFDj0biyue6rw
8wRfyHjsaZbQKoLBtOQIrolvAB0DTebqQxLsYES+Zc0E9aW1aTr89Td93cOwfcaB
+gT2aaJsgDylbjB/arRldBnaK1e3DLBsh3DPQ/GKDvZtitjPnPfU5u8KAxveAJkP
BmLQflB1M+vclmao4WGuhe+nJhkc56W1g/Nm55E/Jjs7WIhrALitwzT+SIfJ8mxK
A7RaiVY=
-----END CERTIFICATE-----
','SYSTEM','2026-04-04 11:17:21.249758',NULL,NULL,false,NULL),
	 ('614a35ca-77ed-467a-a4dd-2b6b93376569','36682983-ffb2-4538-afa0-ae25a6e9ad7a','QPgA8ctmGOLY95BgzfLiOI7myOxUT-QOEwVE_SQlJNwazTkoK6qvkz0TMpbsew5nzvN2W1i1yNUucB5Hc0jjp43gnM-usEP_qLUFRgU1jYRlXPcUdWpC04h_zvqcBQ9aSKaZzPR0MeDDB5U0GAYyKFQYxfKwanyt7gLiFOiF6YWbaYIXRU1N2NDcBqx0bLkTCoVq4K181gH16pu4Q-45Uenhy38qVU29Q_f7n3SeINRDLv93Q0W--eL7TSLMJlfKNlwitBlpHv41ck5oqQzY-g6Os26O5V0gFKsMuxBWKvQXPx7NHcuYZcb5gfJnUA4F-xhAaM98mMjXO_YUqt1DRiNLRVlfU1BMSVRURVIjsMH-TcvGX50PZ6u84_5zHwlIlrXsrbjS_WcMDsBETXlTeU6jQbsY2ko_cA_sNmaMhks95R2QD9rT8SE7529ESsYF7Y-cYGmc-2UULeWGqK8EoQPbN0FP9W8WM0vCj8cZspkczzYG54yPK9UxWNhfpCMmlZU4mqfjZSZvDq6NXyOYnmZQeFrzbnntw6K1lcUH8UTxLcX_xeSHdSedogfbl_5lKfdVVhsRsM87j_tSEpXDdl5lYDX7z7oN6F5zdPQUNRqC3V_srC9ANEj2LFGqwLMJaEYmWVe2ts1kjXvehy70dhY4_T8fDZypLI5mEMYcB-jLJb6jYgWi83SM7W6xt5fLhvaMvagBRInltF5ZwcWbXZ1u0pI7_EH-1ubnH5gRDYArrYE85WPhvuEB0WFtoQmfQ68eEoGVQHqmGcoHdu8FGmYCZWKj7p7bItMJlkljyDZvT2YmaKHi6lZdfYjKU5KbWyN8OIgFRLy48ll93c8-L4au-HO8tF5jidDcCZg2vL-PCOYZ9acMgIQilD9I3cxhXlnc1E6vksh3TG_1wSP8L0NwE0w00DGEKy9zMvusJBan4DcwMn0VSlOY95oNBIEbIX_5CeLPLyfZllLnq25atGetT8lObk_HcCc0QMaOJzigY_7ADEF5v3ppI8pIfC1R95f8ETSZzP9nMkY9ms8aeYnkF9vX5cwx9VwrTXRD1LL7_odb6O8ijHI6K4fx_eOzEFGAZC2gpPKFOaem1j2XlxY75FlLp_Ck6Ej-RhbkUo5SXQ5ANh2qwW_VPCqnU5B9O_2YjCPzj1rvGdXLSViB0F_-0X9Fd9aA8kz5oqNiHCovCk1ZSKAKND3vpFcy7OI6KlegILrqmf-6ou3aUrMXiIAFzUcRkjAMyycf9FooCjH5YNbAXJHnp4e6Fw8kFxZEfn8Cc7Y5EMXbBDOWywmaelyoXVCBC3VY39Uwn8aWtovXW0kISx2nkYgACvRmPMvRJxY9_1_DhDA-CvNU-lUG5hhfhjrXDv40zJu7vQGF8cy5X6XELFuE_o_Sx-wb8tQ6NDDq8m8P_jDAjZaa5jNuSl9ti6avt-H9BAfmoVnix8iFWLKfoUuaMoWAw5PWJHEI6WPsFL155C-rU4G9nGEdgVI0aE26dJOC-nDJNPH2IZgIhJvPhxP0gwrfHIJ6jJmIklsS99iIFZQSIdX6ctQ96Ys4SauvFL6hYyoIZMd8_6fjzOcRq6BfqGaq6-awUCm0PenEfUiFzYtEVCK4z_-YnZ9GiSy81ki1KC-JUDTyL3tx15j7NSVYDptUWH1inCCNnVWDxKrcGL-7SicQ8UAlGhH6K2sUfv77MS0RqNjetewHpEnBI1ku3H5BySjbkpcnrYMYjwu83bF6HzMra8mm746g17T-EXvK-XqrddSkgVIcLjFhHJqUQsOUOGaLCmsWh1mwextvXgTeRC-0O46rsh9ZtTomxZKvMo2pjwP84DcF9ePagX4kaYR2bXPrpbhcvv_XvOcHPnCJGLt6xBI36zs0dEogC8kbuyDYYg3w7uBSkFTNUWU4NRJUFiLJgd8-X-B_AexHTn3NE5XDCjUWKQvLcFOJ_cfKRujW4wGROEP5Nob9f64tXAiCYVoGEiFG96Kt34m3uk1o8pI7l3ClLgFOBaZPkRFiw7U7x2KyyP4','-----BEGIN CERTIFICATE-----
MIIDlTCCAn2gAwIBAgII3QVcZ5ufUKcwDQYJKoZIhvcNAQELBQAwazELMAkGA1UE
BhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoM
BW1vc2lwMQ4wDAYDVQQLDAVJSUlUQjEbMBkGA1UEAwwSd3d3Lm1vc2lwLmlvIChJ
REEpMB4XDTI2MDQwNzIzMDE1OFoXDTQ2MDQwNzIzMDE1OFowaTELMAkGA1UEBhMC
SU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoMBW1v
c2lwMQ4wDAYDVQQLDAVJSUlUQjEZMBcGA1UEAwwQSURBLUNSRURfU0VSVklDRTCC
ASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL1YIWI0SEVO0ZLpNC7BLRxm
cIyjPU1nxCikaVc8Isl/xbIzZym36Zqefww/GbD7na1UEW2uYA9Js8OVqCIkOwST
wAEWp5y50EGYotMDRbkTGDkNVtozb1btS86qNoI+NESb/8gKP5ra7uVmLssPZ2M5
4AFtOsGCA3bC+RVryV+QGXZ99HeVr1P7qlYKWOOGbrySn5pt+IN6mfbaggWXLXQI
PI3DEbY/teGWkRs5Qg4+w04oMhGcMotMfqH7HIZNvSAziKjOMmUDwppPay4DyZO4
ZMMqsyfdkx1zPjUNvsA4xP092H6h+NoTL073vCYr/xwQkVtOvtXal5coUhqATQUC
AwEAAaM/MD0wDAYDVR0TAQH/BAIwADAdBgNVHQ4EFgQU9rweOkir4sEEsnfm5q2B
XoWr814wDgYDVR0PAQH/BAQDAgUgMA0GCSqGSIb3DQEBCwUAA4IBAQAAfWqmBwz9
LWHfts23coOny0P2C7Vb58N6C4XzGwBxPMKmaprZYA9oyJBN8VXfUg8P/nrjoMIn
+tIk8cicvFhd4Cbf4tvoEEPAKs3ClvqiDk8QcGq/1NAnBFQ+r4wSs5KV1WgCBdDD
IfptQPmTQ75Tp0EJLc80xwSmhABheqCZuZe0LaxdezBkR0p5G/6t+Wafglu5fQ6J
b0yo646vtV6wQsOJMeiXpjDNp2WMUbFANEB6Ab+acRU6JsPKiXWfR0X1Gzto5foG
ODwVoVRGMES59JqY60hjgnQ+0SZsuSOpLeF3KQssQ+4sWo8UpxRLyzpRXvFXapIc
VmQN8U2aELq8
-----END CERTIFICATE-----
','SYSTEM','2026-04-08 06:01:58.1641',NULL,NULL,false,NULL),
	 ('4245e0e1-4e72-467d-ad39-ac755bc7b9b7','36682983-ffb2-4538-afa0-ae25a6e9ad7a','I2ZCGl-sAfv5Dv1VgSLVkstNG7YaKj5cmTgZRm6_wQfUIRQwRtuwRueH9OlpZo9uwuKRC15f7eDUvFbbUrjQIHrzv1ZOF_2UHreGwgoo6ccADtWcmmNRvcDwashqTRk-ysphOMfhPQHHe9HyVRURKIxEM6vps2hhePPP5Jdam0_TcOOpfUWAUayQsZR2B11ZadXJrDmMBWm7NyR5HIhqWUSnUdkfQjdSaYdq5X8kxzcc1C9mQsPt69-7xHPb5yiXmIx28dgVXiuWktBUBPwWJrO2MDNwqi3LRVxNN53r9__pX63BI44gUSvXl3EaqOsvGfKG4Jw_7EO4vx5gLtKLvSNLRVlfU1BMSVRURVIjLVsSZuJIy9uUbfocZqTSi85wm4-XDPy5ecxbqU0YMZc2rbr84YSh5QehqqLDf0RYGzQZNQO061xbzYa77RLEO25tJ3BTzJ3NH_4tNZ9l-Kpxp5vAKluu1ybGaLEf9d12uhlFwAE4v9eZnKNrwWT_sdZ6cNexY9FOZD4YiHzt1jXVt89wshnvAoliJlXCwLAIOmNG6liFFKQDffZNDVZVDyS9wriO7XYJpafif8Ty_rizPT_7yyYWd8wHO1TMUS_LpgXeuBH9Erbf1pzudEIshjaWg9bGiLjUMT2wIhL9cRxYzhhkxBYCsvIidkFUu9o-MolhsAqDlaKud7dJwRJaJWePKC7psVXOLALLzE0CNOCeJ44W3hKKZJyScZfrOfy7XS38QlWkxVusB3s6p7_xJTMrmJQVQAAhzzQyMq0W6ZGUUB7-lOJ-Bvrr8JbbgLpt-Lx0rebhRrqvHkLp8NasOgK0_-8-cowF0p9hItdIS7KwdeYZB8MVIydPYlLN2ql6MNZRRWqss_tNuRJfdn55gb4QkRyM_6wNwtr4HtY_t4Q8UemLNkiS3BGEWqpjD-1sdXyxyrkYyQPT3AdOBu6s81bLjzxEFl5u5RSFgipZWWtTW3Kyy_mTw1Ag-aLq7LHnFZ2fxMDZDIe3dXvxyLUL6jJXUPnPbQElKCKVZn-gGUEOUoop143BqRAHqag4xL7PMUzYj_r_FCZmfZiNK6dMDeWbTZEQWw_uV16WvII2xt2hz-qFJFzM2omopcwMfpJ8KqLr5tKT1D31eq6lBRT1-NqePIN9xeQaTDVmZHjivJ2a7ybWLqP8F3yubtwcOCKhHZp-m0pDp0p6mCD68FvllMfyRbDIlHkxxgd2xWMquEY-NoBULiA_njxzuLjDgieeVmMOZNGyC6b898T7N_m2cUkjPQ6qsclaEalj3SQ26Gh5Lm4Hwszhhw4WuT-tiw20jES6-P9ssqzcLBx9ozcWK4v8SQy6zSfeVu4E8ljhnRk9ujnCUIZd0sLNi8jwYFzw-In5hIgGlTEkrqMkH2AiobrWjBy9bC_C4MbTUPhJEYjUFWrdbtM2lS1aoG82JmFTidVRp3coIcrWicoItaboV9wAkh7KcxonnLmaoGZPPaNhibJEgMOu1S8QYx3PXXoXvanx-6XHB-3_3lm2KLKH9H9d9eYe9Pw0bCCe4a8vxj2HyYyDM5yFgItFFkwnt4X_ivY68m27Zsgix98GdH-UK5WubUnCavEIXuN-cwBAt6yCT2m2NCfpmEEaGKEdlrY8VpWPzf5CCTTtJ0iACu_7l3yzBkJxj6hKU6gXpwW1BPd8ihaWphDw5ModcfYbnsaBFC_5psoko75ZGLsAVuayM3lZEHNiJk66syc7_BKDxEoBsvHPk0Jb88DOu3WmgcO9QCaIAm1-gW93MawMEyuv-nMrby8LoaLTNzCoN6Lyni33gT_NkbzTZsEFq0N-8qRnDGhKRGUp4vMpfMXvnOB-ZgBkreZChWT-HTsJO_LAyeB2CvtZcLuLNxHYyC9-WBgpa0LvHxPrVYULn2_J-Nh0Su2Yv3Ftb6YlxCvyep7_hZIJPIqihoKpwtyM0OPTpAWhAoGoZLMRY_0FzO-zZZ1Gh2ijykgZfLFAv0OZ7y9l-oMqCHwBlCzyX1FyHJuX3VMcMQ','-----BEGIN CERTIFICATE-----
MIIDnjCCAoagAwIBAgIIWEKPUctI9vwwDQYJKoZIhvcNAQELBQAwazELMAkGA1UE
BhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoM
BW1vc2lwMQ4wDAYDVQQLDAVJSUlUQjEbMBkGA1UEAwwSd3d3Lm1vc2lwLmlvIChJ
REEpMB4XDTI2MDQwNzIzMDE1OFoXDTQ2MDQwNzIzMDE1OFowcjELMAkGA1UEBhMC
SU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDjAMBgNVBAoMBW1v
c2lwMQ4wDAYDVQQLDAVJSUlUQjEiMCAGA1UEAwwZSURBLW1wYXJ0bmVyLWRlZmF1
bHQtYXV0aDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIhGmIGEFEcU
33YYqrmx1ysmYetgsuVZeqAcTUsl3AY9ipc+LCJOg5V9uPj9pbX0qJpm41zbq5lq
S11R5m61JW/xcgFPzijMeRUDbv89SsD7bsrRlywZmdvII8mVAk8+u3TvN7LI79b2
C51UkCEA0sAqdc91LuLrjRzrMKUjEmv8NWA9jhMh6qVp0kOEXb07eA53c8zglcvH
tvTSVRq6DkSOKgNzkA+qCj3IDzyf3anKKeIuazwN7M6gHfSFfqNDM4lygNzAOT2L
fa3yGNjBINXwI7/n8Za+lwyPhauEKcXpciuFA+mvG2eJin7uXwPzk7buV+74ekEj
pkpSZF0/4T0CAwEAAaM/MD0wDAYDVR0TAQH/BAIwADAdBgNVHQ4EFgQUYlMyAQ6w
IfQy7R/qJgsKJmnDMSAwDgYDVR0PAQH/BAQDAgUgMA0GCSqGSIb3DQEBCwUAA4IB
AQAuZo9dQjrEcyJn32ENinFN+dxizmeYBSdMg7orpxsVVqukqq7tWX2Eu2S3WB8O
Dk8v6B7dV72NK+5czQ8qKd31hfgNoomLhy7+cFGmdtqojOXpaaYV4d4q4FR6h0DZ
sg5SFfT6CIpubCJsRZ7M25YJHy4wxYH+ObhJPrkm0XSFjZ9lUI2yVvwL7cM+4D3B
SFietJdCvpCDMK02zE7KvRaymk2yXgwU33EFmIIFfZlLFsUhEB8e/D4SbKFx1qUu
cxyE+bI1y70/wU+lm7jDADoaMkgH0V2h+srEeDDo9ln6ugnpWI0ULLajpXJMeTa7
nL5RBk0UrwlPJUwr7dgmdvPF
-----END CERTIFICATE-----
','SYSTEM','2026-04-08 06:01:58.323085',NULL,NULL,false,NULL);


-- ============================================================
-- File: misp_license_data_202604241713.sql
-- ============================================================

INSERT INTO ida.misp_license_data (misp_id,license_key,misp_commence_on,misp_expires_on,misp_status,policy_id,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes) VALUES
	 ('MISP','vM1BA8RosNt3kemNGpsneYgcelZwNmsOoKJQgMm92DltqtJh4p','2026-04-15 19:54:24.747','2041-04-15 19:54:24.747','ACTIVE','','InfraProviderServiceImpl','2026-04-15 19:55:04.259',NULL,NULL,false,NULL);


-- ============================================================
-- File: oidc_client_data_202604241713.sql
-- ============================================================

INSERT INTO ida.oidc_client_data (oidc_client_id,oidc_client_name,oidc_client_status,user_claims,auth_context_refs,client_auth_methods,partner_id,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes) VALUES
	 ('88Vjt34c5Twz1oJ2','ODIC Service Client','ACTIVE','name,email,phone,gender,picture,individual_id,address','mosip:idp:acr:linked-wallet,mosip:idp:acr:static-code,mosip:idp:acr:biometrics,mosip:idp:acr:demographic','private_key_jwt','mpartner-default-idp-relyparty-new2','InfraProviderServiceImpl','2023-01-05 08:14:47.834','InfraProviderServiceImpl','2023-01-05 11:43:54.083',false,NULL);


-- ============================================================
-- File: partner_data_202604241713.sql
-- ============================================================

INSERT INTO ida.partner_data (partner_id,partner_name,certificate_data,partner_status,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes) VALUES
	 ('auth_v1z0816154432','AUTH_V1',decode('4C5330744C5331435255644A5469424452564A5553555A4A51304655525330744C533074436B314A5355527556454E44515739585A30463353554A425A306C4A4D6B70336133646C546B357A655468335246465A536B7476576B6C6F646D4E4F5156464654454A5251586468656B564D54554672523045785655554B516D684E51314E564E486844656B464B516D644F566B4A425A3031426133524354564A4A64305642575552575556464952454673513146564E556852565868515657745665455271515531435A303557516B467654517043567A4632597A4A73643031524E48644551566C45566C465254455242566B70545657785655577046596B3143613064424D565646515864335532517A5A444E4D62544632597A4A736430787462485A4A51326852436C52575458424E516A52595246524A4D6B31455558684F56454635546C524A654531576231684556464634545552526545315551586C4F56456C3454565A7664324A715255784E515774485154465652554A6F54554D4B5531553065454E36515570435A303557516B466E5455467264454A4E556B6C335255465A52465A52555568455157784457566331626C6C5865485A6A62565634526B524255304A6E546C5A435157394E517A46436141706A626C4A31576C684A6446517A536D354E556B31335256465A52465A5255557845515842525756684B4D474A74566E6C4D56546C5754564A4E6430565257555257555646455245467755566C59536A426962565A35436B7856546B394E53556C4353577042546B4A6E6133466F61326C484F586377516B465252555A4251553944515645345155314A53554A445A30744451564646515846615930686D4D6E46446245745259546849626A674B596A6852624746554D57397A6232397A5345564D51575177574464544D6B3156563346564D555A51637A5276516A464C5130526B61335649636E673251575671543031794E554A7A4D6B6731566D6C344D304A3056676F76623364456430637A556B6B336354564A4F5531614F573947616B3076566D35595433643562584A6B656C5A795758513361574A6D636D74305447466D64797458617A5272656C5675565752336547303056323943436D704E617A41775A48517654473530575745786345706E4B335A4B5A5868536331637A4D487075557A6C3156567069556C557664565179535731534D5556615A585A3654465255636D52424D6D5A43634564364D32454B4D586B785330393364437456534670565A4552464D6B5A574F445A7653485676656A49786432645A56456C4C57444A6852307731656E4271576E5A484E6B5935535856545955633161444A47546A49794D6A4A3555417030576C4E716347524B4D6D4D33566E524C633031614F5564786355677961584A6D616B314F646B35685531566C53464A355455746C4C7A4A6859566F7A6430564A5445356865584E77656E4E4B553264344D485268436C4D785A54677255556C4551564642516D38775358645252454651516D644F566B685354554A425A6A6846516C5242524546525343394E516A4248515446565A45526E55566443516C4A3651336C42576D64504E576B4B566C6435615756355557466E4D3163796355316F5A464A45515539435A30355753464534516B466D4F4556435155314451584652643052525755704C6231704A61485A6A546B465252557843555546455A32644651677042525845776457706E62324E4F62326C365654523254445A53557A4A34526D6C4B64303932536D315065456446566C55315A7A4E4559334E3651546C5959574D7A6330524B4E56704652326F726348417259317043436E46484E6B5675623363336445343351304E5465474E4D5344567A5755525854326478656C4A4E4D4852754E555A764D545252534846755348706A64316C484F545236536A686E556C4652597A517656466B76626E494B534338304D545A4661485659637A63326257744D4E3068545A6E706B4E4859334E6D684E556B5277636D6C6F564374365A554A324B32747855566870576D4A4D63315A4A62584176536A5930556B52524C303831546770754E446C4C5755566B6145787355303532516C686E527A4E72613052724E5646424F574A444B30703153316C75646D3576576E51785644524B4D464E49656A4A54597A4A59596A526E56307044596D466E5A474E43436D63305346425964585A51656B705A4D7A63724D3256435530467262584A4E4E3264565343397854445577536A467351554A6C4E584635536B4A434D48564D6347394F536C5644525445354B307046515531316545594B5654466D6379747254554577534570476133644A645756445255354E616D3839436930744C5330745255354549454E46556C524A526B6C44515652464C5330744C53304B','hex'),'ACTIVE','PartnerManagementServiceImpl','2026-04-20 21:37:12.191','',NULL,false,NULL);


-- ============================================================
-- File: partner_mapping_202604241713.sql
-- ============================================================

INSERT INTO ida.partner_mapping (partner_id,policy_id,api_key_id,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes) VALUES
	 ('auth_v1z0816154432','98190','198833','PartnerManagementServiceImpl','2026-04-20 21:38:31.558','',NULL,false,NULL);


-- ============================================================
-- File: policy_data_202604241713.sql
-- ============================================================

INSERT INTO ida.policy_data (policy_id,policy_data,policy_name,policy_status,policy_description,policy_commence_on,policy_expires_on,cr_by,cr_dtimes,upd_by,upd_dtimes,is_deleted,del_dtimes) VALUES
	 ('98190',decode('65794A686458526F564739725A5735556558426C496A6F696347397361574E3549697769595778736233646C5A4574355930463064484A70596E56305A584D694F6C7437496D463064484A70596E56305A553568625755694F694A6D61584A7A644535686257556966537837496D463064484A70596E56305A553568625755694F694A7359584E30546D46745A534A394C48736959585230636D6C696458526C546D46745A534936496D646C626D526C63694A394C48736959585230636D6C696458526C546D46745A534936496E4A6C63326C6B5A57356A5A564E305958523163794A394C48736959585230636D6C696458526C546D46745A534936496D5268644756505A6B4A70636E526F496E307365794A686448527961574A316447564F5957316C496A6F69634768766447386966537837496D463064484A70596E56305A553568625755694F694A6A61585A7062464A6C5A326C7A64484A35546E5674596D5679496E307365794A686448527961574A316447564F5957316C496A6F69596D6C79644768445A584A3061575A70593246305A55353162574A6C63694A394C48736959585230636D6C696458526C546D46745A534936496D5A73595764705A474E7A496E307365794A686448527961574A316447564F5957316C496A6F6962476C7A64454E7664573530636E6B6966537837496D463064484A70596E56305A553568625755694F694A776247466A5A55396D516D6C796447676966537837496D463064484A70596E56305A553568625755694F694A6D6247466E59694A394C48736959585230636D6C696458526C546D46745A534936496E426863334E7762334A30546E5674596D5679496E307365794A686448527961574A316447564F5957316C496A6F69636D566D5A584A6C626D4E6C556D567A6157526C626D4E35546E5674596D5679496E307365794A686448527961574A316447564F5957316C496A6F69636D567A5433565159584E7A496E31644C434A68624778766432566B51585630614652356347567A496A706265794A686458526F5533566956486C775A534936496B6C5353564D694C434A686458526F56486C775A534936496D4A7062794973496D3168626D526864473979655349365A6D4673633256394C4873695958563061464E31596C5235634755694F694A4753553548525649694C434A686458526F56486C775A534936496D4A7062794973496D3168626D526864473979655349365A6D4673633256394C4873695958563061464E31596C5235634755694F6949694C434A686458526F56486C775A534936496D393063434973496D3168626D526864473979655349365A6D4673633256394C4873695958563061464E31596C5235634755694F694A4751554E46496977695958563061465235634755694F694A69615738694C434A745957356B59585276636E6B694F6D5A6862484E6C66537837496D46316447685464574A556558426C496A6F69496977695958563061465235634755694F694A7664484174636D56786457567A64434973496D3168626D526864473979655349365A6D4673633256394C4873695958563061464E31596C5235634755694F6949694C434A686458526F56486C775A534936496D743559794973496D3168626D526864473979655349365A6D4673633256394C4873695958563061464E31596C5235634755694F6949694C434A686458526F56486C775A534936496D526C625738694C434A745957356B59585276636E6B694F6D5A6862484E6C66563139','hex'),'AUTHZ0816154432','ACTIVE','Z08161544321','2026-04-20 21:45:34.37','2041-04-20 21:45:56.535','PartnerManagementServiceImpl','2026-04-20 21:46:11.517','',NULL,false,NULL);


-- ============================================================
-- File: uin_hash_salt_202604241713.sql
-- ============================================================

INSERT INTO ida.uin_hash_salt (id,salt,cr_by,cr_dtimes,upd_by,upd_dtimes) VALUES
	 (32,'ZfgElmLfgR2cj/2SC6muFA==','IDA','2026-04-08 15:27:50.897772',NULL,NULL),
	 (879,'EtU30PLhOUut4WfMnEQWlw==','IDA','2026-04-08 15:27:57.114527',NULL,NULL),
	 (120,'BV49P/7F4lBSVc21ZCf2Wg==','IDA','2026-04-21 15:52:26.265722',NULL,NULL);


