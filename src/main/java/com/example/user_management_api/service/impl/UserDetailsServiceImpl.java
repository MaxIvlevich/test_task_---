package com.example.user_management_api.service.impl;

import com.example.user_management_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Загружает пользователя по его идентификатору.
     * Spring Security вызывает этот метод во время аутентификации.
     * @param identifier Идентификатор пользователя (может быть username или email).
     * @return объект UserDetails (в нашем случае, наш класс User, который его реализует).
     * @throws UsernameNotFoundException если пользователь не найден.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.debug("Attempting to load user by identifier: {}", identifier);

        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> {
                    log.warn("User not found with identifier: {}", identifier);
                    return new UsernameNotFoundException("User Not Found with identifier: " + identifier);
                });
    }
}
