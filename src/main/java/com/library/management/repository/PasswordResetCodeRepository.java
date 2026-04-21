package com.library.management.repository;

import com.library.management.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<PasswordResetCode> findByEmailAndCodeAndUsedFalse(String email, String code);

    Optional<PasswordResetCode> findByEmailAndCodeAndUsedFalseAndVerifiedTrue(String email, String code);
}