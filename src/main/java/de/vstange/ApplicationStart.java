package org.citeplag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author Vincent Stange
 */
@Configuration
@EnableAutoConfiguration
public class ApplicationStart {

    public static void main(String[] args) throws Exception {
        // start the full spring environment
        SpringApplication.run(ApplicationStart.class, args);
    }
}