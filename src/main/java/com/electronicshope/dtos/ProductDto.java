package com.electronicshope.dtos;

import com.electronicshope.entities.Category;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
    private Long productId;
    private String title;
    private String description;
    private double price;
    private double discountedPrice;
    private int quantity;
    private LocalDate addedDate;
    private boolean isLive;
    private boolean inStock;
    private String productImage;
    private CategoryDto category;
}
