package com.dansmultipro.ops.service;

import com.dansmultipro.ops.model.Gateway;
import com.dansmultipro.ops.repository.GatewayRepo;
import com.dansmultipro.ops.service.impl.GatewayServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class GatewayServiceTest {

    @Mock
    private GatewayRepo gatewayRepo;

    @InjectMocks
    private GatewayServiceImpl gatewayService;

    @Test
    public void shouldReturnData_whenIdValid() {
        var id = UUID.randomUUID();
        var gatewaySaved = new Gateway();
        gatewaySaved.setId(id);
        gatewaySaved.setVersion(0);
        gatewaySaved.setName("GATEWAY");
        gatewaySaved.setCode("GT01");

        Mockito.when(gatewayRepo.findById(Mockito.any())).thenReturn(Optional.of(gatewaySaved));

        var result = gatewayService.getById(id.toString());

        Assertions.assertEquals("GT01", result.getCode());
        Mockito.verify(gatewayRepo, Mockito.atLeast(1)).findById(id);
    }

    @Test
    public void shouldReturnAll_whenExist() {
        List<Gateway> gatewayList = new ArrayList<>();

        var gateway1 = new Gateway();
        gateway1.setId(UUID.randomUUID());
        gateway1.setVersion(0);
        gateway1.setName("GATEWAY1");
        gateway1.setCode("GT01");
        var gateway2 = new Gateway();
        gateway2.setId(UUID.randomUUID());
        gateway2.setVersion(0);
        gateway2.setName("GATEWAY2");
        gateway2.setCode("GT02");

        gatewayList.add(gateway1);
        gatewayList.add(gateway2);

        Mockito.when(gatewayRepo.findAll()).thenReturn(gatewayList);

        var result = gatewayService.getAll();

        Assertions.assertEquals(gatewayList.size(), result.size());
        Assertions.assertEquals("GT01", result.getFirst().getCode());

        Mockito.verify(gatewayRepo, Mockito.atLeast(1)).findAll();
    }

}
