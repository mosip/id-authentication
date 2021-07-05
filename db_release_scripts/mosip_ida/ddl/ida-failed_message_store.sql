-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.failed_message_store
-- Purpose    	: Failed Message Store: Stores failed messages.
--           
-- Create By   	: Ram Bhatt
-- Created Date	: Jul-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------
-- 
-- ------------------------------------------------------------------------------------------


-- object: ida.failed_message_store | type: TABLE --
-- DROP TABLE IF EXISTS ida.failed_message_store CASCADE;
CREATE TABLE ida.failed_message_store
(
	id character varying (256) not null PRIMARY KEY,
	topic character varying (256) not null,
	published_on_dtimes timestamp not null,
	message character varying not null,
	status_code character varying(32) not null,
	failed_dtimes timestamp not null,
	cr_by character varying(256) not null,
	cr_dtimes timestamp not null,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp,
 	UNIQUE(id)
);
-- ddl-end --
