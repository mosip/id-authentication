-- object: prereg.pre_registration_transaction | type: TABLE --
-- DROP TABLE IF EXISTS prereg.pre_registration_transaction CASCADE;
CREATE TABLE prereg.pre_registration_transaction(
	id character varying(36) NOT NULL,
	trn_type_code character varying(36) NOT NULL,
	parent_prereg_trn_id character varying(36),
	status_code character varying(36) NOT NULL,
	status_comments character varying(1024),
	lang_code character varying(3) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT preg_trn_pk PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON TABLE prereg.pre_registration_transaction IS 'Transaction table to store various transactions that are processd within pre-registration module/application.';
-- ddl-end --
COMMENT ON COLUMN prereg.pre_registration_transaction.id IS 'Transaction id of the transactions that were recorded in pre-registration module/application';
-- ddl-end --
COMMENT ON COLUMN prereg.pre_registration_transaction.trn_type_code IS 'Transaction type code of the transaction being processed.';
-- ddl-end --
COMMENT ON COLUMN prereg.pre_registration_transaction.status_code IS 'Current status of the transaction. Refers to code field of master.status_list table.';
-- ddl-end --
COMMENT ON COLUMN prereg.pre_registration_transaction.status_comments IS 'Comments provided by the actor during the transaction processing.';
-- ddl-end --
COMMENT ON COLUMN prereg.pre_registration_transaction.lang_code IS 'Language code in which status and other items are stored';
-- ddl-end --
ALTER TABLE prereg.pre_registration_transaction OWNER TO appadmin;
-- ddl-end --

