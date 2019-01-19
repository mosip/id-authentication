-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: gender  - Master Gender list
-- table alias  : gndr	

-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
  
-- table section -------------------------------------------------------------------------------

create table master.gender (

	code 		character varying(16) not null,     

	name 		character varying(64) not null,
	

	lang_code  	character varying(3) not null,		-- master.language.code

	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes 	timestamp  not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp ,
	is_deleted 	boolean,
	del_dtimes	timestamp 
	
)
;

-- keys section -------------------------------------------------------------------------------
alter table master.gender add constraint pk_gndr_code primary key (code, lang_code)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_gndr_<col> on master.gender (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.gender is 'Master Gender table'
;

