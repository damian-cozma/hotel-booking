package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.SignupDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.repository.UserRepository;
import com.damian.hotelbooking.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
        } else {
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
    public User registerUser(SignupDto signupDto) {

        if (existsByEmail(signupDto.getEmail())) {
            throw new RuntimeException("Email already registered: " + signupDto.getEmail());
        }

        if (existsByUsername(signupDto.getUsername())) {
            throw new RuntimeException("Username already taken: " + signupDto.getUsername());
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = "{bcrypt}" + passwordEncoder.encode(signupDto.getPassword());

        User user = new User(
                signupDto.getUsername(),
                signupDto.getFirstName(),
                signupDto.getLastName(),
                signupDto.getEmail(),
                encodedPassword,
                signupDto.getPhoneNumber(),
                UserRole.ROLE_USER
        );

        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User saveWithPasswordEncoding(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("{bcrypt}")) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword("{bcrypt}" + passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public boolean changePassword(User user, String currentPassword, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String currentEncodedPassword = user.getPassword().replace("{bcrypt}", "");
        if (!passwordEncoder.matches(currentPassword, currentEncodedPassword)) {
            return false;
        }

        user.setPassword("{bcrypt}" + passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}
