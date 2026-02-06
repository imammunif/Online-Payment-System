package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.CreateResponseDto;
import com.dansmultipro.ops.dto.DeleteResponseDto;
import com.dansmultipro.ops.dto.UpdateResponseDto;
import com.dansmultipro.ops.dto.product.CreateProductRequestDto;
import com.dansmultipro.ops.dto.product.ProductResponseDto;
import com.dansmultipro.ops.dto.product.UpdateProductRequestDto;
import com.dansmultipro.ops.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'GA', 'CUST')")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<ProductResponseDto> res = productService.getAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('SA', 'GA', 'CUST')")
    public ResponseEntity<ProductResponseDto> getById(
            @PathVariable String id
    ) {
        ProductResponseDto res = productService.getById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<CreateResponseDto> create(
            @RequestBody @Valid CreateProductRequestDto data
    ) {
        CreateResponseDto res = productService.create(data);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<UpdateResponseDto> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateProductRequestDto data
    ) {
        UpdateResponseDto res = productService.update(id, data);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<DeleteResponseDto> delete(
            @PathVariable String id
    ) {
        DeleteResponseDto res = productService.deleteById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
