package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.constant.RoleCode;
import com.dansmultipro.ops.exception.NotFoundException;
import com.dansmultipro.ops.model.GatewayUser;
import com.dansmultipro.ops.pojo.AuthorizationPoJo;
import com.dansmultipro.ops.repository.GatewayUserRepo;
import com.dansmultipro.ops.service.PrincipalService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PrincipalServiceImpl implements PrincipalService {

    private final GatewayUserRepo gatewayUserRepo;

    public PrincipalServiceImpl(GatewayUserRepo gatewayUserRepo) {
        this.gatewayUserRepo = gatewayUserRepo;
    }

    @Override
    public AuthorizationPoJo getPrincipal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new UsernameNotFoundException("Invalid login");
        }

        return (AuthorizationPoJo) auth.getPrincipal();
    }

    @Override
    public String getFilterId(UUID id, String roleCode) {
        if (roleCode.equals(RoleCode.GATEWAY.getCode())) {
            return getGatewayId(id);
        }
        return id.toString();
    }

    private String getGatewayId(UUID id) {
        GatewayUser gatewayUser = gatewayUserRepo.findByUserId(id).orElseThrow(
                () -> new NotFoundException("Gateway user not found")
        );
        String gatewayId = gatewayUser.getGateway().getId().toString();
        return gatewayId;
    }

}
