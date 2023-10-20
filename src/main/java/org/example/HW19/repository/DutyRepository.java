package org.example.HW19.repository;

import org.example.HW19.entity.Duty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DutyRepository extends JpaRepository<Duty, Long> {

    Duty findByName(String name);
}
