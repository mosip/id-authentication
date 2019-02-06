-- object: idrepo.uin_document | type: TABLE --
-- DROP TABLE IF EXISTS idrepo.uin_document CASCADE;
CREATE TABLE idrepo.uin_document(
	uin_ref_id character varying(36) NOT NULL,
	doccat_code character varying(36) NOT NULL,
	doctyp_code character varying(36),
	doc_id character varying(128),
	doc_name character varying(128) NOT NULL,
	docfmt_code character varying(36),
	doc_hash character varying(64) NOT NULL,
	lang_code character varying(3) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_uind PRIMARY KEY (uin_ref_id,doccat_code)

);
-- ddl-end --
COMMENT ON TABLE idrepo.uin_document IS 'Table to store documents that are provided during the registration process and are related to this UIN. ';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document.doc_name IS 'Name of the document';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document.doc_hash IS 'Hash value of the document';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document.lang_code IS 'Language code of the master data references like document category / type code.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document.cr_by IS 'record created by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document.cr_dtimes IS 'record created datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document.upd_by IS 'Record updated by';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document.upd_dtimes IS 'record updated datetime';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document.is_deleted IS 'Field to indicate whether the record is deleted (soft delete) or not.';
-- ddl-end --
COMMENT ON COLUMN idrepo.uin_document.del_dtimes IS 'Record deleted datetime';
-- ddl-end --
ALTER TABLE idrepo.uin_document OWNER TO sysadmin;
-- ddl-end --


-- object: uk_uind | type: CONSTRAINT --
-- ALTER TABLE idrepo.uin_document DROP CONSTRAINT IF EXISTS uk_uind CASCADE;
ALTER TABLE idrepo.uin_document ADD CONSTRAINT uk_uind UNIQUE (uin_ref_id,doc_id);
-- ddl-end --


