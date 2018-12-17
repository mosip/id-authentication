-- create table section -------------------------------------------------------------
-- schema 		: master	- Master Reference schema
-- table 		: message_list	- Master Reference Message Codes&Description Lists, for ERRORS, WARNINGS etc
-- table alias  : msglst

-- schemas section -----------------------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section ---------------------------------------------------------------------------------
create table master.message_list(

	code character varying(36) not null,    
	
	message character varying(256) not null,  
	descr character varying(256),
	
	msg_type character varying(64),				-- ERROR, FAILURE, EXCEPTION etc
	msg_grp character varying(64),				-- OTP, ISSUANCE, VALIDATION , SYSTEM, etc

	lang_code character varying(3) not null,	-- master.language.code
	
	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp
)
;

-- keys section ------------------------------------------------------------------------------
alter table master.message_list add constraint pk_msglst_code primary key (code, lang_code)
;
-- 

-- indexes section ----------------------------------------------------------------------------
-- create index idx_msglst_<colX> on mastref.message_list (colX )
-- ;

-- comments section --------------------------------------------------------------------------- 
comment on table master.message_list is 'Master Message List table to store all messages related to ERROR, FAILURE, ECEPTION...etc.'
;
comment on column master.message_list.code is 'Primary key, with lang_cd for error messages'
;
comment on column master.message_list.msg_type is 'ERROR, FAILURE, EXCEPTION etc'
;
comment on column master.message_list.msg_grp is 'OTP, ISSUANCE, VALIDATION , SYSTEM, etc'
;

