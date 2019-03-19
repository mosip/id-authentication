-- object: ida.token_seed | type: TABLE --
-- DROP TABLE IF EXISTS ida.token_seed CASCADE;
CREATE TABLE ida.token_seed(
	seed_no character varying(32) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_tknsd PRIMARY KEY (seed_no)

);
-- ddl-end --
COMMENT ON TABLE ida.token_seed IS 'Token ID Seed: Stores a random number that will be used as seed in the algorithm to generate a token ID. This seed value is encrypted/hashed and used along with a counter in the algorithm to generate a unique random number. Only one seed value would be available for the generation of token ID and this will never change.';
-- ddl-end --
COMMENT ON COLUMN ida.token_seed.seed_no IS 'Seed Number: Seed number is the random number generated which will be used as seed in the algorithm to generate token ID.';
-- ddl-end --
ALTER TABLE ida.token_seed OWNER TO sysadmin;
-- ddl-end --

