package com.example.schoolmoney.logger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
}
