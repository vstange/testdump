package de.vstange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
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