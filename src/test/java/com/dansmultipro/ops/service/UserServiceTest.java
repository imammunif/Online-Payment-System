package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.PaginatedResponseDto;
import com.dansmultipro.ops.dto.user.*;
import com.dansmultipro.ops.model.Gateway;
import com.dansmultipro.ops.model.GatewayUser;
import com.dansmultipro.ops.model.User;
import com.dansmultipro.ops.model.UserRole;
import com.dansmultipro.ops.pojo.AuthorizationPoJo;
import com.dansmultipro.ops.repository.*;
import com.dansmultipro.ops.service.impl.UserServiceImpl;
import com.dansmultipro.ops.util.MailUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRoleRepo userRoleRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private GatewayUserRepo gatewayUserRepo;
    @Mock
    private TransactionRepo transactionRepo;
    @Mock
    private GatewayRepo gatewayRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailUtil mailUtil;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    protected PrincipalService principalService;

    @InjectMocks
    private UserServiceImpl userService;

    private AuthorizationPoJo authPojo;
    private Gateway gateway1;
    private Gateway gateway2;
    private User userSystem;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private UserRole systemRole;
    private UserRole customerRole;
    private UserRole gatewayRole;
    private GatewayUser gatewayUser1;
    private GatewayUser gatewayUser2;

    @BeforeEach
    public void setup() {
        systemRole = new UserRole();
        systemRole.setId(UUID.randomUUID());
        systemRole.setCode("SYS");
        systemRole.setName("System");

        customerRole = new UserRole();
        customerRole.setId(UUID.randomUUID());
        customerRole.setCode("CUST");
        customerRole.setName("Customer");

        gatewayRole = new UserRole();
        gatewayRole.setId(UUID.randomUUID());
        gatewayRole.setCode("GA");
        gatewayRole.setName("Gateway");

        gateway1 = new Gateway();
        gateway1.setId(UUID.randomUUID());
        gateway1.setName("GATEWAY1");

        gateway2 = new Gateway();
        gateway2.setId(UUID.randomUUID());
        gateway2.setName("GATEWAY2");

        userSystem = new User();
        userSystem.setId(UUID.randomUUID());
        userSystem.setName("SYSTEM");
        userSystem.setEmail("system@internal.com");
        userSystem.setPassword("password");
        userSystem.setActive(false);
        userSystem.setVersion(0);
        userSystem.setUserRole(systemRole);

        user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setName("Customer 1");
        user1.setEmail("customer1@mail.com");
        user1.setPassword("password");
        user1.setActive(true);
        user1.setActivationCode("AKF676");
        user1.setVersion(1);
        user1.setUserRole(customerRole);

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setName("Customer 2");
        user2.setEmail("customer2@mail.com");
        user2.setPassword("password");
        user2.setActive(false);
        user2.setActivationCode("AKF678");
        user2.setVersion(0);
        user2.setUserRole(customerRole);

        user3 = new User();
        user3.setId(UUID.randomUUID());
        user3.setName("Gateway 1");
        user3.setEmail("gateway1@mail.com");
        user3.setPassword("password");
        user3.setActive(true);
        user3.setVersion(0);
        user3.setUserRole(gatewayRole);

        user4 = new User();
        user4.setId(UUID.randomUUID());
        user4.setName("Gateway 2");
        user4.setEmail("gateway2@mail.com");
        user4.setPassword("password");
        user4.setActive(true);
        user4.setVersion(0);
        user4.setUserRole(gatewayRole);

        gatewayUser1 = new GatewayUser();
        gatewayUser1.setId(UUID.randomUUID());
        gatewayUser1.setUser(user3);
        gatewayUser1.setGateway(gateway1);
        gatewayUser1.setVersion(0);

        gatewayUser2 = new GatewayUser();
        gatewayUser2.setId(UUID.randomUUID());
        gatewayUser2.setUser(user4);
        gatewayUser2.setGateway(gateway2);
        gatewayUser2.setVersion(0);

    }

    @Test
    public void shouldCreateUserCustomer_whenDataValid() {
        userService.setUserRepo(userRepo);

        var dto = new CreateUserCustomerRequestDto();
        dto.setName("New Customer User");
        dto.setEmail("customer@mail.com");
        dto.setPassword("password");

        Mockito.when(userRepo.findByUserRole_Code(Mockito.anyString())).thenReturn(Optional.of(userSystem));
        Mockito.when(userRoleRepo.findByCode(Mockito.any())).thenReturn(Optional.of(customerRole));
        Mockito.when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(dto.getPassword())).thenReturn("password");
        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(user1);

        var result = userService.createUserCustomer(dto);

        Assertions.assertEquals(user1.getId(), result.getId());
        Mockito.verify(userRepo, Mockito.atLeast(1)).findByUserRole_Code(Mockito.any());
        Mockito.verify(userRoleRepo, Mockito.atLeast(1)).findByCode(Mockito.any());
        Mockito.verify(userRepo, Mockito.atLeast(1)).findByEmail(dto.getEmail());
        Mockito.verify(passwordEncoder, Mockito.atLeast(1)).encode(dto.getPassword());
        Mockito.verify(userRepo, Mockito.atLeast(1)).save(Mockito.any(User.class));
        Mockito.verify(rabbitTemplate, Mockito.atLeast(1)).convertAndSend(Mockito.any(), Mockito.any(), Mockito.any(Object.class));
    }

    @Test
    public void shouldActivateUserCustomer_whenDataValid() {
        userService.setUserRepo(userRepo);

        String email = user2.getEmail();
        String code = user2.getActivationCode();

        Mockito.when(userRepo.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user2));
        Mockito.when(userRepo.findByUserRole_Code(Mockito.any())).thenReturn(Optional.of(userSystem));
        Mockito.when(userRepo.saveAndFlush(Mockito.any())).thenReturn(user2);

        var result = userService.activateUserCustomer(email, code);

        Assertions.assertEquals("User is successfully activated", result);
        Mockito.verify(userRepo, Mockito.atLeast(1)).findByEmail(Mockito.any());
        Mockito.verify(userRepo, Mockito.atLeast(1)).findByUserRole_Code(Mockito.any());
        Mockito.verify(userRepo, Mockito.atLeast(1)).saveAndFlush(Mockito.any());
    }

    @Test
    public void shouldCreateUserGateway_whenDataValid() {
        userService.setPrincipalService(principalService);
        authPojo = new AuthorizationPoJo(user1.getId());

        var dto = new CreateUserGatewayRequestDto();
        dto.setName("New Gateway User");
        dto.setEmail("gateway@mail.com");
        dto.setPassword("password");
        dto.setGatewayId(gateway1.getId().toString());

        Mockito.when(principalService.getPrincipal()).thenReturn(authPojo);
        Mockito.when(userRoleRepo.findByCode(Mockito.any())).thenReturn(Optional.of(gatewayRole));
        Mockito.when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(dto.getPassword())).thenReturn("password");
        Mockito.when(userRepo.save(Mockito.any())).thenReturn(user3);
        Mockito.when(gatewayRepo.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(gateway1));
        Mockito.when(gatewayUserRepo.save(Mockito.any(GatewayUser.class))).thenReturn(gatewayUser1);

        var result = userService.createUserGateway(dto);

        Assertions.assertEquals(user3.getId(), result.getId());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRoleRepo, Mockito.atLeast(1)).findByCode(Mockito.any());
        Mockito.verify(userRepo, Mockito.atLeast(1)).findByEmail(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.atLeast(1)).encode(Mockito.any());
        Mockito.verify(userRepo, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(gatewayRepo, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(gatewayUserRepo, Mockito.atLeast(1)).save(Mockito.any());
    }

    @Test
    public void shouldReturnData_whenIdValid() {
        Mockito.when(userRepo.findById(Mockito.any())).thenReturn(Optional.of(user1));

        var result = userService.getById(user1.getId().toString());

        Assertions.assertEquals(user1.getId(), result.getId());
        Mockito.verify(userRepo, Mockito.atLeast(1)).findById(Mockito.any());
    }

    @Test
    public void shouldReturnAllUserCustomer_whenExist() {
        int page = 1;
        int size = 10;
        List<User> customerList = List.of(user1, user2);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = new PageImpl<>(customerList, pageable, customerList.size());

        Mockito.when(userRoleRepo.findByCode(Mockito.any())).thenReturn(Optional.of(customerRole));
        Mockito.when(userRepo.findAllByUserRole_Id(Mockito.any(UUID.class), Mockito.any(Pageable.class))).thenReturn(userPage);

        PaginatedResponseDto<UserResponseDto> result = userService.getAllUserCustomers(page, size);

        Assertions.assertEquals(2, result.getTotal());
        Assertions.assertEquals(2, result.getData().size());
        Assertions.assertEquals(user1.getId(), result.getData().getFirst().getId());
        Assertions.assertEquals(user1.getName(), result.getData().getFirst().getName());
        Mockito.verify(userRoleRepo, Mockito.atLeast(1)).findByCode(Mockito.any());
        Mockito.verify(userRepo, Mockito.atLeast(1)).findAllByUserRole_Id(Mockito.eq(customerRole.getId()), Mockito.any(Pageable.class));
    }

    @Test
    public void shouldReturnAllUserGateway_whenExist() {
        int page = 1;
        int size = 10;
        List<GatewayUser> gatewayUserList = List.of(gatewayUser1, gatewayUser2);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<GatewayUser> gatewayUserPage = new PageImpl<>(gatewayUserList, pageable, gatewayUserList.size());

        Mockito.when(userRoleRepo.findByCode(Mockito.any())).thenReturn(Optional.of(gatewayRole));
        Mockito.when(gatewayUserRepo.findAllByUser_UserRole_Id(Mockito.any(UUID.class), Mockito.any(Pageable.class))).thenReturn(gatewayUserPage);

        PaginatedResponseDto<UserGatewayResponseDto> result = userService.getAllUserGateways(page, size);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getTotal());
        Assertions.assertEquals(2, result.getData().size());
        Assertions.assertEquals(gatewayUser1.getId(), result.getData().getFirst().getId());
        Assertions.assertEquals(gatewayUser1.getUser().getName(), result.getData().getFirst().getName());

        Mockito.verify(userRoleRepo, Mockito.atLeast(1)).findByCode(Mockito.any());
        Mockito.verify(gatewayUserRepo, Mockito.atLeast(1)).findAllByUser_UserRole_Id(Mockito.eq(gatewayRole.getId()), Mockito.any(Pageable.class));
    }

    @Test
    public void shouldUpdateData_whenVersionValid() {
        userService.setPrincipalService(principalService);
        authPojo = new AuthorizationPoJo(user1.getId());

        var dto = new UpdateUserRequestDto();
        dto.setName(user2.getName());
        dto.setVersion(user2.getVersion());

        var updateduser = new User();
        updateduser.setVersion(user1.getVersion());

        Mockito.when(principalService.getPrincipal()).thenReturn(authPojo);
        Mockito.when(userRepo.findById(Mockito.any())).thenReturn(Optional.of(user2));
        Mockito.when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepo.saveAndFlush(Mockito.any())).thenReturn(updateduser);

        var result = userService.update(user2.getId().toString(), dto);

        Assertions.assertEquals(1, result.getVersion());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepo, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(userRepo, Mockito.atLeast(1)).saveAndFlush(Mockito.any());


    }

}