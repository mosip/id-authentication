-- create table section -------------------------------------------------
-- schema 		: reg	- Registration (instead of enrolment client or ID issuance client)
-- table 		: registration_transaction	- Registration / Enrolment Packet and Transactions.
-- table alias  : regtrn

-- table section -------------------------------------------------
create table reg.registration_transaction (

	id 				character varying(36) not null,
	
	reg_id 		    character varying(39) not null,		-- reg.registration.id
	
	trn_type_code 	character varying(36) not null,		-- reg.transaction_type.code
	remarks 		character varying(1024), 
	
	parent_regtrn_id 	character varying(36),			-- optional,  self join.



	
	status_code 	character varying(36) not null,		-- reg.status_list.code
	lang_code 		character varying(3) not null,		-- master.language.code
	status_comment 	character varying(1024),
	
	-- is_active 	boolean not null,
	cr_by 		character varying (256) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes	timestamp,
	is_deleted 	boolean,
	del_dtimes 	timestamp
	
)
;

-- keys section -------------------------------------------------
 alter table reg.registration_transaction add constraint pk_regtrn_id primary key (id)
 ;
