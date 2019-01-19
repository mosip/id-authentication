-- create table section --------------------------------------------------------
-- schema 		: master  - Master Reference schema
-- table 		: mid_seq - Master tables to keep the current Machin ID sequence
-- table alias  : midseq	

-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.mid_seq (
	
		curr_seq_no integer NOT NULL, 

		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp 
		
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table master.mid_seq add constraint pk_midseq_id primary key (curr_seq_no)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_midseq_<col> on master.mid_seq (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.mid_seq is 'Master tables to keep the current Machin ID sequence'
;

