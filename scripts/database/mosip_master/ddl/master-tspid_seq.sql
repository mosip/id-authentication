-- create table section --------------------------------------------------------
-- schema 		: master  			- IDA schema
-- table 		: tspid_seq 	- IDA tables to keep the Service Provider ID sequence
-- table alias  : tspidseq

-- schemas section ---------------------------------------------------------------

-- create schema if master schema not exists
create schema if not exists master
;
 
-- table section -------------------------------------------------------------------------------

	create table master.tspid_seq (
	
		curr_seq_no integer NOT NULL, 

		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp
		
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table master.tspid_seq add constraint pk_tspidseq_id primary key (curr_seq_no)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_tspidseq_<col> on master.tspid_seq (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table master.tspid_seq is 'Master tables to keep the Service Provider ID sequence'
;

