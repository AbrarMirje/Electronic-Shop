package com.electronicshope.services;

import com.electronicshope.dtos.PageableResponse;
import com.electronicshope.dtos.UserDto;

import java.util.List;

public interface IUserService {
    // Create User
    UserDto createUser(UserDto user);

    // Update User
    UserDto updateUser(UserDto userDto, String id);

    // Delete User
    void deleteUser(String id);

    // Get all users
    PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir);

    // Get single user by id
    UserDto getUserById(String id);

    // Get single user by email
    UserDto getUserByEmail(String email);

    // Search user
    List<UserDto> searchUser(String keyword);
}
