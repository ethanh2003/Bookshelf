package com.example.bookshelf.Service;

import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.User;
import com.example.bookshelf.repository.UserRepository;
import com.example.bookshelf.Request.UserLoginRequest;
import com.example.bookshelf.Request.UserRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    public void registerUser(UserRegistrationRequest registrationRequest) {
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        User newUser = new User(registrationRequest.getUsername(), registrationRequest.getEmail(), encodedPassword);
        userRepository.save(newUser);
    }

    public boolean loginUser(@RequestBody UserLoginRequest loginRequest) {
        System.out.println("Login Request: " + loginRequest.getUsername() + ", " + loginRequest.getPassword());

        Optional<User> user = findByUsername(loginRequest.getUsername());
        return user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword());
    }

    @Transactional
    public Optional<User> findByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";

        try {
            User user = entityManager.createQuery(jpql, User.class)
                    .setParameter("username", username)
                    .setMaxResults(1)
                    .getSingleResult();
            return Optional.ofNullable(user);
        } catch (NoResultException ex) {
            System.out.println("User not found for username: " + username);
            return Optional.empty();
        }
    }



    public Long getUserIdByUsername(String username) {
        Optional<User> user = findByUsername(username);
        if (user.isPresent()) {
            return user.get().getId();
        } else {
            throw new RuntimeException("User not found");
        }
    }






    public List<Book> getUserBooks(String username) {
        Optional<User> userOptional = findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return BookService.getUserBooks(user.getId());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
