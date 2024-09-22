package com.electronicshope.services.impl;

import com.electronicshope.dtos.PageableResponse;
import com.electronicshope.dtos.UserDto;
import com.electronicshope.entities.User;
import com.electronicshope.exceptions.ResourceNotFoundException;
import com.electronicshope.repositories.UserRepository;
import com.electronicshope.services.IUserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
//@AllArgsConstructor
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Value("${user.profile.image.path}")
    private String imagePath;

    @Override
    public UserDto createUser(UserDto user) {

        // Generating unique id in string format
        String userId = UUID.randomUUID().toString();
        user.setId(userId);

        // DTO to Entity conversion
//        UserDto entityUser = mapper.map(user, UserDto.class); //--> This is the best and valid way.
        User entityUser = dtoToEntity(user);


        User savedUser = userRepository.save(entityUser);



        // Entity to DTO Conversion
//        User savedUser = mapper.map(entityUser, User.class); //--> This is the best and valid way.
        return entityToDto(savedUser);
    }

    private UserDto entityToDto(User savedUser) {
/*
        return UserDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .password(savedUser.getPassword())
                .about(savedUser.getAbout())
                .gender(savedUser.getGender())
                .imageName((savedUser.getImageName()))
                .build();
*/

        return mapper.map(savedUser, UserDto.class);
    }

    private User dtoToEntity(UserDto user) {

/*
        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .about(user.getAbout())
                .gender(user.getGender())
                .imageName((user.getImageName()))
                .build();
*/

        return mapper.map(user, User.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User is not available with this id"));
        user.setName(userDto.getName());
        // email update (not necessary this time)
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        user.setPassword(userDto.getPassword());
        user.setImageName(userDto.getImageName());

        // Save data
        User updatedUser = userRepository.save(user);
        return entityToDto(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User is not available with this id"));

        // deleting user image also while deleting the user
        // images/user/abc.png
        String fullPath = imagePath + user.getImageName();

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException e){
            System.out.println("User don't have an image");
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto>getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()): (Sort.by(sortBy).descending());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> page = userRepository.findAll(pageable);

        List<User> users = page.getContent();

        List<UserDto> userDtos = users.stream()
//                .map((user)->entityToDto(user))
                .map(this::entityToDto)
                .collect(Collectors.toList());

        PageableResponse<UserDto> response = new PageableResponse<>();
        response.setContent(userDtos);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setIsLastPage(page.isLast());

        return response;
    }

    @Override
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User is not available with this id"));
        return entityToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User byEmail = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found with this email"));
        return entityToDto(byEmail);
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> byNameContaining = userRepository.findByNameContaining(keyword);
        return byNameContaining.stream()
//                .map((user) -> entityToDto(user))
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}
