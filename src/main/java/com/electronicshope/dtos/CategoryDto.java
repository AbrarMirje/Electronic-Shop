package com.electronicshope.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDto {
    private Long categoryId;

    @NotBlank
    @Min(value = 4, message = "title must be of minimum 4 characters")
    @JsonIgnore
    private String title;

    @NotBlank(message = "Description is required")
    @JsonIgnore
    private String description;

    @JsonIgnore
    private String coverImage;
}
