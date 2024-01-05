-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.credential_event_store
-- Purpose    	: 
--           
-- Create By   	: Sadanandegowda DM
-- Created Date	: Jan-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- Jan-2021		Ram Bhatt	    Set is_deleted flag to not null and default false 
-- Mar-2021		Ram Bhatt	    Reverting is_deleted not null changes
-- Sep-2021		Ram Bhatt	    Added index to cr_dtimes column
-- ------------------------------------------------------------------------------------------

-- object: ida.credential_event_store | type: TABLE --
-- DROP TABLE IF EXISTS ida.credential_event_store CASCADE;
CREATE TABLE ida.credential_event_store(
	event_id character varying(36) NOT NULL,
	event_topic character varying(256) NOT NULL,
	credential_transaction_id character varying(64) NOT NULL,
	publisher character varying(128),
	published_on_dtimes timestamp,
	event_object character varying,
	status_code character varying(36),
	retry_count smallint,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	CONSTRAINT pk_ces_id PRIMARY KEY (event_id)

);
-- ddl-end --
--index section starts----
CREATE INDEX ind_ces_id ON ida.credential_event_store (cr_dtimes);
--index section ends------
COMMENT ON TABLE ida.credential_event_store IS 'Credential Event Store: Store all credential request in IDA and their status, Retry request incase of failure';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.event_id IS 'Event ID: Event id of the credential request';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.event_topic IS 'Event Topic: Topic of the credential request where message is requested through websub';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.credential_transaction_id IS 'Credential transaction id where credential request details are stored';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.publisher IS 'Pusblisher of the messages';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.published_on_dtimes IS 'Date and time of the message published';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.event_object IS 'Credential event object details';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.status_code IS 'Status of the envent ex: NEW, STORED, FAILED, FAILED_WITH_MAX_RETRIES';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.retry_count IS 'Retry count of the credential request event incase of failure';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.cr_by IS 'Created By : ID or name of the user who create / insert record';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN ida.credential_event_store.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --

