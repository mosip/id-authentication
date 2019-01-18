-- create table section --------------------------------------------------------
-- schema 		: master  	- Master Reference schema
-- table 		: rcid_seq 	- Master tables to keep the current Registration center ID sequence
-- table alias  : rcidseq	

-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.rcid_seq (
	
		curr_seq_no integer NOT NULL, 

		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table master.rcid_seq add constraint pk_rcidseq_id primary key (curr_seq_no)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_rcidseq_<col> on master.rcid_seq (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.rcid_seq is 'Master tables to keep the current Registration center ID sequence'
;

