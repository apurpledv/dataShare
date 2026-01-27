package com.openclassrooms.dataShare_api.unit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.repository.UserRepository;

@Disabled
@SpringBootTest
class UserRepositoryTests {
	@Autowired
	private UserRepository userRepository;

    static final private PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:17"));

	@DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");

		postgres.start();
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

	@Test
	public void testUserRepository() {
		User user = new User("testUser", "testUser");

		// create
		User savedUser = userRepository.save(user);
		assertNotNull(savedUser);

		// read
		Optional<User> foundUser = userRepository.findById(savedUser.getId());
		assertTrue(foundUser.isPresent());
		assertTrue(user.getEmail().equals(foundUser.get().getEmail()));
		assertTrue(user.getPassword().equals(foundUser.get().getPassword()));

		// update
		savedUser.setEmail("testUserNewEmail");
		savedUser = userRepository.save(savedUser);
		assertNotNull(savedUser);
		assertTrue(savedUser.getEmail().equals("testUserNewEmail"));

		// delete
		userRepository.delete(savedUser);
		assertTrue(userRepository.findById(savedUser.getId()).isEmpty());
	}
}
