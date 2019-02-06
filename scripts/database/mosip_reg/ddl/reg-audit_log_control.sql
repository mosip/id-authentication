-- object: reg.audit_log_control | type: TABLE --
-- DROP TABLE IF EXISTS reg.audit_log_control CASCADE;
CREATE TABLE reg.audit_log_control(
	reg_id character varying(39) NOT NULL,
	audit_log_from_dtimes timestamp NOT NULL,
	audit_log_to_dtimes timestamp NOT NULL,
	audit_log_sync_dtimes timestamp,
	audit_log_purge_dtimes timestamp,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	CONSTRAINT pk_algc PRIMARY KEY (reg_id)

);
-- ddl-end --
COMMENT ON TABLE reg.audit_log_control IS 'As part of the registration packet creation, the audit log information pending available in the system will also be enclosed within the packet. Once the packet is processed and a UIN is generated, the audit log information has to be purged. This table will store the needed details to control the audit log generation and purging.';
-- ddl-end --
COMMENT ON COLUMN reg.audit_log_control.audit_log_from_dtimes IS 'Start date of the audit log information that was considered to be included in the packet to be synched with the MOSIP server.';
-- ddl-end --
COMMENT ON COLUMN reg.audit_log_control.audit_log_to_dtimes IS 'Till date of the audit log information that was considered to be included in the packet to be synched with the MOSIP server. ';
-- ddl-end --
COMMENT ON COLUMN reg.audit_log_control.audit_log_sync_dtimes IS 'Date time on which the audit log information was synched with the MOSIP server. ';
-- ddl-end --
COMMENT ON COLUMN reg.audit_log_control.audit_log_purge_dtimes IS 'Date time on which the log information was purged from the audit log table ';
-- ddl-end --
COMMENT ON COLUMN reg.audit_log_control.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN reg.audit_log_control.cr_dtimes IS 'Record created datetime';
-- ddl-end --
COMMENT ON COLUMN reg.audit_log_control.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN reg.audit_log_control.upd_dtimes IS 'Record updated datetime';
-- ddl-end --

