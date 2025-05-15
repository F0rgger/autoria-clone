package com.autoria.clone;

import com.autoria.clone.application.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AutoriaCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoriaCloneApplication.class, args);
    }

    @Bean
    public CommandLineRunner initRoles(RoleService roleService) {
        return args -> {
            roleService.initializeRolesAndPermissions();
        };
    }
}