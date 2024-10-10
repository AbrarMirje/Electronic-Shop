package com.electronicshope.services.impl;

import com.electronicshope.dtos.PageableResponse;
import com.electronicshope.dtos.ProductDto;
import com.electronicshope.entities.Category;
import com.electronicshope.entities.Product;
import com.electronicshope.exceptions.ResourceNotFoundException;
import com.electronicshope.helpers.Helper;
import com.electronicshope.repositories.CategoryRepository;
import com.electronicshope.repositories.IProductRepository;
import com.electronicshope.services.ICategoryService;
import com.electronicshope.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final IProductRepository productRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    @Value("${product.image.path}")
    private String imagePath;
    @Override
    public ProductDto createProduct(ProductDto productDto, MultipartFile image) throws IOException {
        String uploadFile = fileService.uploadFile(image, imagePath);
        productDto.setProductImage(uploadFile);

        Product product =modelMapper.map(productDto, Product.class);
        product.setAddedDate(LocalDate.now());
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Product save = null;
        if (product.isInStock()){
            product.setTitle(productDto.getTitle());
//            product.setDescription(productDto.setDescription());
            product.setPrice(productDto.getPrice());
            product.setDiscountedPrice(productDto.getDiscountedPrice());
            product.setQuantity(productDto.getQuantity());
            product.setLive(productDto.isLive());
            product.setInStock(productDto.isInStock());
            product.setProductImage(productDto.getProductImage());
            save = productRepository.save(product);
        }

        return modelMapper.map(save, ProductDto.class);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        String fullPath = imagePath + product.getProductImage();

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        productRepository.delete(product);

    }

    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findAll(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByLiveTrue(pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public ProductDto get(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> searchByTitle(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByTitleContaining(keyword, pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public ProductDto createProductWithCategory(ProductDto productDto, Long categoryId, MultipartFile file) throws IOException {

        // Check whether category is exists or not
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category is not found"));



        // uploading image if provided
        if (file != null && !file.isEmpty()){
            String uploadFile = fileService.uploadFile(file, imagePath);
            productDto.setProductImage(uploadFile);
        }


        Product productMap = modelMapper.map(productDto, Product.class);

        productMap.setCategory(category);

        Product saveProduct = productRepository.save(productMap);

        return modelMapper.map(saveProduct, ProductDto.class);
    }

    @Override
    public ProductDto updateCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setCategory(category);
        Product save = productRepository.save(product);

        return modelMapper.map(save, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getAllProducts(
            Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir
    ) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> page = productRepository.findByCategory(category, pageable);

        return Helper.getPageableResponse(page, ProductDto.class);
    }
}
