package com.dansmultipro.ops.repository;

import com.dansmultipro.ops.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepo extends JpaRepository<UserRole, UUID> {

    Optional<UserRole> findByCode(String code);

}
