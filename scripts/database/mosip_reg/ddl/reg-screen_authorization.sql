-- create table section --------------------------------------------------------
-- schema 		: reg  					- Registration schema
-- table 		: screen_authorization  - Screen authorization for role level
-- table alias  : scrauth	

-- table section -------------------------------------------------------------------------------

create table reg.screen_authorization (
				
		screen_id character varying (36) not null,		 -- master.screen_detail.id
			-- app_id character varying (36) not null,   -- this FK mapped in master.screen_detail table, so not required here.
			
		role_code 	  character varying (36) not null,	 -- master.role_list.code
			lang_code character varying (3) not null,	 -- master.language.code
		
		is_permitted boolean not null,

		is_active 	boolean not null,
		cr_by 		character varying (256) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (256),
		upd_dtimes  timestamp,
		is_deleted 	boolean,
		del_dtimes	timestamp

	)
;

-- keys section -------------------------------------------------------------------------------
alter table reg.screen_authorization add constraint pk_scrauth_screen_id primary key (screen_id, role_code)
 ;

