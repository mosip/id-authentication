-- create table section --------------------------------------------------------
-- schema 		: master  	- Master Reference schema
-- table 		: rid_seq 	- Master tables to keep the current Registration ID sequence
-- table alias  : ridseq

-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.rid_seq (
	
		curr_seq_no integer NOT NULL, 

		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp
		
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table master.rid_seq add constraint pk_ridseq_id primary key (curr_seq_no)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_ridseq_<col> on master.rid_seq (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.rid_seq is 'Master tables to keep the current Registration ID sequence'
;

