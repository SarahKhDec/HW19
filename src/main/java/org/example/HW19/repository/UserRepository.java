package org.example.HW19.repository;

import org.example.HW19.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);

    User findByEmail(String email);
    User findByEmailAndId (String email, Long id);
}
