package de.vstange.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

/**
 * Standard JPA repository for results. Additionally Provides a streaming
 * function and a filter method.
 *
 * @author Vincent Stange
 */
@Repository
public interface FormulaRepository extends JpaRepository<Formula, String> {

    /**
     * http://knes1.github.io/blog/2015/2015-10-19-streaming-mysql-results-using-java8-streams-and-spring-data.html
     *
     * @return very nice stream of data
     */
    @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE))
    @org.springframework.data.jpa.repository.Query(value = "select t from Formula t")
    Stream<Formula> streamAll();

    Formula findByNameAndSectionname(String name, String sectionname);
}