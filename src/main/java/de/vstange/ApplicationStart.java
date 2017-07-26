package de.vstange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Just simple run the class to start up the spring boot environment.
 * Why spring boot? I just wanted a simple db/hibernate setup.
 *
 * @author Vincent Stange
 */
@SpringBootApplication
@EnableJpaRepositories
public class ApplicationStart {

    /**
     * This is will start the spring boot environment and scan
     * for runnable classes. For a full test run it should at
     * least find {@link de.vstange.runner.TestRunner}.
     *
     * @param args additional parameters, none used so far
     * @throws Exception throw everything
     */
    public static void main(String[] args) throws Exception {
        // start the full spring environment
        SpringApplication.run(ApplicationStart.class, args);
    }
}