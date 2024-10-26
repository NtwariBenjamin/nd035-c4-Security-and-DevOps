package com.example.demo.controllers;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.example.demo.service.HashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;



@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final Logger log= LoggerFactory.getLogger(UserController.class);
	private  HashService hashService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;


	public UserController(){}


	@GetMapping
	public ResponseEntity<List<User>> allUsers(){
		return ResponseEntity.of(Optional.of(userRepository.findAll()));
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		log.info("Username created with: ",createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if (createUserRequest.getPassword().length()<7||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			return ResponseEntity.badRequest().build();
		}
//		SecureRandom random=new SecureRandom();
//		byte[] salt=new byte[16];
//		random.nextBytes(salt);
//		String encodedSalt= Base64.getEncoder().encodeToString(salt);
//		String hashedPassword=hashService.getHashedValue(createUserRequest.getPassword(),encodedSalt);
//		user.setSalt(encodedSalt);
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		log.info("Password Before Encoding: ",createUserRequest.getPassword());
		log.info("The Encoded Password: ",bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		return ResponseEntity.ok(user);
	}
	
}
