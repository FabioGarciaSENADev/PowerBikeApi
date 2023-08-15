package com.powerbike;

import com.powerbike.models.ERole;
import com.powerbike.models.RoleEntity;
import com.powerbike.models.UserEntity;
import com.powerbike.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class PowerBikeApiRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PowerBikeApiRestApplication.class, args);
	}

	/*@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;*/

	/*@Bean
	CommandLineRunner init(){
		return args -> {

			UserEntity userEntity = UserEntity.builder()
					.email("fabio@mail.com")
					.username("fabio")
					.password(passwordEncoder.encode("1234"))
					.roles(Set.of(RoleEntity.builder()
									.name(ERole.valueOf(ERole.ADMIN.name()))
									.build()))
					.build();

			UserEntity userEntity2 = UserEntity.builder()
					.email("jesus@mail.com")
					.username("jesus")
					.password(passwordEncoder.encode("1234"))
					.roles(Set.of(RoleEntity.builder()
							.name(ERole.valueOf(ERole.USER.name()))
							.build()))
					.build();

			UserEntity userEntity3 = UserEntity.builder()
					.email("marktin@mail.com")
					.username("marktin")
					.password(passwordEncoder.encode("1234"))
					.roles(Set.of(RoleEntity.builder()
							.name(ERole.valueOf(ERole.ACUDIENTE.name()))
							.build()))
					.build();

			userRepository.save(userEntity);
			userRepository.save(userEntity2);
			userRepository.save(userEntity3);

		};
	}*/

}
