-- object: idrepo.uin_document_h | type: TABLE --
-- DROP TABLE IF EXISTS idrepo.uin_document_h CASCADE;
CREATE TABLE idrepo.uin_document_h(
	uin_ref_id character varying(36) NOT NULL,
	doccat_code character varying(36) NOT NULL,
	doctyp_code character varying(36) NOT NULL,
	eff_dtimes timestamp NOT NULL,
	doc_id character varying(128) NOT NULL,
	doc_name character varying(128) NOT NULL,
	docfmt_code character varying(36) NOT NULL,
	doc_hash character varying(64) NOT NULL,
	lang_code character varying(3) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_uindh PRIMARY KEY (uin_ref_id,doccat_code,doctyp_code,eff_dtimes),
	CONSTRAINT uk_uindh UNIQUE (uin_ref_id,doc_id,eff_dtimes)

);
-- ddl-end --
COMMENT ON TABLE idrepo.uin_document_h IS 'Table to store documents that are provided during the registration process and are related to this UIN. ';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.uin_ref_id IS 'reference id of an uin, refers to uin_ref_id of idrep.uin table';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.doc_name IS 'Name of the document';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.doc_hash IS 'Hash value of the document';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.lang_code IS 'Language code of the master data references like document category / type code.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document_h.del_dtimes IS 'Record deleted datetime';
-- ddl-end --
ALTER TABLE idrepo.uin_document_h OWNER TO appadmin;
-- ddl-end --

