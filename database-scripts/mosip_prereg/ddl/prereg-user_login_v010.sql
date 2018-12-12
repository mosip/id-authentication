-- create table section -------------------------------------------------
-- schema 		: prereg			- Pre Registration
-- table 		: user_login		- Pre Registration Login User details
-- table alias  : usrlgn			

-- schemas section -------------------------------------------------

-- create schema if prereg schema for Pre Registration is not exists
create schema if not exists prereg
;

-- table section -------------------------------------------------
create table prereg.user_login (

	login_id character varying(64),

	mobile 	character varying(16),	
	email 	character varying(64),

    last_login_dtimes timestamp,
	is_active 	boolean,
	cr_dtimes 	timestamp,
	upd_dtimes 	timestamp
	
)
;

-- keys section -------------------------------------------------
alter table prereg.user_login add constraint pk_usrlgn_login_id primary key (login_id)
;

-- indexes section -------------------------------------------------

create index  idx_usrlgn_mobile on prereg.user_login (mobile)
;
create index  idx_usrlgn_email on prereg.user_login (email)
;

-- comments section ------------------------------------------------- 
comment on table prereg.user_login is 'user_login table stores registered individual email and/or mobile number'
;
comment on column prereg.user_login.login_id is 'Value of the login_id is either mobile or email.  User have the option to select the login id preference or update email or mobile, in this case login_id also need to be updated'
;
