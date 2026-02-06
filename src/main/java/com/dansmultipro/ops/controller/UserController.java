package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.*;
import com.dansmultipro.ops.dto.user.*;
import com.dansmultipro.ops.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<PaginatedResponseDto<UserResponseDto>> getAllUserCustomers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PaginatedResponseDto<UserResponseDto> res = userService.getAllUserCustomers(page, size);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/gateways")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<PaginatedResponseDto<UserGatewayResponseDto>> getAllUserGateways(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PaginatedResponseDto<UserGatewayResponseDto> res = userService.getAllUserGateways(page, size);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SA', 'GA', 'CUST')")
    public ResponseEntity<UserResponseDto> getById(
            @PathVariable String id
    ) {
        UserResponseDto res = userService.getById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<CreateResponseDto> createUserCustomer(
            @RequestBody @Valid CreateUserCustomerRequestDto data
    ) {
        CreateResponseDto res = userService.createUserCustomer(data);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/gateways")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<CreateResponseDto> createGatewayAdmin(
            @RequestBody @Valid CreateUserGatewayRequestDto data
    ) {
        CreateResponseDto res = userService.createUserGateway(data);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('SA', 'GA', 'CUST')")
    public ResponseEntity<CommonResponseDto> changePassword(
            @RequestBody @Valid ChangePasswordRequestDto data
    ) {
        CommonResponseDto response = userService.changePassword(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SA', 'CUST')")
    public ResponseEntity<UpdateResponseDto> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateUserRequestDto data
    ) {
        UpdateResponseDto res = userService.update(id, data);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<DeleteResponseDto> delete(
            @PathVariable String id
    ) {
        DeleteResponseDto res = userService.deleteById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
