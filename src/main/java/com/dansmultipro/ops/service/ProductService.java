package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.CreateResponseDto;
import com.dansmultipro.ops.dto.DeleteResponseDto;
import com.dansmultipro.ops.dto.UpdateResponseDto;
import com.dansmultipro.ops.dto.product.CreateProductRequestDto;
import com.dansmultipro.ops.dto.product.ProductResponseDto;
import com.dansmultipro.ops.dto.product.UpdateProductRequestDto;

import java.util.List;

public interface ProductService {

    List<ProductResponseDto> getAll();

    ProductResponseDto getById(String id);

    CreateResponseDto create(CreateProductRequestDto data);

    UpdateResponseDto update(String id, UpdateProductRequestDto data);

    DeleteResponseDto deleteById(String id);

}
