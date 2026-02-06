package com.dansmultipro.ops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseModel {

    @Id
    @Column(length = 36)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false, length = 36)
    private UUID createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 36)
    private UUID updatedBy;

    @Column
    private LocalDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
