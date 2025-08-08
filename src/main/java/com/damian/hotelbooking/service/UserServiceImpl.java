package com.damian.hotelbooking.service;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.repository.UserRepository;
import com.damian.hotelbooking.entity.UserRole;
import com.damian.hotelbooking.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllByOrderByLastNameAsc();
    }

    @Override
    public User findById(Long theId) {
        Optional<User> result = userRepository.findById(theId);

        User user = null;

        if (result.isPresent()) {
            user = result.get();
        }
        else {
            throw new RuntimeException("Did not find user id - " + theId);
        }

        return user;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long theId) {
        userRepository.deleteById(theId);
    }

    @Override
    public User registerUser(SignupRequest signupRequest) {

        if (existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email already registered: " + signupRequest.getEmail());
        }
        
        if (existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Username already taken: " + signupRequest.getUsername());
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = "{bcrypt}" + passwordEncoder.encode(signupRequest.getPassword());

        User user = new User(
            signupRequest.getUsername(),
            signupRequest.getFirstName(),
            signupRequest.getLastName(),
            signupRequest.getEmail(),
            encodedPassword,
            signupRequest.getPhoneNumber(),
            UserRole.ROLE_USER
        );

        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
