-- create table section -------------------------------------------------
-- schema 		: prereg						- Pre Registration
-- table 		: language_transliteration 		- Mapping table of language for transliteration
-- table alias  : ltrnln			

-- schemas section -------------------------------------------------

-- create schema if prereg schema for Pre Registration is not exists
create schema if not exists prereg
;

-- table section -------------------------------------------------
create table prereg.language_transliteration (

	lang_from_code	character varying(3) not null,	 -- master.language.code
	lang_to_code 	character varying(3) not null,	 -- master.language.code
	
	lang_id 		character varying(30) not null,

	cr_by 		character varying (32) not null,      
	cr_dtimes 	timestamp not null,
	upd_by 		character varying (32),
	upd_dtimes 	timestamp
)
;

-- keys section -------------------------------------------------
alter table prereg.language_transliteration add constraint pk_ltrnln primary key (lang_from_code, lang_to_code)
;

-- comments section ------------------------------------------------- 
comment on table prereg.language_transliteration is 'Mapping table of language for transliteration'
;

