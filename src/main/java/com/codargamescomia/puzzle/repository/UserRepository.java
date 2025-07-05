package com.codargamescomia.puzzle.repository;

import com.codargamescomia.puzzle.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}