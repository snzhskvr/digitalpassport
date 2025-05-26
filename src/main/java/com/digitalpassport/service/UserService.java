package com.digitalpassport.service;

import com.digitalpassport.entity.User;
import com.digitalpassport.exception.persistence.AttemptToUpdateIdException;
import com.digitalpassport.exception.persistence.UserAlreadyExistsException;
import com.digitalpassport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User newUser) {
        User user = userRepository.findById(newUser.getUsername()).orElse(null);
        if (user != null) {
            throw new UserAlreadyExistsException();
        }
        return userRepository.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUser(username);
    }

    public User getUser(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user with username=" + username));
    }

    public User updateUser(String username, User newUser) {
        if (newUser.getUsername() != null && !username.equals(newUser.getUsername())) {
            throw new AttemptToUpdateIdException();
        }

        return userRepository.findById(username).map(user -> {
            newUser.setUsername(user.getUsername());
            return userRepository.save(newUser);
        }).orElseThrow(() -> new UsernameNotFoundException("Could not find user with username=" + username));
    }

    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    public List<User> getUserPage(int index, int size) {
        return userRepository.findAll(PageRequest.of(index, size, Sort.by("username"))).getContent();
    }
}
