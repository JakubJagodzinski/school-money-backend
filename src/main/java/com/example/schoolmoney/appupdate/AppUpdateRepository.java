package com.example.schoolmoney.appupdate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUpdateRepository extends JpaRepository<AppUpdate, UUID> {

    Optional<AppUpdate> findFirstByOrderByVersionDesc();

}
