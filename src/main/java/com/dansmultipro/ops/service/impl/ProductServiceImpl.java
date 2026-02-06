package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.dto.CreateResponseDto;
import com.dansmultipro.ops.dto.DeleteResponseDto;
import com.dansmultipro.ops.dto.UpdateResponseDto;
import com.dansmultipro.ops.dto.product.CreateProductRequestDto;
import com.dansmultipro.ops.dto.product.ProductResponseDto;
import com.dansmultipro.ops.dto.product.UpdateProductRequestDto;
import com.dansmultipro.ops.exception.MissMatchException;
import com.dansmultipro.ops.exception.NotFoundException;
import com.dansmultipro.ops.model.Product;
import com.dansmultipro.ops.repository.ProductRepo;
import com.dansmultipro.ops.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {

    private final ProductRepo productRepo;

    public ProductServiceImpl(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public List<ProductResponseDto> getAll() {
        List<ProductResponseDto> result = productRepo.findAll().stream()
                .map(v -> new ProductResponseDto(v.getId(), v.getName(), v.getCode(), v.getVersion().toString()))
                .toList();
        return result;
    }

    @Override
    public ProductResponseDto getById(String id) {
        Product product = productRepo.findById(UUID.fromString(id)).orElseThrow(
                () -> new NotFoundException("Product not found")
        );
        return new ProductResponseDto(product.getId(), product.getName(), product.getCode(), product.getVersion().toString());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public CreateResponseDto create(CreateProductRequestDto data) {
        Product newProduct = prepareForInsert(new Product());
        newProduct.setName(data.getName());
        newProduct.setCode(data.getCode());
        Product createdProduct = productRepo.save(newProduct);
        return new CreateResponseDto(createdProduct.getId(), "Saved");
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public UpdateResponseDto update(String id, UpdateProductRequestDto data) {
        Product product = productRepo.findById(UUID.fromString(id)).orElseThrow(
                () -> new NotFoundException("Product not found")
        );
        if (!product.getVersion().equals(data.getVersion())) {
            throw new MissMatchException("Version not match");
        }
        Product productUpdate = prepareForUpdate(product);
        productUpdate.setName(data.getName());
        Product updatedProduct = productRepo.saveAndFlush(productUpdate);
        return new UpdateResponseDto(updatedProduct.getVersion(), "Updated");
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public DeleteResponseDto deleteById(String id) {
        Product product = productRepo.findById(UUID.fromString(id)).orElseThrow(
                () -> new NotFoundException("Product not found")
        );
        productRepo.deleteById(product.getId());
        return new DeleteResponseDto("Deleted");
    }

}
