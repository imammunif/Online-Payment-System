package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.config.RabbitMQConfig;
import com.dansmultipro.ops.constant.ResponseMessage;
import com.dansmultipro.ops.constant.RoleCode;
import com.dansmultipro.ops.constant.StatusCode;
import com.dansmultipro.ops.dto.CreateResponseDto;
import com.dansmultipro.ops.dto.PaginatedResponseDto;
import com.dansmultipro.ops.dto.UpdateResponseDto;
import com.dansmultipro.ops.dto.transaction.CreateTransactionRequestDto;
import com.dansmultipro.ops.dto.transaction.TransactionResponseDto;
import com.dansmultipro.ops.exception.InvalidPageException;
import com.dansmultipro.ops.exception.InvalidStatusException;
import com.dansmultipro.ops.exception.MissMatchException;
import com.dansmultipro.ops.exception.NotFoundException;
import com.dansmultipro.ops.model.*;
import com.dansmultipro.ops.pojo.MailPoJo;
import com.dansmultipro.ops.pojo.MailUpdateStatusPoJo;
import com.dansmultipro.ops.repository.*;
import com.dansmultipro.ops.service.TransactionService;
import com.dansmultipro.ops.util.MailUtil;
import com.dansmultipro.ops.util.RandomGenerator;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl extends BaseService implements TransactionService {

    private final UserRepo userRepo;
    private final GatewayRepo gatewayRepo;
    private final GatewayUserRepo gatewayUserRepo;
    private final TransactionRepo transactionRepo;
    private final TransactionStatusRepo transactionStatusRepo;
    private final TransactionStatusHistoryRepo transactionStatusHistoryRepo;
    private final ProductRepo productRepo;
    private final MailUtil mailUtil;
    private final RabbitTemplate rabbitTemplate;

    public TransactionServiceImpl(UserRepo userRepo, GatewayRepo gatewayRepo, GatewayUserRepo gatewayUserRepo, TransactionRepo transactionRepo, TransactionStatusRepo transactionStatusRepo, TransactionStatusHistoryRepo transactionStatusHistoryRepo, ProductRepo productRepo, MailUtil mailUtil, RabbitTemplate rabbitTemplate) {
        this.userRepo = userRepo;
        this.gatewayRepo = gatewayRepo;
        this.gatewayUserRepo = gatewayUserRepo;
        this.transactionRepo = transactionRepo;
        this.transactionStatusRepo = transactionStatusRepo;
        this.transactionStatusHistoryRepo = transactionStatusHistoryRepo;
        this.productRepo = productRepo;
        this.mailUtil = mailUtil;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public PaginatedResponseDto<TransactionResponseDto> getAll(Integer page, Integer size) {

        validatePagination(page, size);
        Pageable pageable = PageRequest.of(page - 1, size);

        UUID userId = principalService.getPrincipal().getId();
        User user = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        Page<Transaction> transactionPage = fetchHistoryPage(user, pageable);
        List<TransactionResponseDto> responseDtoList = transactionPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        PaginatedResponseDto<TransactionResponseDto> paginatedTransactionResponse = new PaginatedResponseDto<>(
                responseDtoList,
                transactionPage.getTotalElements()
        );

        return paginatedTransactionResponse;
    }

    @CacheEvict(value = "histories", allEntries = true)
    @Transactional(rollbackOn = Exception.class)
    @Override
    public CreateResponseDto create(CreateTransactionRequestDto data) {
        UUID gatewayId = validateUUID(data.getGatewayId());
        UUID productId = validateUUID(data.getProductId());
        User user = userRepo.findById(principalService.getPrincipal().getId()).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        TransactionStatus status = transactionStatusRepo.findByCode(StatusCode.PROCESS.getCode()).orElseThrow(
                () -> new NotFoundException("Status not found")
        );
        Gateway gateway = gatewayRepo.findById(gatewayId).orElseThrow(
                () -> new NotFoundException("Gateway not found")
        );
        Product product = productRepo.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found")
        );

        Transaction newTransaction = prepareForInsert(new Transaction());
        newTransaction.setCustomer(user);
        newTransaction.setStatus(status);
        newTransaction.setCode(RandomGenerator.randomizeCode(10));
        newTransaction.setGateway(gateway);
        newTransaction.setProduct(product);
        newTransaction.setTotalBill(data.getAmount());
        newTransaction.setAccountNumber(data.getAccountNumber());
        Transaction createdTransaction = transactionRepo.save(newTransaction);

        TransactionStatusHistory newStatusHistory = prepareForInsert(new TransactionStatusHistory());
        newStatusHistory.setStatus(status);
        newStatusHistory.setTransaction(createdTransaction);
        transactionStatusHistoryRepo.save(newStatusHistory);

        MailPoJo mailPoJo = new MailPoJo(
                user.getEmail(),
                createdTransaction.getCode(),
                user.getName()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EX_TRANSACTION,
                RabbitMQConfig.EMAIL_KEY_TRANSACTION,
                mailPoJo
        );

        return new CreateResponseDto(createdTransaction.getId(), ResponseMessage.CREATED.getMessage());
    }

    @CacheEvict(value = "histories", allEntries = true)
    @Transactional(rollbackOn = Exception.class)
    @Override
    public UpdateResponseDto update(String id, String action, Integer version) {
        UUID userId = principalService.getPrincipal().getId();
        gatewayUserRepo.findByUserId(userId).orElseThrow(
                () -> new NotFoundException("Gateway user not found")
        );
        Transaction transaction = transactionRepo.findById(validateUUID(id)).orElseThrow(
                () -> new NotFoundException("Transaction not found")
        );
        if (!transaction.getVersion().equals(version)) {
            throw new MissMatchException("Version not match");
        }
        TransactionStatus transactionStatus = transactionStatusRepo.findByCode(action).orElseThrow(
                () -> new NotFoundException("Status is not found")
        );

        Transaction updateTransaction = prepareForUpdate(transaction);
        String currentStatus = transaction.getStatus().getCode();

        if (action.equals(StatusCode.PAID.getCode())) {
            if (currentStatus.equals(StatusCode.REJECTED.getCode())) {
                throw new InvalidStatusException("Rejected transaction can't be paid");
            } else if (currentStatus.equals(StatusCode.PAID.getCode())) {
                throw new InvalidStatusException("Transaction already paid");
            } else {
                updateTransaction.setStatus(transactionStatus);
            }
        }
        if (action.equals(StatusCode.REJECTED.getCode())) {
            if (currentStatus.equals(StatusCode.PAID.getCode())) {
                throw new InvalidStatusException("Paid transaction can't be rejected");
            } else if (currentStatus.equals(StatusCode.REJECTED.getCode())) {
                throw new InvalidStatusException("Transaction already rejected");
            } else {
                updateTransaction.setStatus(transactionStatus);
            }
        }
        Transaction updatedTransaction = transactionRepo.saveAndFlush(updateTransaction);

        TransactionStatusHistory newStatusHistory = prepareForInsert(new TransactionStatusHistory());
        newStatusHistory.setStatus(transactionStatus);
        newStatusHistory.setTransaction(transaction);
        transactionStatusHistoryRepo.save(newStatusHistory);

        MailUpdateStatusPoJo mailPoJo = new MailUpdateStatusPoJo(
                updatedTransaction.getCustomer().getEmail(),
                updatedTransaction.getCode(),
                transaction.getCustomer().getName(),
                updatedTransaction.getStatus().getName()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EX_STATUS,
                RabbitMQConfig.EMAIL_KEY_STATUS,
                mailPoJo
        );

        return new UpdateResponseDto(updatedTransaction.getVersion(), ResponseMessage.UPDATED.getMessage());
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE_TRANSACTION)
    public void receiveEmailNotificationTransaction(MailPoJo pojo) {
        Context context = new Context();
        context.setVariable("userName", pojo.getUsername());
        context.setVariable("transactionCode", pojo.getEmailBody());

        mailUtil.sendHtml(
                pojo.getEmailAddress(),
                "New Transaction Successfully Created",
                "email-template-transaction-created",
                context);
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE_STATUS)
    public void receiveEmailNotificationUpdate(MailUpdateStatusPoJo pojo) {
        Context context = new Context();
        String status = pojo.getStatus();
        String name = pojo.getUsername();
        String trxCode = pojo.getEmailBody();
        String content = status.equalsIgnoreCase("Paid")
                ? "Thank you for your payment. Your transaction is now complete."
                : "Unfortunately, your transaction could not be processed at this time.";
        String subject = status.equalsIgnoreCase("Paid")
                ? "Transaction Success " + trxCode
                : "Transaction rejected " + trxCode;

        context.setVariable("userName", name);
        context.setVariable("transactionCode", trxCode);
        context.setVariable("status", status);
        context.setVariable("messageContent", content);

        mailUtil.sendHtml(
                pojo.getEmailAddress(),
                subject,
                "email-template-transaction-updated",
                context
        );
    }

    private void validatePagination(Integer page, Integer size) {
        if (page < 1) {
            throw new InvalidPageException("Invalid requested page, minimum 1");
        }
        if (size < 5) {
            throw new InvalidPageException("Invalid requested page size, minimum 5");
        }
    }

    private Page<Transaction> fetchHistoryPage(User user, Pageable pageable) {
        if (user.getUserRole().getCode().equals(RoleCode.SUPERADMIN.getCode())) {
            return transactionRepo.findAll(pageable);
        } else if (user.getUserRole().getCode().equals(RoleCode.CUSTOMER.getCode())) {
            return transactionRepo.findByCustomerId(user.getId(), pageable);
        } else if (user.getUserRole().getCode().equals(RoleCode.GATEWAY.getCode())) {
            GatewayUser gatewayUser = gatewayUserRepo.findByUserId(user.getId()).orElseThrow(
                    () -> new NotFoundException("Gateway user not found")
            );
            return transactionRepo.findByGatewayId(gatewayUser.getGateway().getId(), pageable);
        } else {
            throw new NotFoundException("Invalid user role");
        }
    }

    private TransactionResponseDto mapToResponse(Transaction v) {
        return new TransactionResponseDto(
                v.getId().toString(), v.getCode(), v.getTotalBill().toString(),
                v.getAccountNumber(), v.getStatus().getName(), v.getCustomer().getName(),
                v.getGateway().getName(), v.getProduct().getName());
    }

}
