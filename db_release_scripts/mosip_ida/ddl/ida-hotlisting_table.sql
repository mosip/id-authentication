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
