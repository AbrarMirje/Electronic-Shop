package com.electronicshope.controllers;

import com.electronicshope.dtos.ApiResponseMessage;
import com.electronicshope.dtos.PageableResponse;
import com.electronicshope.dtos.ProductDto;
import com.electronicshope.services.impl.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("product")
public class ProductController {
    private final ProductService productService;

    @PostMapping(value = "/add-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestPart("product") String productDto,
            @RequestPart("image") MultipartFile image
            ){

        System.out.println(productDto);
        try {
            ProductDto productDto1 = new ObjectMapper().readValue(productDto, ProductDto.class);
            ProductDto product = productService.createProduct(productDto1, image);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping("/get-product/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId){
        ProductDto product = productService.get(productId);
        return new ResponseEntity<>(product, HttpStatus.FOUND);
    }


    @GetMapping("/get-products")
    public ResponseEntity<PageableResponse<ProductDto>> getProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){

        PageableResponse<ProductDto> allProducts = productService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allProducts, HttpStatus.FOUND);
    }

    @PutMapping("/update-product/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @RequestBody ProductDto productDto,
            @PathVariable Long productId
    ){
        return new ResponseEntity<>(productService.updateProduct(productDto, productId), HttpStatus.OK);
    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
        ApiResponseMessage apiResponseMessage = ApiResponseMessage.builder()
                .message("Product deleted successfully")
                .isSuccess(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(apiResponseMessage, HttpStatus.OK);
    }

    @GetMapping("/get-live-products")
    public ResponseEntity<PageableResponse<ProductDto>> getLiveProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        PageableResponse<ProductDto> allLive = productService.getAllLive(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allLive, HttpStatus.FOUND);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<PageableResponse<ProductDto>> searchKeyword(
            @RequestParam String keyword,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        PageableResponse<ProductDto> searchByTitle = productService.searchByTitle(keyword, pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(searchByTitle, HttpStatus.FOUND);
    }
}
