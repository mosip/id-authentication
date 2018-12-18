-- create table section --------------------------------------------------------
-- schema 		: kernel			- Kernel schema
-- table 		: key_alias  		- Key manager alias mapping table
-- table alias  : keyals

-- schemas section ---------------------------------------------------------------

-- create schema if kernel schema not exists
create schema if not exists kernel
;

-- table section -------------------------------------------------------------------
create table kernel.key_alias (

	id 			character varying(36) not null,   
	app_id 		character varying(36) not null,		-- master.app_detail , Application id mapped to Application detail table in master databse	
	ref_id		character varying(64) , 			-- Ref id is received from key requester, This can be machine id, TPS id...etc.
	key_gen_dtimes		timestamp,
	key_expire_dtimes 	timestamp,
	status_code    		character varying(36),		-- master.status_list.code
	
	lang_code 	character varying(3),				-- master.language.code	
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes 	timestamp,
	is_deleted	boolean,
	del_dtimes 	timestamp
	
)
;

-- keys section -------------------------------------------------------------------------------
alter table kernel.key_alias add constraint pk_keymals_id primary key (id)
 ;
 
-- indexes section -------------------------------------------------
create unique index idx_keymals_id on kernel.key_alias (app_id, ref_id, key_gen_dtimes) 
;

-- indexes section -----------------------------------------------------------------------
-- create index idx_keymals_<colX> on kernel.key_alias (<colX>)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table kernel.key_alias is 'Key Manange alias mapping tables is to store key aliases to map and get key from Key Store device like HSM'
;

comment on column kernel.key_alias.id is 'Alias ID is a unique identifier (UUID) used for storing keys in SoftHSM keystore and mapped in the table'
;
