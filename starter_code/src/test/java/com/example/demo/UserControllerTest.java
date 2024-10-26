package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@SpringBootTest
public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepo= mock(UserRepository.class);
    private CartRepository cartRepo=mock(CartRepository.class);
    private BCryptPasswordEncoder encoder=mock(BCryptPasswordEncoder.class);
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp(){
        userController=new UserController();
        TestUtils.injectObjects(userController,"userRepository",userRepo);
        TestUtils.injectObjects(userController,"cartRepository",cartRepo);
        TestUtils.injectObjects(userController,"bCryptPasswordEncoder",encoder);

        User testUser = new User(1L, "testUsername", "password");
        when(userRepo.findByUsername("testUsername")).thenReturn(testUser);
    }

    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request=new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");
        final ResponseEntity<User> user = userController.createUser(request);
        assertNotNull(user);
        assertEquals(200,user.getStatusCodeValue());
        User u=user.getBody();
        assertNotNull(u);
        assertEquals(0,u.getId());
        assertEquals("testUser",u.getUsername());
        assertEquals("thisIsHashed",u.getPassword());
    }

    @Test
    public void test_findUserById() throws Exception {
     Long userId=1L;
     User testUser=new User();
     when(userRepo.findById(anyLong())).thenReturn(java.util.Optional.of(testUser));

         mockMvc.perform(get("/id/{id}",userId)
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk())
                 .andExpect(content().string("{\"id\":1}"));
    }
    @Test
    public void testFindByUserName() throws Exception {
        // Arrange
        String username = "testUser";

        // Act & Assert
        mockMvc.perform(get("/api/users/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }
}
