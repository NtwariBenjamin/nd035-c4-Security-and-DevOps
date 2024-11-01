package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    @Autowired
    private CartController cartController;
    private UserRepository userRepository=mock(UserRepository.class);
    private CartRepository cartRepository=mock(CartRepository.class);
    private ItemRepository itemRepository=mock(ItemRepository.class);

    @Before
    public void setUp(){
        cartController = new CartController(null, null, null);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);
        when(userRepository.findByUsername("test")).thenReturn(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Square Widget");
        BigDecimal price = BigDecimal.valueOf(1.99);
        item.setPrice(price);
        item.setDescription("A widget that is Square");
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
    }

    @Test
    public void addToCart_happyPath(){
        ModifyCartRequest request=new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");
        ResponseEntity<Cart> response=cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart=response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(1.99),cart.getTotal());
    }

    @Test
    public void invalidUser_toCart(){
        ModifyCartRequest request=new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("Rurangwa");
        ResponseEntity<Cart> response=cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void invalidItem_toCart(){
        ModifyCartRequest request=new ModifyCartRequest();
        request.setItemId(2L);
        request.setQuantity(1);
        request.setUsername("test");
        ResponseEntity<Cart> response=cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void removeFromCart_happyPath(){
        ModifyCartRequest request=new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(2);
        request.setUsername("test");
        ResponseEntity<Cart> response=cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        request=new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");
        response=cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart=response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(1.99),cart.getTotal());
    }

    @Test
    public void removeFromCart_InvalidUser(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("Rurangwa");
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
    @Test
    public void removeFromCart_invalidItem() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(2L);
        request.setQuantity(1);
        request.setUsername("test");
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
