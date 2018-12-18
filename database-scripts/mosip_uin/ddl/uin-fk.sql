
-- object: uind_uin_fk | type: CONSTRAINT --
-- ALTER TABLE uin.uin_detail DROP CONSTRAINT IF EXISTS uind_uin_fk CASCADE;
ALTER TABLE uin.uin_detail ADD CONSTRAINT uind_uin_fk FOREIGN KEY (uin_ref_id)
REFERENCES uin.uin (uin_ref_id) MATCH FULL
ON DELETE SET NULL ON UPDATE CASCADE;
-- ddl-end --
