package com.electronicshope.services;

import com.electronicshope.dtos.CategoryDto;
import com.electronicshope.dtos.PageableResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ICategoryService {
    // Create category
    CategoryDto createCategory(CategoryDto categoryDto, MultipartFile image);

    // Update category
    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);

    // Delete category
    void deleteCategory(Long categoryId);

    // Get all category
    PageableResponse<CategoryDto> getAll(int PageNumber, int pageSize, String sortBy, String sortDir);

    // Get single category
    CategoryDto get(Long categoryId);

    // Search category
    PageableResponse<CategoryDto> search(String keyword, int PageNumber, int pageSize, String sortBy, String sortDir);
}
