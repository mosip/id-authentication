-- object: reg.audit_log_control | type: TABLE --
-- DROP TABLE IF EXISTS reg.audit_log_control CASCADE;
CREATE TABLE reg.audit_log_control(
	reg_id character varying(39) NOT NULL,
	audit_log_from_dtimes timestamp NOT NULL,
	audit_log_to_dtimes timestamp NOT NULL,
	audit_log_sync_dtimes timestamp,
	audit_log_purge_dtimes timestamp,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	CONSTRAINT pk_algc PRIMARY KEY (reg_id)

);


