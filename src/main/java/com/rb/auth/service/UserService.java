package com.rb.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rb.auth.dto.MessageResponse;
import com.rb.auth.dto.UserDto;
import com.rb.auth.entity.LoggedOutToken;
import com.rb.auth.entity.UserEntity;
import com.rb.auth.repository.LoggedOutTokenRepository;
import com.rb.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private LoggedOutTokenRepository loggedOutTokenRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private TokenService tokenService;

    public UserEntity saveUser(UserDto userDto){
        final UserEntity user = objectMapper.convertValue(userDto, UserEntity.class);
        user.setRole(userDto.getRole().toUpperCase());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(user);
    }

    public String loginUser(UserDto userDto){
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
        return tokenService.generateToken(authentication);
    }

    public void logOut(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        LoggedOutToken loggedOutToken = new LoggedOutToken();
        loggedOutToken.setToken(token);
        loggedOutTokenRepository.save(loggedOutToken);
    }

}
