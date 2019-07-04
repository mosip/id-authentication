-- create table section -------------------------------------------------
-- schema 		: audit				- Audit database schema 
-- table 		: app_audit_log		- Audit log
-- table alias  : adtl

-- schemas section -------------------------------------------------

-- create schema if master schema for Identity Issuance tables is not exists
create schema if not exists audit
;

-- table section -------------------------------------------------
create table if not exists audit.audit_log_app (
	log_id 			  character varying(64) not null ,
	log_dtimesz       timestamp with time zone not null,
	log_desc          character varying(2048) ,
	event_id          character varying(64) not null ,
	event_name        character varying(128) not null ,
	event_type        character varying(64) not null ,
	action_dtimesz    timestamp with time zone not null ,
	host_name         character varying(32) not null ,
	host_ip           character varying(16) not null ,
	session_user_id   character varying(64) not null ,
	session_user_name character varying(128) ,
	app_id            character varying(64) not null ,
	app_name          character varying(128) not null ,
	module_id         character varying(64) ,
	module_name       character varying(128) ,
	ref_id            character varying(64) not null ,
	ref_id_type       character varying(64) not null ,
	cr_by             character varying(24) not null
)
;

-- keys section -------------------------------------------------
-- ALTER TABLE audit.audit_log_app DROP CONSTRAINT IF EXISTS pk_adtl_log_id;
-- alter table audit.audit_log_app add constraint pk_adtl_log_id primary key (log_id);
-- 

-- indexes section -------------------------------------------------
-- create index idx_adtl_<colX> on audit.audit_log_app (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table audit.audit_log_app is 'Application Audit Log table is to track application related audit details for analysing, auditing and reporting purposes'
;

--comment on column audit.app_audit_log.<columnname> is 'comment on a column'
--;
