package org.mosip.kernel.pridgenerator.dao;


import org.mosip.kernel.pridgenerator.model.Prid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PridGenRepository extends JpaRepository<Prid, String> {

}
