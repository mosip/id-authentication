-- create table section -------------------------------------------------------------
-- schema 		: master			- Master Reference schema
-- table 		: status_list    	- Master Reference Status List
-- table alias  : status	

-- schemas section -------------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section ----------------------------------------------------------------------
create table master.status_list (

	code character varying(36) not null ,  

	descr character varying(256) not null , 
	status_seq smallint,
	
	sttyp_code character varying(36) not null ,  -- master.status_type.code	
	lang_code character varying(3) not null ,  	-- master.language.code
	
	is_active 	boolean not null ,
	cr_by 		character varying (32) not null ,
	cr_dtimes 	timestamp not null ,
	upd_by  	character varying (32) ,
	upd_dtimes timestamp ,
	is_deleted 	boolean ,
	del_dtimes timestamp
	
)
;

-- keys section ------------------------------------------------------------------------------------------------
alter table master.status_list add constraint pk_status_code primary key (code, lang_code)
 ;


-- indexes section -----------------------------------------------------------------------------------------------
-- create index idx_status_<colX> on mastref.status_list (colX )
-- ;

-- comments section ---------------------------------------------------------------------------
comment on table master.status_list is 'Master Status List table, stores Status type codes for each Transaction / Processes'
;
comment on column master.status_list.code is 'Status codes like STARTED, INPROGRESS, COMPLETED, FAILED etc for each Transaction Type with Sequence number, with lang_cd for multi language'
;
