package com.dansmultipro.ops.service;

import com.dansmultipro.ops.model.TransactionStatus;
import com.dansmultipro.ops.repository.TransactionStatusRepo;
import com.dansmultipro.ops.service.impl.TransactionStatusServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionStatusServiceTest {

    @Mock
    private TransactionStatusRepo transactionStatusRepo;

    @InjectMocks
    private TransactionStatusServiceImpl transactionStatus;

    @Test
    public void shouldReturnAllHistory_whenExist() {
        List<TransactionStatus> statusList = new ArrayList<>();

        var status1 = new TransactionStatus();
        status1.setId(UUID.randomUUID());
        status1.setVersion(0);
        status1.setName("In Process");
        status1.setCode("PROCESS");

        var status2 = new TransactionStatus();
        status2.setId(UUID.randomUUID());
        status2.setVersion(0);
        status2.setName("Paid");
        status2.setCode("PAY");

        statusList.add(status1);
        statusList.add(status2);

        Mockito.when(transactionStatusRepo.findAll()).thenReturn(statusList);

        var result = transactionStatus.getAll();

        Assertions.assertEquals(statusList.size(), result.size());
        Assertions.assertEquals("PROCESS", result.getFirst().getCode());

        Mockito.verify(transactionStatusRepo, Mockito.atLeast(1)).findAll();
    }

}
