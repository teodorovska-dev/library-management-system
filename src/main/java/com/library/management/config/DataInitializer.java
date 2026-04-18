package com.library.management.config;

import com.library.management.entity.Role;
import com.library.management.enums.RoleName;
import com.library.management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.findByName(RoleName.USER).isEmpty()) {
            roleRepository.save(Role.builder().name(RoleName.USER).build());
        }

        if (roleRepository.findByName(RoleName.ADMIN).isEmpty()) {
            roleRepository.save(Role.builder().name(RoleName.ADMIN).build());
        }
    }
}