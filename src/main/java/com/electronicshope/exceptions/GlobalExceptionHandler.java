package com.electronicshope.exceptions;

import com.electronicshope.dtos.ApiResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle resource not found exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> resourceNotFoundExceptionHandler(ResourceNotFoundException e){
        ApiResponseMessage apiResponse = ApiResponseMessage.builder()
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .isSuccess(true)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }


    // MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        Map<String, Object> response = new HashMap<>();

        allErrors.forEach((objectError -> {
            String message = objectError.getDefaultMessage();
            String field = ((FieldError) objectError).getField();

            response.put(field, message);
        }));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
