package com.dansmultipro.ops.repository;

import com.dansmultipro.ops.model.GatewayUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GatewayUserRepo extends JpaRepository<GatewayUser, UUID> {

    Boolean existsByUserId(UUID id);

    Optional<GatewayUser> findByUserId(UUID userId);

    Page<GatewayUser> findAllByUser_UserRole_Id(UUID roleId, Pageable pageable);

}
