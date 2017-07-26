package de.vstange.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Standard JPA repository for votes.
 *
 * @author Vincent Stange
 */
@Repository
public interface VotesRepository extends JpaRepository<Votes, String> {
}