-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_ida
-- Table Name 	: ida.identity_cache
-- Purpose    	: Identity Cache: Details of UIN stored along with uin data and biometric details, This data is synched from ID Repo whenever it is needed and used for authentication request during validation and response to authentication
--           
-- Create By   	: Sadanandegowda DM
-- Created Date	: 19-May-2020
-- 
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------
-- object: ida.identity_cache | type: TABLE --
-- DROP TABLE IF EXISTS ida.identity_cache CASCADE;
CREATE TABLE ida.identity_cache(
	id character varying(256) NOT NULL,
	token_id character varying(128) NOT NULL,
	demo_data bytea NOT NULL,
	bio_data bytea NOT NULL,
	expiry_timestamp timestamp,
	transaction_limit smallint,
	cr_by character varying(256) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	upd_by character varying(256),
	upd_dtimes timestamp,
	is_deleted boolean DEFAULT FALSE,
	del_dtimes timestamp,
	identity_expiry timestamp,
	CONSTRAINT pk_idcache_id PRIMARY KEY (id)

);
-- ddl-end --
--index section starts----
CREATE INDEX ind_id ON ida.identity_cache (id);
--index section ends------

COMMENT ON TABLE ida.identity_cache IS 'Identity Cache: Details of UIN stored along with uin data and biometric details, This data is synched from ID Repo whenever it is needed and used for authentication request during validation and response to authentication.';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.id IS 'ID: ID of an identity cache, This can be UIN or VID of an individuals for whom the authentication request is beeing made. Hash value is stored.';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.token_id IS 'Token ID : Token ID generated in reference to UIN/VID';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.demo_data IS 'Demo Data: Demographic data of an individuals which is cached to use during authentication request.';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.bio_data IS 'Biometric Data: Biometric data of an individuals is stored and used during authentication request.';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.expiry_timestamp IS 'Expiry Date and Time: Expiry date and time of the individual dat which is stored here.';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.transaction_limit IS 'Transaction Limit: Transaction limit is set for the individual records data which is stored this table';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.cr_by IS 'Created By : ID or name of the user who create / insert record';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN ida.identity_cache.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --
