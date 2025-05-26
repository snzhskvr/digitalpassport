package com.digitalpassport.service;

import com.digitalpassport.BaseIntegrationTest;
import com.digitalpassport.entity.Role;
import com.digitalpassport.entity.User;
import com.digitalpassport.exception.persistence.AttemptToUpdateIdException;
import com.digitalpassport.exception.persistence.UserAlreadyExistsException;
import com.digitalpassport.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        User user = users.getFirst();
        Set<Role> roles = user.getRoles();
        assertEquals(1, users.size());
        assertEquals("admin", user.getUsername());
        assertEquals(3, roles.size());
    }

    @Test
    void testSaveUser() {
        User admin = new User();
        admin.setUsername("admin");
        assertThrows(UserAlreadyExistsException.class, () -> userService.saveUser(admin));

        User expected = getUser();
        userService.saveUser(expected);
        User actual = userRepository.findById(expected.getUsername()).orElseThrow();
        assertUsers(expected, actual);
    }

    @Test
    void testGetUser() {
        User expected = getUser();
        assertThrows(UsernameNotFoundException.class, () -> userService.getUser(expected.getUsername()));
        userRepository.save(expected);
        User actual = userService.getUser(expected.getUsername());
        assertUsers(expected, actual);
    }

    @Test
    void testUpdateUser() {
        User expected = getUser();
        userRepository.save(expected);
        assertThrows(AttemptToUpdateIdException.class, () -> userService.updateUser("new" + expected.getUsername(), expected));
        expected.setEnabled(false);
        User actual = userService.updateUser(expected.getUsername(), expected);
        assertUsers(expected, actual);
    }

    @Test
    void testDeleteUser() {
        User expected = getUser();
        userRepository.save(expected);
        long before = userRepository.findById(expected.getUsername()).stream().count();
        userService.deleteUser(expected.getUsername());
        long after = userRepository.findById(expected.getUsername()).stream().count();
        assertTrue(before > after);
    }

    @Test
    void testGetUserPage() {
        User expected = getUser();
        userRepository.save(expected);
        List<User> firstPage = userService.getUserPage(0, 2);
        assertEquals(2, firstPage.size());
        List<User> secondPage = userService.getUserPage(1, 2);
        assertEquals(0, secondPage.size());
    }

    private User getUser() {
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user"));
        user.setRoles(new HashSet<>(roleService.getAllRoles()));
        return user;
    }

    private void assertUsers(User expected, User actual) {
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getRoles(), actual.getRoles());
        assertEquals(expected.isAccountNonExpired(), actual.isAccountNonExpired());
        assertEquals(expected.isAccountNonLocked(), actual.isAccountNonLocked());
        assertEquals(expected.isCredentialsNonExpired(), actual.isCredentialsNonExpired());
        assertEquals(expected.isEnabled(), actual.isEnabled());
    }
}
