package com.damian.hotelbooking;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.entity.UserRole;
import com.damian.hotelbooking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;

@SpringBootApplication
public class HotelbookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelbookingApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(UserRepository userRepository) {
//		return args -> {
//			User user = userRepository.findByUsername("Alexu")
//					.orElseThrow(() -> new UsernameNotFoundException("User not found"));
//		};
//	}

}
