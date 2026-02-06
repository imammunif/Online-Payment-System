package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.product.CreateProductRequestDto;
import com.dansmultipro.ops.dto.product.UpdateProductRequestDto;
import com.dansmultipro.ops.model.Product;
import com.dansmultipro.ops.pojo.AuthorizationPoJo;
import com.dansmultipro.ops.repository.ProductRepo;
import com.dansmultipro.ops.service.impl.ProductServiceImpl;
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
public class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    protected PrincipalService principalService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void shouldCreated_whenDataValid() {
        productService.setPrincipalService(principalService);
        var authPojo = new AuthorizationPoJo(UUID.randomUUID());

        var dto = new CreateProductRequestDto();
        dto.setName("PRODUCT");
        dto.setCode("P001");

        var productSaved = new Product();
        var id = UUID.randomUUID();
        productSaved.setId(id);

        Mockito.when(principalService.getPrincipal()).thenReturn(authPojo);
        Mockito.when(productRepo.save(Mockito.any())).thenReturn(productSaved);

        var result = productService.create(dto);

        Assertions.assertEquals(id, result.getId());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(productRepo, Mockito.atLeast(1)).save(Mockito.any());
    }

    @Test
    public void shouldReturnData_whenIdValid() {
        var id = UUID.randomUUID();
        var productSaved = new Product();
        productSaved.setId(id);
        productSaved.setVersion(0);
        productSaved.setCode("P001");

        Mockito.when(productRepo.findById(Mockito.any())).thenReturn(Optional.of(productSaved));

        var result = productService.getById(id.toString());

        Assertions.assertEquals("P001", result.getCode());
        Mockito.verify(productRepo, Mockito.atLeast(1)).findById(id);
    }

    @Test
    public void shouldUpdateData_whenVersionValid() {
        productService.setPrincipalService(principalService);
        var authPojo = new AuthorizationPoJo(UUID.randomUUID());

        var id = UUID.randomUUID();
        var product = new Product();
        product.setId(id);
        product.setVersion(0);
        product.setCode("P001");

        var dto = new UpdateProductRequestDto();
        dto.setName("PRODUCT");
        dto.setVersion(0);

        var updatedProduct = new Product();
        updatedProduct.setVersion(1);

        Mockito.when(principalService.getPrincipal()).thenReturn(authPojo);
        Mockito.when(productRepo.findById(Mockito.any())).thenReturn(Optional.of(product));
        Mockito.when(productRepo.saveAndFlush(Mockito.any())).thenReturn(updatedProduct);

        var result = productService.update(id.toString(), dto);

        Assertions.assertEquals(1, result.getVersion());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(productRepo, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(productRepo, Mockito.atLeast(1)).saveAndFlush(Mockito.any());
    }

    @Test
    public void shouldReturnAll_whenExist() {
        List<Product> productList = new ArrayList<>();

        var product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setVersion(0);
        product1.setCode("P001");
        var product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setVersion(0);
        product2.setCode("P002");

        productList.add(product1);
        productList.add(product2);

        Mockito.when(productRepo.findAll()).thenReturn(productList);

        var result = productService.getAll();

        Assertions.assertEquals(productList.size(), result.size());
        Assertions.assertEquals("P001", result.getFirst().getCode());

        Mockito.verify(productRepo, Mockito.atLeast(1)).findAll();
    }

}
