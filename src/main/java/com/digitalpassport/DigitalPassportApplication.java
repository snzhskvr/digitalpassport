package com.digitalpassport;

import com.digitalpassport.entity.Role;
import com.digitalpassport.entity.User;
import com.digitalpassport.service.RoleService;
import com.digitalpassport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class DigitalPassportApplication implements CommandLineRunner {

	private final UserService userService;
	private final RoleService roleService;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(DigitalPassportApplication.class, args);
	}

	@Override
	public void run(String... args) {
		initAdmin();
	}

	private void initAdmin() {
		String username = "admin";
		try {
			userService.getUser(username);
		} catch (UsernameNotFoundException e) {
			List<Role> allRoles = roleService.getAllRoles();
			User adminUser = new User();
			adminUser.setUsername(username);
			adminUser.setPassword(passwordEncoder.encode(StringUtils.defaultIfBlank(System.getenv("DIGITALPASSPORT_ADMIN_PASSWORD"), username)));
			adminUser.setRoles(new HashSet<>(allRoles));
			userService.saveUser(adminUser);
		}
	}
}
