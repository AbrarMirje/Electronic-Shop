package com.electronicshope.dtos;

import com.electronicshope.validate.ImageNameValidate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String id;

    @Size(min = 3, max = 20, message = "Invalid name!!")
    private String name;

//    @Email(message = "Invalid email!!")
    @Pattern(regexp = "^[a-z0-9][-a-z0-9._]+@([-a-z0-9]+\\.)+[a-z]{2,5}$", message = "*---Please enter valid email---*")
    @NotBlank(message = "Email is required!!")
    private String email;

    @NotBlank(message = "Password must be required!!")
    private String password;

    @Size(min = 4, max = 6, message = "Invalid gender!!")
    private String gender;

    @NotBlank(message = "Write something about yourself!!")
    private String about;

    @ImageNameValidate
    private String imageName;
}
