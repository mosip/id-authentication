-- object: ida.hotlist_cache | type: TABLE --

-- DROP TABLE IF EXISTS ida.hotlist_cache CASCADE;

CREATE TABLE IF NOT EXISTS ida.hotlist_cache (
	id_hash character varying(128) NOT NULL,
	id_type character varying(128) NOT NULL,
	status character varying(64),
	start_timestamp timestamp,
	expiry_timestamp timestamp,
	CONSTRAINT "pk_idHashidType" PRIMARY KEY (id_hash,id_type)

);



