package com.electronicshope.services.impl;

import com.electronicshope.dtos.CategoryDto;
import com.electronicshope.dtos.PageableResponse;
import com.electronicshope.entities.Category;
import com.electronicshope.exceptions.ResourceNotFoundException;
import com.electronicshope.helpers.Helper;
import com.electronicshope.repositories.CategoryRepository;
import com.electronicshope.services.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor

public class    CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    private final ModelMapper mapper;

    @Value("${category.image.path}")
    private String imagePath;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto, MultipartFile image) {

        String originalFilename = image.getOriginalFilename();
//        String fileName = UUID.randomUUID().toString();
        String uploadFile = null;
        try {
            uploadFile = fileService.uploadFile(image, imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String filePath = imagePath + uploadFile;
        categoryDto.setCoverImage(filePath);


        File folder = new File(imagePath);
        if (!folder.exists()) {
            // Create a folder
            folder.mkdirs();
        }

        Category category = mapper.map(categoryDto, Category.class);
        Category save = categoryRepository.save(category);
        return mapper.map(save, CategoryDto.class);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        // Finding category by its id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Updating the category
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());

        Category save = categoryRepository.save(category);
        return mapper.map(save, CategoryDto.class);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String fullPath = imagePath + category.getCoverImage();

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        categoryRepository.delete(category);
    }

    @Override
    public PageableResponse<CategoryDto> getAll(int PageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(PageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);
        return Helper.getPageableResponse(page, CategoryDto.class);
    }

    @Override
    public CategoryDto get(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return mapper.map(category, CategoryDto.class);
    }

    @Override
    public PageableResponse<CategoryDto> search(String keyword, int PageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(PageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findAllByTitleContaining(keyword, pageable);
        return Helper.getPageableResponse(page, CategoryDto.class);
    }
}
