package com.example.user_management_api.repository;

import com.example.user_management_api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository  extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    /**
     * Загружает страницу пользователей, подтягивая связанные данные UserData
     * для избежания проблемы N+1.
     * @param pageable параметры пагинации.
     * @return страница пользователей.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userData")
    Page<User> findAllWithData(Pageable pageable);
}
