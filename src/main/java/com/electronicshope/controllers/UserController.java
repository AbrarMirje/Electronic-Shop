package com.electronicshope.controllers;

import com.electronicshope.dtos.ApiResponseMessage;
import com.electronicshope.dtos.ImageResponse;
import com.electronicshope.dtos.PageableResponse;
import com.electronicshope.dtos.UserDto;
import com.electronicshope.services.impl.FileService;
import com.electronicshope.services.impl.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
//@AllArgsConstructor

// Use @RequiredArgsConstructor, bcoz in this code I use String property so when I use @AllArgsConstructor, it tries to autowired all of those properties.
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    // Create
    @PostMapping("/create-user")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto){
        UserDto user = userService.createUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // Update
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserDto userDto
    ){
        UserDto updateUser = userService.updateUser(userDto, userId);
        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    // Delete
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        ApiResponseMessage userDeletedSuccessfully = ApiResponseMessage.builder()
                .message("User deleted successfully")
                .isSuccess(true)
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(userDeletedSuccessfully,HttpStatus.OK);

    }

    // Get all
    @GetMapping("/get-all-users")
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        return new ResponseEntity<>(userService.getAllUser(pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);
    }

    // Get single
    @GetMapping("/get-single-user/{userId}")
    public ResponseEntity<UserDto> getSingleUser(@PathVariable String userId){
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.FOUND);
    }
    // Get by email
    @GetMapping("/get-user-by-email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email){
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.FOUND);
    }

    // Search user
    @GetMapping("/get-user-by-keyword/{keyword}")
    public ResponseEntity<List<UserDto>> getUserByKeyword(@PathVariable String keyword){
        return new ResponseEntity<>(userService.searchUser(keyword),HttpStatus.FOUND);
    }

    // Upload User Image
    @PostMapping("/image/{id}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @RequestParam MultipartFile userImage,
            @PathVariable String id
            ) throws IOException {

        String imageName = fileService.uploadFile(userImage, imageUploadPath);

        // We have to update image name with respective user
        UserDto userDto = userService.getUserById(id);
        userDto.setImageName(imageName);
        UserDto user = userService.updateUser(userDto, id);


        // To update image into the database also
        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .isSuccess(true)
                .status(HttpStatus.CREATED)
                .build();

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }


    // Serve image to user
    @GetMapping("/image/{userId}")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse servletResponse) throws IOException {
        //
        UserDto userById = userService.getUserById(userId);
        InputStream resource = fileService.getResource(imageUploadPath, userById.getImageName());

        servletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, servletResponse.getOutputStream());
    }
}
