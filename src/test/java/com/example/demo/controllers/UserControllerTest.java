package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock (CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock (BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController,"userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }


    //Sanity check tests
    @Test
    public void createUserHappyPath(){
        when(bCryptPasswordEncoder.encode("testtest")).thenReturn("thisIsHashed" );
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("claudia");
        createUserRequest.setPassword("testtest");
        createUserRequest.setConfirmPassword("testtest");

        final ResponseEntity<User> userResponseEntity = userController.createUser(createUserRequest);
        assertNotNull(userResponseEntity);
        assertEquals(HttpStatus.OK, userResponseEntity.getStatusCode());
        User user = userResponseEntity.getBody();
       assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("claudia", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void testConfirmedPasswordMatchesPassword(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("claudia");
        createUserRequest.setPassword("testtest");
        createUserRequest.setConfirmPassword("testtest1");
        //check if the length matches the condition
        int i = 7;
        Assertions.assertTrue(createUserRequest.getPassword().length()>i);

        final ResponseEntity<User> userResponseEntity = userController.createUser(createUserRequest);
        //if length matches condition but the confirmed password doesn't match the original password then it should throw an error
        assertEquals(HttpStatus.BAD_REQUEST, userResponseEntity.getStatusCode());
    }

    @Test
    public void testPasswordLength(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("claudia");
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("test");

        //check if the length matches the condition
        int i = 7;
        assertFalse(createUserRequest.getPassword().length()>i);

        //check if confirmed password matches original password
        assertEquals(createUserRequest.getPassword(), createUserRequest.getConfirmPassword());

        //check if user is not created because length doesn't match condition
        final ResponseEntity<User> userResponseEntity = userController.createUser(createUserRequest);
        assertEquals(HttpStatus.BAD_REQUEST, userResponseEntity.getStatusCode());
    }

    @Test
    public void testFindByUsernameHappyPath(){
        User user = new User();
        user.setUsername("claudia");
        when(userRepository.findByUsername("claudia")).thenReturn(user);
        final ResponseEntity<User> userResponseEntity = userController.findByUserName("claudia");
        assertEquals(HttpStatus.OK, userResponseEntity.getStatusCode());
    }

    @Test
    public void testIfNotFoundUsername(){
        User user = new User();
        user.setUsername("claudia");
        when(userRepository.findByUsername("claudia")).thenReturn(user);
        final ResponseEntity<User> userResponseEntity = userController.findByUserName("julia");
        assertFalse(userResponseEntity.getStatusCode()== HttpStatus.OK);
        assertEquals(HttpStatus.NOT_FOUND, userResponseEntity.getStatusCode());
    }

    @Test
    public void testFindByIdHappyPath(){
        User user = new User();
        user.setId(0);
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(user));
        final ResponseEntity<User> userResponseEntity = userController.findById(0L);

        assertEquals(HttpStatus.OK, userResponseEntity.getStatusCode());

    }

    @Test
    public void testFindByIdNotFound(){
        User user = new User();
        user.setId(0);
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(user));
        final ResponseEntity<User> userResponseEntity = userController.findById(1L);
        assertFalse(userResponseEntity.getStatusCode()== HttpStatus.OK);
        assertEquals(HttpStatus.NOT_FOUND, userResponseEntity.getStatusCode());
    }

}
