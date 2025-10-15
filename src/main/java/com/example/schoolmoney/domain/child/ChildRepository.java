package com.example.schoolmoney.domain.child;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChildRepository extends JpaRepository<Child, UUID> {
}
