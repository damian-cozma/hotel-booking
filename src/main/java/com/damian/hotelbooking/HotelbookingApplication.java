package com.damian.hotelbooking;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.entity.UserRole;
import com.damian.hotelbooking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HotelbookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelbookingApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserRepository userRepository) {
		return (args) -> {
			// createUser(userRepository);
		};
	}

	private void createUser(UserRepository userRepository) {
		//userRepository.save(new User("Damian", "Damian", "Cozma", "dcozma.ro@gmail.com", "password123", "0741234567", UserRole.ROLE_USER));
	}

}
