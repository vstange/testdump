package de.vstange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Just simple start up class for the spring boot environment.
 * Why spring boot? I just wanted a simple db/hibernate setup.
 *
 * @author Vincent Stange
 */
@SpringBootApplication
@EnableJpaRepositories
public class ApplicationStart {

    public static void main(String[] args) throws Exception {
        // start the full spring environment
        SpringApplication.run(ApplicationStart.class, args);
    }

}