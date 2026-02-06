package com.dansmultipro.ops.service.impl;


import com.dansmultipro.ops.constant.RoleCode;
import com.dansmultipro.ops.exception.InvalidUuidException;
import com.dansmultipro.ops.exception.NotFoundException;
import com.dansmultipro.ops.model.BaseModel;
import com.dansmultipro.ops.model.User;
import com.dansmultipro.ops.repository.UserRepo;
import com.dansmultipro.ops.service.PrincipalService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

public class BaseService {

    protected PrincipalService principalService;
    private UserRepo userRepo;

    @Autowired
    public void setPrincipalService(PrincipalService principalService) {
        this.principalService = principalService;
    }

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    protected <T extends BaseModel> T prepareForInsert(T object) {
        object.setId(UUID.randomUUID());
        object.setCreatedAt(LocalDateTime.now());
        object.setCreatedBy(principalService.getPrincipal().getId());
        return object;
    }

    protected <T extends BaseModel> T prepareForInsertBySystem(T object) {
        User userSystem = userRepo.findByUserRole_Code(RoleCode.SYSTEM.getCode()).orElseThrow(
                () -> new NotFoundException("User system not found")
        );
        object.setId(UUID.randomUUID());
        object.setCreatedAt(LocalDateTime.now());
        object.setCreatedBy(userSystem.getId());
        return object;
    }

    protected <T extends BaseModel> T prepareForUpdate(T object) {
        object.setUpdatedAt(LocalDateTime.now());
        object.setUpdatedBy(principalService.getPrincipal().getId());
        return object;
    }

    protected <T extends BaseModel> T prepareForUpdateBySystem(T object) {
        User userSystem = userRepo.findByUserRole_Code(RoleCode.SYSTEM.getCode()).orElseThrow(
                () -> new NotFoundException("User system not found")
        );
        object.setUpdatedAt(LocalDateTime.now());
        object.setUpdatedBy(userSystem.getId());
        return object;
    }

    protected UUID validateUUID(String id) {
        if (id.isBlank()) {
            throw new InvalidUuidException("Invalid ID format");
        }
        try {
            UUID validId = UUID.fromString(id);
            return validId;
        } catch (IllegalArgumentException ex) {
            throw new InvalidUuidException("Invalid ID format");
        }
    }

}
