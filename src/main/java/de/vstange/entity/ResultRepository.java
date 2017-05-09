package de.vstange.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TODO
 *
 * @author Vincent Stange
 */
@Repository
public interface ResultRepository extends JpaRepository<Result, String> {

}
