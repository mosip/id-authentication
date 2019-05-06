-- create table section --------------------------------------------------------
-- schema 		: reg  		- Registration schema
-- table 		: rid_seq 	-Registration tables to keep the current Registration ID sequence
-- table alias  : ridseq

-- table section -------------------------------------------------------------------------------

	create table reg.rid_seq (
	
		curr_seq_no integer NOT NULL, 

		cr_by 		character varying (256) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (256),
		upd_dtimes timestamp
		
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table reg.rid_seq add constraint pk_ridseq_id primary key (curr_seq_no)
;