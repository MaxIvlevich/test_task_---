package com.example.user_management_api.repository;

import com.example.user_management_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface UserRepository  extends JpaRepository<User, UUID> {
}
