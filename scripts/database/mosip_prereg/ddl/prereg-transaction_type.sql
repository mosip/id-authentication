-- object: prereg.transaction_type | type: TABLE --
-- DROP TABLE IF EXISTS prereg.transaction_type CASCADE;
CREATE TABLE prereg.transaction_type(
	code character varying(36) NOT NULL,
	descr character varying(256) NOT NULL,
	lang_code character varying(3) NOT NULL,
	is_active boolean NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT ttype_pk PRIMARY KEY (code,lang_code)

);
-- ddl-end --
COMMENT ON TABLE prereg.transaction_type IS 'Transaction type table to store various transactions that are processd within pre-registration module/application.';
-- ddl-end --
COMMENT ON COLUMN prereg.transaction_type.code IS 'Code of the transaction types available in pre-registration module/application';
-- ddl-end --
COMMENT ON COLUMN prereg.transaction_type.descr IS 'Description of the transaction types of pre-registration module/application';
-- ddl-end --
COMMENT ON COLUMN prereg.transaction_type.is_active IS 'Active status of a transaction type';
-- ddl-end --
ALTER TABLE prereg.transaction_type OWNER TO appadmin;
-- ddl-end --

