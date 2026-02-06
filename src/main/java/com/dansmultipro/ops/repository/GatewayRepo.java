package com.dansmultipro.ops.repository;

import com.dansmultipro.ops.model.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GatewayRepo extends JpaRepository<Gateway, UUID> {
}
