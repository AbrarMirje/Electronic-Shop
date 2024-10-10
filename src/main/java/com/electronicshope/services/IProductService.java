package com.electronicshope.services;

import com.electronicshope.dtos.PageableResponse;
import com.electronicshope.dtos.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IProductService {

    // Create Product
    ProductDto createProduct(ProductDto productDto, MultipartFile image) throws IOException;

    // Update Product
    ProductDto updateProduct(ProductDto productDto, Long productId);

    // Delete Product
    void deleteProduct(Long productId);

    // Get all Product
    PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    // Get all live products
    PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir);

    // Get single Product
    ProductDto get(Long productId);

    // Search Product
    PageableResponse<ProductDto> searchByTitle(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir);

    // Create product with category
    ProductDto createProductWithCategory(ProductDto productDto, Long categoryId, MultipartFile file) throws IOException;

    // Update category of product
    ProductDto updateCategory(Long productId, Long categoryId);

    // Get products of given category
    PageableResponse<ProductDto> getAllProducts(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);
}
