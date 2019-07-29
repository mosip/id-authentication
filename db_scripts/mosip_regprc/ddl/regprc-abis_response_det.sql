-- NOTE: the code below contains the SQL for the selected object
-- as well for its dependencies and children (if applicable).
-- 
-- This feature is only a convinience in order to permit you to test
-- the whole object's SQL definition at once.
-- 
-- When exporting or generating the SQL for the whole database model
-- all objects will be placed at their original positions.


-- object: regprc.abis_response_det | type: TABLE --
-- DROP TABLE IF EXISTS regprc.abis_response_det CASCADE;
CREATE TABLE regprc.abis_response_det(
	abis_resp_id character varying(36) NOT NULL,
	matched_bio_ref_id character varying(36) NOT NULL,
	score numeric(6,3) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_abisrdt PRIMARY KEY (matched_bio_ref_id,abis_resp_id)

);
-- ddl-end --
COMMENT ON TABLE regprc.abis_response_det IS 'ABIS Response Detail: Stores details of all the ABIS responses received from ABIS system. Response details will mainly have scores, which is applicable only for identify request type.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response_det.matched_bio_ref_id IS 'Matched BIO Reference ID: Bio Reference IDs that are potential matches with the host reference id as rececived by an ABIS application.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response_det.cr_by IS 'Created By : ID or name of the user who create / insert record.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response_det.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response_det.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response_det.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response_det.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response_det.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --
ALTER TABLE regprc.abis_response_det OWNER TO sysadmin;
-- ddl-end --

