-- create table section -------------------------------------------------
-- schema 		: regprc		- Registration processor(enrolment server or ID issuance server)
-- table 		: diskspace_log	- Diskspace log
-- table alias  : dsksp		

-- schemas section -------------------------------------------------
 
-- create schema if master schema for registration processor schema not exists
create schema if not exists regprc
;

-- table section -------------------------------------------------
create table regprc.diskspace_log (
	log_dtimes timestamp not null,
	host_ip character varying(17) not null,
	host_name character varying(64),
	folder_path character varying(256) not null,
	total_mb numeric(12,4) not null,
	used_mb numeric(12,4) not null,
	free_mb numeric(12,4) ,
	free_pct numeric(5,2) ,
	cr_by character varying (32) not null,
	cr_dtimes timestamp  not null
)
;

-- keys section -------------------------------------------------
-- alter table regprc.diskspace_log add constraint pk_dsksp_<keycolumn> primary key (keycolumn)
-- ;
-- 

-- indexes section -------------------------------------------------
-- create index idx_dsksp_<colX> on regprc.diskspace_log (colX )
-- ;

-- comments section ------------------------------------------------- 
comment on table regprc.diskspace_log is 'Diskspace Log table is to track idsvr server diskspace details for audit and reporting purposes '
;

--comment on column regprc.diskspace_log.<columnname> is 'comment on a column'
--;
