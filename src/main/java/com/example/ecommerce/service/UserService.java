package com.example.ecommerce.service;

import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.model.User;

public interface UserService {

    User register(RegisterRequest request);

    String login(LoginRequest request);
}
