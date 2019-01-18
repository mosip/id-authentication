-- create table section --------------------------------------------------------
-- schema 		: kernel  	- Kernel Schema
-- table 		: uin 		- Kernel tables to keep all pre generated UIN numbers
-- table alias  : uin

-- schemas section ---------------------------------------------------------------

-- create schema if kernel schema not exists
create schema if not exists kernel
;
 
-- table section -------------------------------------------------------------------------------

	create table kernel.uin (
	
		uin 		character varying (28) not null,
		is_used 	boolean ,
		
		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes 	timestamp ,
		is_deleted 	boolean,
		del_dtimes	timestamp 
		
	)
;

-- keys section -------------------------------------------------------------------------------
alter table kernel.uin add constraint pk_uin_id primary key (uin)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_uin_<col> on kernel.uin (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table kernel.uin is 'Kernel tables to keep all pre generated UIN numbers'
;

