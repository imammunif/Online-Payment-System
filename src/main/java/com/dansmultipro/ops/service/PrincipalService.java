package com.dansmultipro.ops.service;

import com.dansmultipro.ops.pojo.AuthorizationPoJo;

import java.util.UUID;

public interface PrincipalService {

    AuthorizationPoJo getPrincipal();

    String getFilterId(UUID id, String roleCode);

}
