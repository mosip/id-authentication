-- create table section ----------------------------------------------------------------
-- schema 		: kenel			 - Platform  kernel schema
-- table 		: otp_transaction - Table to store OTP Transaction information
-- table alias  : otptrn           - Alias for OTP Transaction
 
-- schemas section ---------------------------------------------------------------------

-- create schema if platform kernel schmea not exist
create schema if not exists kernel
;
  
-- table section -----------------------------------------------------------------------
create table kernel.otp_transaction (

	id 						character varying(64),
	
	ref_id					character varying(64),
	ref_id_type	            character varying(64),		-- master.id_type.code
	
	otp 					character varying(8),
	
	generated_dtimes 		timestamp,
	expiry_dtimes 			timestamp,
	validation_retry_count 	smallint,
	
	status_code 			character varying(64),		-- kernel.status_list.code 
	lang_code 				character varying(3),		-- master.language.code
	
	-- is_active boolean,
	cr_by character varying (32),
	cr_dtimes timestamp,
	upd_by  character varying (32),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp
	
)
;

-- keys section --------------------------------------------------------------------------
alter table kernel.otp_transaction add constraint pk_otptrn_id primary key (id)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_otptrn_otp on kernel.otp_transaction (id, otp )
-- ;

-- comments section ----------------------------------------------------------------------
comment on table kernel.otp_transaction is 'OTP Transaction table to store all otp related information and status'
;

comment on column kernel.otp_transaction.id is 'OTP id is unique value to store identification of each otp transactions'
;
