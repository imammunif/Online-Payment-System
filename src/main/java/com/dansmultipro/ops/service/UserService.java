package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.*;
import com.dansmultipro.ops.dto.user.*;
import com.dansmultipro.ops.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    PaginatedResponseDto<UserResponseDto> getAllUserCustomers(Integer page, Integer size);

    PaginatedResponseDto<UserGatewayResponseDto> getAllUserGateways(Integer page, Integer size);

    UserResponseDto getById(String id);

    CreateResponseDto createUserCustomer(CreateUserCustomerRequestDto data);

    CreateResponseDto createUserGateway(CreateUserGatewayRequestDto data);

    String activateUserCustomer(String email, String code);

    CommonResponseDto changePassword(ChangePasswordRequestDto data);

    UpdateResponseDto update(String id, UpdateUserRequestDto data);

    DeleteResponseDto deleteById(String id);

}
