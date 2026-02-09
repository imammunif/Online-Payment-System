package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.config.RabbitMQConfig;
import com.dansmultipro.ops.constant.ResponseMessage;
import com.dansmultipro.ops.constant.RoleCode;
import com.dansmultipro.ops.dto.*;
import com.dansmultipro.ops.dto.user.*;
import com.dansmultipro.ops.exception.*;
import com.dansmultipro.ops.model.Gateway;
import com.dansmultipro.ops.model.GatewayUser;
import com.dansmultipro.ops.model.User;
import com.dansmultipro.ops.model.UserRole;
import com.dansmultipro.ops.pojo.MailPoJo;
import com.dansmultipro.ops.repository.*;
import com.dansmultipro.ops.service.UserService;
import com.dansmultipro.ops.util.MailUtil;
import com.dansmultipro.ops.util.RandomGenerator;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    private final UserRoleRepo userRoleRepo;
    private final UserRepo userRepo;
    private final GatewayUserRepo gatewayUserRepo;
    private final TransactionRepo transactionRepo;
    private final GatewayRepo gatewayRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailUtil mailUtil;
    private final RabbitTemplate rabbitTemplate;

    public UserServiceImpl(UserRoleRepo userRoleRepo, UserRepo userRepo, GatewayUserRepo gatewayUserRepo, TransactionRepo transactionRepo, GatewayRepo gatewayRepo, PasswordEncoder passwordEncoder, MailUtil mailUtil, RabbitTemplate rabbitTemplate) {
        this.userRoleRepo = userRoleRepo;
        this.userRepo = userRepo;
        this.gatewayUserRepo = gatewayUserRepo;
        this.transactionRepo = transactionRepo;
        this.gatewayRepo = gatewayRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailUtil = mailUtil;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found")
        );
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(email));
        if (!user.getActive()) {
            throw new InactiveException("Account is not activated");
        }
        return new org.springframework.security.core.userdetails.User(
                email, user.getPassword(), new ArrayList<>()
        );
    }

    @Cacheable(value = "users", key = "'page:' + #page + 'size:' + #size")
    @Override
    public PaginatedResponseDto<UserResponseDto> getAllUserCustomers(Integer page, Integer size) {
        UserRole userRole = userRoleRepo.findByCode(RoleCode.CUSTOMER.getCode()).orElseThrow(
                () -> new NotFoundException("Role not found")
        );

        if (page < 1) {
            throw new InvalidPageException("Invalid requested page, minimum 1");
        }
        if (size < 5) {
            throw new InvalidPageException("Invalid requested page size, minimum 5");
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = userRepo.findAllByUserRole_Id(userRole.getId(), pageable);

        List<User> userList = userPage.getContent();
        List<UserResponseDto> userResponseDtoList = new ArrayList<>();
        for (User v : userList) {
            UserResponseDto dto = new UserResponseDto(
                    v.getId(), v.getName(), v.getEmail(), v.getUserRole().getName(),
                    v.getActive().toString(), v.getVersion().toString()
            );
            userResponseDtoList.add(dto);
        }

        PaginatedResponseDto<UserResponseDto> paginatedUserResponse = new PaginatedResponseDto<>(
                userResponseDtoList,
                userPage.getTotalElements()
        );

        return paginatedUserResponse;
    }

    @Cacheable(value = "user-gateways", key = "'page:' + #page + 'size:' + #size")
    @Override
    public PaginatedResponseDto<UserGatewayResponseDto> getAllUserGateways(Integer page, Integer size) {
        UserRole userRole = userRoleRepo.findByCode(RoleCode.GATEWAY.getCode()).orElseThrow(
                () -> new NotFoundException("Role not found")
        );

        if (page < 1) {
            throw new InvalidPageException("Invalid requested page, minimum 1");
        }
        if (size < 5) {
            throw new InvalidPageException("Invalid requested page size, minimum 5");
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<GatewayUser> userPage = gatewayUserRepo.findAllByUser_UserRole_Id(userRole.getId(), pageable);

        List<GatewayUser> userList = userPage.getContent();
        List<UserGatewayResponseDto> userGatewayResponseDtoList = new ArrayList<>();
        for (GatewayUser v : userList) {
            UserGatewayResponseDto dto = new UserGatewayResponseDto(
                    v.getId(), v.getUser().getName(), v.getUser().getEmail(), v.getUser().getUserRole().getName(),
                    v.getGateway().getName(), v.getVersion().toString()
            );
            userGatewayResponseDtoList.add(dto);
        }

        PaginatedResponseDto<UserGatewayResponseDto> paginatedUserResponse = new PaginatedResponseDto<>(
                userGatewayResponseDtoList,
                userPage.getTotalElements()
        );

        return paginatedUserResponse;
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    public UserResponseDto getById(String id) {
        UUID validId = validateUUID(id);
        User user = userRepo.findById(validId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getUserRole().getName(), user.getActive().toString(), user.getVersion().toString());
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional(rollbackOn = Exception.class)
    @Override
    public CreateResponseDto createUserCustomer(CreateUserCustomerRequestDto data) {
        UserRole userRole = userRoleRepo.findByCode(RoleCode.CUSTOMER.getCode()).orElseThrow(
                () -> new NotFoundException("Role not found")
        );
        User newUser = prepareForInsertBySystem(new User());
        String requestEmail = data.getEmail();
        if (userRepo.findByEmail(requestEmail).isPresent()) {
            throw new AlreadyExistsException("Email already exist");
        }
        String code = RandomGenerator.randomizeCode(6);
        newUser.setEmail(requestEmail);
        newUser.setPassword(passwordEncoder.encode(data.getPassword()));
        newUser.setName(data.getName());
        newUser.setActive(false);
        newUser.setActivationCode(code);
        newUser.setUserRole(userRole);
        User createdUser = userRepo.save(newUser);

        MailPoJo mailPoJo = new MailPoJo(
                createdUser.getEmail(),
                createdUser.getActivationCode(),
                createdUser.getName()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EX_USER,
                RabbitMQConfig.EMAIL_KEY_USER,
                mailPoJo
        );

        return new CreateResponseDto(createdUser.getId(), ResponseMessage.CREATED.getMessage());
    }

    @CacheEvict(value = "user-gateways", allEntries = true)
    @Transactional(rollbackOn = Exception.class)
    @Override
    public CreateResponseDto createUserGateway(CreateUserGatewayRequestDto data) {
        UserRole userRole = userRoleRepo.findByCode(RoleCode.GATEWAY.getCode()).orElseThrow(
                () -> new NotFoundException("Role not found")
        );
        User newUser = prepareForInsert(new User());
        String requestEmail = data.getEmail();
        if (userRepo.findByEmail(requestEmail).isPresent()) {
            throw new AlreadyExistsException("Email already exist");
        }
        newUser.setEmail(requestEmail);
        newUser.setPassword(passwordEncoder.encode(data.getPassword()));
        newUser.setName(data.getName());
        newUser.setActive(true);
        newUser.setUserRole(userRole);
        User createdUser = userRepo.save(newUser);

        Gateway gateway = gatewayRepo.findById(validateUUID(data.getGatewayId())).orElseThrow(
                () -> new NotFoundException("Gateway not found")
        );
        GatewayUser newGatewayUser = prepareForInsert(new GatewayUser());
        newGatewayUser.setUser(createdUser);
        newGatewayUser.setGateway(gateway);
        gatewayUserRepo.save(newGatewayUser);

        return new CreateResponseDto(createdUser.getId(), ResponseMessage.CREATED.getMessage());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public String activateUserCustomer(String email, String code) {
        User user = userRepo.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        if (!user.getActivationCode().equals(code)) {
            throw new MissMatchException("Oops!, Code is not valid");
        }
        if (user.getActive()) {
            throw new AlreadyActivatedException("User already activated");
        }
        User updateUser = prepareForUpdateBySystem(user);
        updateUser.setActive(true);
        userRepo.saveAndFlush(updateUser);

        return "User is successfully activated";
    }

    @Override
    public CommonResponseDto changePassword(ChangePasswordRequestDto data) {
        UUID userId = principalService.getPrincipal().getId();
        User user = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        if (!passwordEncoder.matches(data.getOldPassword(), user.getPassword())) {
            throw new MissMatchException("Wrong old password");
        }

        User userUpdate = prepareForUpdate(user);
        String newPassword = passwordEncoder.encode(data.getNewPassword());
        userUpdate.setPassword(newPassword);
        userRepo.saveAndFlush(user);

        return new CommonResponseDto("Change password success");
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public UpdateResponseDto update(String id, UpdateUserRequestDto data) {
        UUID validId = validateUUID(id);
        User user = userRepo.findById(validId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        if (!user.getVersion().equals(data.getVersion())) {
            throw new MissMatchException("Version not match");
        }
        User userUpdate = prepareForUpdate(user);
        String requestEmail = data.getEmail();
        if (!user.getEmail().equals(requestEmail)) {
            if (userRepo.findByEmail(requestEmail).isPresent()) {
                throw new AlreadyExistsException("Email already exist");
            }
        }
        userUpdate.setEmail(requestEmail);
        userUpdate.setName(data.getName());
        User updatedUser = userRepo.saveAndFlush(userUpdate);

        return new UpdateResponseDto(updatedUser.getVersion(), ResponseMessage.UPDATED.getMessage());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public DeleteResponseDto deleteById(String id) {
        UUID validId = validateUUID(id);
        User user = userRepo.findById(validId).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        if (gatewayUserRepo.existsByUserId(validId)) {
            throw new ResourceInUseException("Unable to delete user, already referenced in gateway-user records");
        }
        if (transactionRepo.existsByCustomerId(validId)) {
            throw new ResourceInUseException("Unable to delete user, already referenced in transaction records");
        }
        userRepo.deleteById(user.getId());

        return new DeleteResponseDto(ResponseMessage.DELETED.getMessage());
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE_USER)
    public void receiveEmailNotificationRegister(MailPoJo pojo) {
        Context context = new Context();
        String userName = pojo.getUsername();
        String url = "http://localhost:8080/users/activate"
                + "?email=" + URLEncoder.encode(pojo.getEmailAddress(), StandardCharsets.UTF_8)
                + "&code=" + pojo.getEmailBody();

        context.setVariable("userName", userName);
        context.setVariable("messageContent", "Your account has been created! Click this link in order to activate your profile and enable transactions.");
        context.setVariable("actionUrl", url);

        mailUtil.sendHtml(
                pojo.getEmailAddress(),
                "Activate Your Account",
                "email-template-verification",
                context
        );
    }

}
