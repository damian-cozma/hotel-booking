package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.ProfileDto;
import com.damian.hotelbooking.dto.SignupDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.repository.UserRepository;
import com.damian.hotelbooking.entity.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
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
    public User registerUser(@Valid SignupDto signupDto, BindingResult bindingResult) {

        if (existsByEmail(signupDto.getEmail())) {
            bindingResult.rejectValue("email", "error.signupDto", "Email already registered");
        }

        if (existsByUsername(signupDto.getUsername())) {
            bindingResult.rejectValue("username", "error.signupDto", "Username already taken");
        }

        if (existsByPhoneNumber(signupDto.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "error.signupDto", "Phone number already in use");
        }

        if (bindingResult.hasErrors()) {
            return null;
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
    public User saveProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto, Principal principal,
                            BindingResult bindingResult, Model model) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));

        if (!user.getPhoneNumber().equals(profileDto.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(profileDto.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "error.profileDto", "Phone number already in use");
        }

        if (!user.getEmail().equals(profileDto.getEmail()) &&
                userRepository.existsByEmail(profileDto.getEmail())) {
            bindingResult.rejectValue("email", "error.profileDto", "Email already registered");
        }

        if (bindingResult.hasErrors()) {
            return null;
        }

        user.setFirstName(profileDto.getFirstName());
        user.setLastName(profileDto.getLastName());
        user.setEmail(profileDto.getEmail());
        user.setPhoneNumber(profileDto.getPhoneNumber());

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
    public boolean changePassword(Principal principal, String currentPassword, String newPassword,
                                  String confirmPassword, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("activeSection", "security");
            return false;
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found - " + principal.getName()));

        if (!newPassword.equals(confirmPassword)) {
            bindingResult.rejectValue("confirmPassword", "error.passwordDto", "New passwords don't match");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String currentEncodedPassword = user.getPassword().replace("{bcrypt}", "");
        if (!passwordEncoder.matches(currentPassword, currentEncodedPassword)) {
            bindingResult.rejectValue("currentPassword", "error.passwordDto", "Current password is incorrect");
            return false;
        }

        user.setPassword("{bcrypt}" + passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public void deleteAccount(Principal principal, HttpServletRequest request) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found - " + principal.getName()));

        Long userId = user.getId();

        userRepository.deleteById(userId);

        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
