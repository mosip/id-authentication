-- create table section --------------------------------------------------------
-- schema 		: kernel			- Kernel schema
-- table 		: key_store  		- Key store table
-- table alias  : keystr

-- schemas section ---------------------------------------------------------------

-- create schema if kernel schema not exists
create schema if not exists kernel
;

-- table section -------------------------------------------------------------------
create table kernel.key_store (

	id 			character varying(36) not null,   
    master_key 	character varying(36) not null,
    private_key	bytea not null,
    public_key	bytea not null,

	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes 	timestamp,
	is_deleted	boolean,
	del_dtimes 	timestamp
	
)
;

-- keys section -------------------------------------------------------------------------------
alter table kernel.key_store add constraint pk_keystr_id primary key (id)
 ;
 

-- indexes section -----------------------------------------------------------------------
-- create index idx_keymals_<colX> on kernel.key_store (<colX>)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table kernel.key_store is 'Key Manange store table which will store all the private and public keys for MOSIP applications'
;

comment on column kernel.key_store.id is 'Alias ID is a unique identifier (UUID) used for storing keys and mapping'
;
