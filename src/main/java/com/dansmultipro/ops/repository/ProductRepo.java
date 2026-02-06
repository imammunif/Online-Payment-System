package com.dansmultipro.ops.repository;

import com.dansmultipro.ops.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {
}
