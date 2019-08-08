-- object: idmapuser | type: ROLE --
-- DROP ROLE IF EXISTS idmapuser;
CREATE ROLE idmapuser WITH 
	INHERIT
	LOGIN
	PASSWORD 'Mosip@dev123';
-- ddl-end --
