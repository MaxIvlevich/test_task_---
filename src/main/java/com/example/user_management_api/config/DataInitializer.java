package com.example.user_management_api.config;

import com.example.user_management_api.model.User;
import com.example.user_management_api.model.UserData;
import com.example.user_management_api.model.enums.Role;
import com.example.user_management_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

/**
 * Инициализатор данных.
 * Выполняется при старте приложения для создания начальных данных,
 * таких как пользователь-администратор.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Этот метод будет выполнен после того, как Spring Boot полностью запустится.
     * @param args аргументы командной строки (не используются).
     */
    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("Admin user not found. Creating a new one...");

            UserData adminData = UserData.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .dateOfBirth(LocalDate.now())
                    .build();

            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .email("admin@example.com")
                    .phoneNumber("+70000000000")
                    .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                    .build();
            adminData.setUser(adminUser);
            adminUser.setUserData(adminData);
            userRepository.save(adminUser);
            log.info("Admin user 'admin' created successfully.");
        } else {
            log.info("Admin user already exists. Skipping creation.");
        }
    }
}
