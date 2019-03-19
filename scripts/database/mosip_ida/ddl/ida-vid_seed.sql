-- object: ida.vid_seed | type: TABLE --
-- DROP TABLE IF EXISTS ida.vid_seed CASCADE;
CREATE TABLE ida.vid_seed(
	seed_no character varying(32) NOT NULL,
	cr_by character varying(32) NOT NULL,
	cr_dtimes timestamp NOT NULL,
	is_deleted boolean,
	del_dtimes timestamp,
	CONSTRAINT pk_vidsd PRIMARY KEY (seed_no)

);
-- ddl-end --
COMMENT ON TABLE ida.vid_seed IS 'Virtual ID Seed: Stores a random number that will be used as seed in the algorithm to generate a vid. This seed value is encrypted/hashed and used along with a counter in the algorithm to generate a unique random number. Only one seed value would be available for the generation of vid and this will never change.';
-- ddl-end --
COMMENT ON COLUMN ida.vid_seed.seed_no IS 'Seed Number: Seed number is the random number generated which will be used as seed in the algorithm to generate vid.';
-- ddl-end --
ALTER TABLE ida.vid_seed OWNER TO sysadmin;
-- ddl-end --

