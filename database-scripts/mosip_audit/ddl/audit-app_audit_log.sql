-- create table section -------------------------------------------------
-- schema 		: audit				- Audit database schema 
-- table 		: app_audit_log		- Audit log
-- table alias  : audlog

-- schemas section -------------------------------------------------

-- create schema if audit schema not exists
create schema if not exists audit
;

-- table section -------------------------------------------------
create table audit.app_audit_log (
	log_id 			  character varying(64) not null ,
	log_dtimes       timestamp not null,
	log_desc          character varying(2048) ,
	event_id          character varying(64) not null ,
	event_type        character varying(64) not null ,
	event_name        character varying(128) not null ,
	action_dtimes    timestamp not null ,
	host_name         character varying(32) not null ,
	host_ip           character varying(16) not null ,
	session_user_id   character varying(64) not null ,
	session_user_name character varying(128) ,
	app_id            character varying(64) not null ,
	app_name          character varying(128) not null ,
	module_id         character varying(64) ,
	module_name       character varying(128) ,
	ref_id            character varying(64) ,
	ref_id_type       character varying(64) ,
	cr_by             character varying(32) not null
)
;

-- keys section -------------------------------------------------
 alter table audit.app_audit_log add constraint pk_audlog_log_id primary key (log_id)
 ;
-- 

-- indexes section -------------------------------------------------
-- create index idx_audlog_<colX> on audit.app_audit_log (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table audit.app_audit_log is 'Application Audit Log table is to track application related audit details for analysing, auditing and reporting purposes'
;

--comment on column audit.app_audit_log.<columnname> is 'comment on a column'
--;
