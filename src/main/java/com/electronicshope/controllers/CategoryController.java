package com.electronicshope.controllers;

import com.electronicshope.dtos.ApiResponseMessage;
import com.electronicshope.dtos.CategoryDto;
import com.electronicshope.dtos.PageableResponse;
import com.electronicshope.dtos.ProductDto;
import com.electronicshope.services.impl.CategoryService;
import com.electronicshope.services.impl.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.annotation.Repeatable;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    // Create
    @PostMapping(value = "/create-category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(
            @RequestPart("category") String categoryDtoJson,
            @RequestParam("image") MultipartFile image
    ) throws JsonMappingException, JsonProcessingException {
        try {
            // Deserialize the JSON string into CategoryDto
            CategoryDto categoryDto = new ObjectMapper().readValue(categoryDtoJson, CategoryDto.class);

            // Pass the deserialized object and image to the service layer
            CategoryDto createdCategory = categoryService.createCategory(categoryDto, image);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            // Handle any other errors that might occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Update
    @PutMapping("/update-category/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @RequestBody CategoryDto categoryDto,
            @PathVariable Long categoryId){
        CategoryDto updateCategory = categoryService.updateCategory(categoryDto, categoryId);
        return new ResponseEntity<>(updateCategory, HttpStatus.OK);
    }
    // Delete
    @DeleteMapping("/delete-mapping/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteMapping(@PathVariable Long categoryId){
        categoryService.deleteCategory(categoryId);
        ApiResponseMessage categoryDeletedSuccessfully = ApiResponseMessage.builder()
                .message("Category deleted successfully")
                .isSuccess(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(categoryDeletedSuccessfully,HttpStatus.OK);
    }
    // Get all
    @GetMapping("/getAllCategories")
    public ResponseEntity<PageableResponse<CategoryDto>> getAllCategories(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        return new ResponseEntity<>(categoryService.getAll(pageNumber, pageSize, sortBy, sortDir),HttpStatus.FOUND);
    }
    // Get
    @GetMapping("/getCategory/{categoryId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long categoryId){
        CategoryDto categoryDto = categoryService.get(categoryId);
        return new ResponseEntity<>(categoryDto, HttpStatus.FOUND);
    }

    // Search
    @GetMapping("/searchCategory")
    public ResponseEntity<PageableResponse<CategoryDto>> searchCategory(
            @RequestParam String keyword,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        return new ResponseEntity<>(categoryService.search(keyword, pageNumber, pageSize, sortBy, sortDir), HttpStatus.FOUND);
    }


    // create product with category
    @PostMapping("/{categoryId}/products")
    public ResponseEntity<ProductDto> createProductWithCategory(
            @PathVariable Long categoryId,
            @RequestBody ProductDto productDto,
            @RequestPart("image") MultipartFile file
    ) throws IOException {
        ProductDto productWithCategory = productService.createProductWithCategory(productDto, categoryId, file);
        return new ResponseEntity<>(productWithCategory, HttpStatus.CREATED);
    }

    // update category of product
    @PutMapping("{categoryId}/products/{productId}")
    public ResponseEntity<ProductDto> updateCategoryOfProduct(
            @PathVariable Long categoryId,
            @PathVariable Long productId
    ){
        ProductDto updateCategory = productService.updateCategory(productId, categoryId);
        return new ResponseEntity<>(updateCategory, HttpStatus.OK);
    }

    @GetMapping("{categoryId}/products")
    public ResponseEntity<PageableResponse<ProductDto>> getProductOfCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        PageableResponse<ProductDto> allProducts = productService.getAllProducts(
                categoryId, pageNumber, pageSize, sortBy, sortDir
        );
        return new ResponseEntity<>(allProducts, HttpStatus.FOUND);
    }
}
