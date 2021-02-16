-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.hotlisting_table
-- Purpose    	: Hotlisting Table : To store the list of blocked ids.
--           
-- Created By   : Ram Bhatt
-- Created Date	: Feb-2021
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------

-- object: ida.hotlisting_table | type: TABLE --
-- DROP TABLE IF EXISTS ida.hotlisting_table CASCADE;
CREATE TABLE ida.hotlisting_table (
	id_hash character varying(128) NOT NULL,
	id_type character varying(128) NOT NULL,
	status character varying(64),
	start_timestamp timestamp,
	end_timestamp timestamp,
	CONSTRAINT "pk_idHashidType" PRIMARY KEY (id_hash,id_type)

);
-- ddl-end --
COMMENT ON TABLE ida.hotlisting_table IS E'UIN Authentication Lock: An individual is provided an option to lock or unlock any of the authentication types that are provided by the system. When an individual locks a particular type of authentication, any requests received by the system will be rejected. The details of the locked authentication types are stored in this table.';
-- ddl-end --
COMMENT ON COLUMN ida.hotlisting_table.id_hash IS E'Vanilla hash of IdValue';
-- ddl-end --
COMMENT ON COLUMN ida.hotlisting_table.id_type IS E'ID Type';
-- ddl-end --
COMMENT ON COLUMN ida.hotlisting_table.status IS E'Status : Blocked/Unblocked';
-- ddl-end --
COMMENT ON COLUMN ida.hotlisting_table.start_timestamp IS E'Start Timestamp';
-- ddl-end --
COMMENT ON COLUMN ida.hotlisting_table.end_timestamp IS E'Lock End Datetime: End date and time when the UIN Authentication lock was released.';
-- ddl-end --
ALTER TABLE ida.hotlisting_table OWNER TO sysadmin;
-- ddl-end --



