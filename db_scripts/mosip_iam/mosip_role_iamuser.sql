-- object: masteruser | type: ROLE --
-- DROP ROLE IF EXISTS masteruser;
CREATE ROLE iamuser WITH 
	INHERIT
	LOGIN
	PASSWORD 'Mosip@dev123';
-- ddl-end --
