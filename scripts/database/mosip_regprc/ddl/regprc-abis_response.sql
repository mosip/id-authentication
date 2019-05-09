-- NOTE: the code below contains the SQL for the selected object
-- as well for its dependencies and children (if applicable).
-- 
-- This feature is only a convinience in order to permit you to test
-- the whole object's SQL definition at once.
-- 
-- When exporting or generating the SQL for the whole database model
-- all objects will be placed at their original positions.


-- object: regprc.abis_response | type: TABLE --
-- DROP TABLE IF EXISTS regprc.abis_response CASCADE;
CREATE TABLE regprc.abis_response(
	id character varying(36) NOT NULL,
	abis_req_id character varying(36),
	resp_dtimes timestamp NOT NULL,
	resp_text bytea,
	status_code character varying(32) NOT NULL,
	status_comment character varying(256),
	lang_code character varying(3) NOT NULL,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_abisresp PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON TABLE regprc.abis_response IS 'ABIS Response: Stores all the responses that were received from ABIS systems for the request sent.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.id IS 'Response Id: Id of the response received from ABIS application. This is a system generated unique number, can be UUID. This will be used in reference tables';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.resp_dtimes IS 'Response Date Time: Data and Time when the response was received.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.resp_text IS 'Response Text: Text of the response that was received from the ABIS application.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.status_code IS 'Status Code:  Current Status code of the ABIS reponse transaction. Refers to master.status_list.code';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.status_comment IS 'Status Comment: Comments captured as part of packet processing (if any). This can be used in case someone wants to abort the transaction, comments can be provided as additional information.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.lang_code IS 'Language Code: Code of the language used while this ABIS response transaction was created.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.cr_by IS 'Created By : ID or name of the user who create / insert record.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN regprc.abis_response.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --
ALTER TABLE regprc.abis_response OWNER TO sysadmin;
-- ddl-end --

-- object: uk_abisresp | type: CONSTRAINT --
-- ALTER TABLE regprc.abis_response DROP CONSTRAINT IF EXISTS uk_abisresp CASCADE;
ALTER TABLE regprc.abis_response ADD CONSTRAINT uk_abisresp UNIQUE (abis_req_id,resp_dtimes);
-- ddl-end --


