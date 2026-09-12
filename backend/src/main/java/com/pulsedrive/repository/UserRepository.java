package com.pulsedrive.repository;
import com.pulsedrive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Long> {
  Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

  List<User> findByRoleIgnoreCase(String role);

List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String firstName,
        String lastName,
        String email
);
    long countByRoleIgnoreCase(String role);
}
