package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.PaginatedResponseDto;
import com.dansmultipro.ops.dto.transactionstatushistory.TransactionStatusHistoryResponseDto;
import com.dansmultipro.ops.model.*;
import com.dansmultipro.ops.pojo.AuthorizationPoJo;
import com.dansmultipro.ops.repository.GatewayUserRepo;
import com.dansmultipro.ops.repository.TransactionStatusHistoryRepo;
import com.dansmultipro.ops.service.impl.TransactionStatusHistoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionStatusHistoryServiceTest {

    @Mock
    private TransactionStatusHistoryRepo transactionStatusHistoryRepo;
    @Mock
    private GatewayUserRepo gatewayUserRepo;
    @Mock
    protected PrincipalService principalService;

    @InjectMocks
    private TransactionStatusHistoryServiceImpl transactionStatusHistoryService;

    private AuthorizationPoJo authPojo;
    private Product product;
    private Gateway gateway1;
    private Gateway gateway2;
    private User user1;
    private User user2;
    private GatewayUser gatewayUser;
    private Transaction transaction1;
    private Transaction transaction2;
    private TransactionStatus status1;
    private TransactionStatus status2;
    private TransactionStatusHistory history1;
    private TransactionStatusHistory history2;
    private TransactionStatusHistory history3;

    @BeforeEach
    public void setup() {
        transactionStatusHistoryService.setPrincipalService(principalService);

        user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setName("USER1");
        user1.setEmail("user1@mail.com");

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setName("USER2");
        user2.setEmail("user2@mail.com");

        authPojo = new AuthorizationPoJo(user1.getId());

        status1 = new TransactionStatus();
        status1.setId(UUID.randomUUID());
        status1.setCode("PROCESS");
        status1.setName("In Process");

        status2 = new TransactionStatus();
        status2.setId(UUID.randomUUID());
        status2.setCode("PAID");
        status2.setName("Paid");

        gateway1 = new Gateway();
        gateway1.setId(UUID.randomUUID());
        gateway1.setName("GATEWAY1");

        gateway2 = new Gateway();
        gateway2.setId(UUID.randomUUID());
        gateway2.setName("GATEWAY2");

        gatewayUser = new GatewayUser();
        gatewayUser.setGateway(gateway1);
        gatewayUser.setUser(user1);

        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("PRODUCT");

        transaction1 = new Transaction();
        transaction1.setId(UUID.randomUUID());
        transaction1.setVersion(0);
        transaction1.setCode("TRX001");
        transaction1.setTotalBill(new BigDecimal("13999"));
        transaction1.setAccountNumber("081234567");
        transaction1.setStatus(status1);
        transaction1.setCustomer(user1);
        transaction1.setGateway(gateway1);
        transaction1.setProduct(product);

        transaction2 = new Transaction();
        transaction2.setId(UUID.randomUUID());
        transaction2.setVersion(0);
        transaction2.setCode("TRX002");
        transaction2.setTotalBill(new BigDecimal("71999"));
        transaction2.setAccountNumber("081234568");
        transaction2.setStatus(status1);
        transaction2.setCustomer(user2);
        transaction2.setGateway(gateway2);
        transaction2.setProduct(product);

        history1 = new TransactionStatusHistory();
        history1.setId(UUID.randomUUID());
        history1.setStatus(status1);
        history1.setTransaction(transaction1);

        history2 = new TransactionStatusHistory();
        history2.setId(UUID.randomUUID());
        history2.setStatus(status2);
        history2.setTransaction(transaction1);

        history3 = new TransactionStatusHistory();
        history3.setId(UUID.randomUUID());
        history3.setStatus(status1);
        history3.setTransaction(transaction2);
    }

    @Test
    public void shouldReturnAllHistory_whenExist() {
        int page = 1;
        int size = 10;
        List<TransactionStatusHistory> historyList = List.of(history1, history2, history3);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionStatusHistory> historyPage = new PageImpl<>(historyList, pageable, historyList.size());

        Mockito.when(transactionStatusHistoryRepo.findByOrderByCreatedAtDesc(Mockito.any(Pageable.class))).thenReturn(historyPage);

        PaginatedResponseDto<TransactionStatusHistoryResponseDto> result = transactionStatusHistoryService.getAll(page, size);

        Assertions.assertEquals(3, result.getTotal());
        Assertions.assertEquals(3, result.getData().size());
        Assertions.assertEquals("TRX001", result.getData().getFirst().getTransactionCode());
        Assertions.assertEquals("In Process", result.getData().getFirst().getStatusName());

        Mockito.verify(transactionStatusHistoryRepo, Mockito.atLeast(1)).findByOrderByCreatedAtDesc(Mockito.any(Pageable.class));
    }

    @Test
    public void shouldReturnAllGatewayHistory_whenExist() {
        int page = 1;
        int size = 10;
        List<TransactionStatusHistory> gatewayHistoryList = List.of(history1, history2);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionStatusHistory> historyPage = new PageImpl<>(gatewayHistoryList, pageable, gatewayHistoryList.size());

        Mockito.when(principalService.getPrincipal()).thenReturn(authPojo);
        Mockito.when(gatewayUserRepo.findByUserId(Mockito.any())).thenReturn(Optional.of(gatewayUser));
        Mockito.when(transactionStatusHistoryRepo.findAllByTransaction_GatewayIdOrderByCreatedAtDesc(Mockito.any(UUID.class), Mockito.any(Pageable.class))).thenReturn(historyPage);

        PaginatedResponseDto<TransactionStatusHistoryResponseDto> result = transactionStatusHistoryService.getAllByGatewayId(page, size);
        Assertions.assertEquals(2, result.getTotal());
        Assertions.assertEquals(2, result.getData().size());
        Assertions.assertEquals("In Process", result.getData().get(0).getStatusName());
        Assertions.assertEquals("Paid", result.getData().get(1).getStatusName());

        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(gatewayUserRepo, Mockito.atLeast(1)).findByUserId(Mockito.any());
        Mockito.verify(transactionStatusHistoryRepo, Mockito.atLeast(1))
                .findAllByTransaction_GatewayIdOrderByCreatedAtDesc(Mockito.eq(gateway1.getId()), Mockito.any(Pageable.class));
    }

}