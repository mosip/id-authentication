-- create table section -----------------------------------------------------------
-- schema 		: ida	- Id-Authentication 
-- table 		: auth_transaction - Authentication request details
-- table alias  : authtrn

-- schemas section -----------------------------------------------------------------

-- create schema if ida schema for Id-Authentication schema not exists
create schema if not exists ida
;

-- table section --------------------------------------------------------------------
create table ida.auth_transaction (

	id character varying(36) not null,		

	request_dtimes timestamp not null,
	response_dtimes timestamp not null,
	request_trn_id character varying(64),

	auth_type_code character varying(36) not null,     -- master.authentication_type.code
	status_code character varying(36) not null,		   -- ida.status_list.code 
	status_comment character varying(1024),
		lang_code character varying(3) not null, 		-- master.language.code
												-- authtyp_code, status_code and ref_id_type must be from same lang_code.

	ref_id_type character varying(36),				-- master.id_type.code
	ref_id	 character varying(64),					
	
	static_tkn_id  character varying(64),
	
	-- is_active 	boolean not null,
	
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp
	
)
;

-- keys section ---------------------------------------------------------------------------
alter table ida.auth_transaction add constraint pk_authtrn_id primary key (id)
 ;


-- indexes section -----------------------------------------------------------------------
-- create index idx_authtrn_<colX> on ida.auth_transaction(colX)
-- ;

-- comments section ---------------------------------------------------------------------- 
comment on table ida.auth_transaction is 'auth_transaction table is used to store all authentication request transaction details'
;


