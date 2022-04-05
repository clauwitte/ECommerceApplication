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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCartHappyPath(){
        fillInData("claudia", "testtest", 1L, "cookies", "gluten free", 1.00);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1l);
        modifyCartRequest.setUsername("claudia");
        modifyCartRequest.setQuantity(2);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyCartRequest);

        assertTrue(modifyCartRequest.getItemId()>0);
        assertEquals(HttpStatus.OK, cartResponseEntity.getStatusCode());
    }

    @Test
    public void testRemoveFromCartHappyPath(){
        fillInData("claudia", "testtest", 1L, "cookies", "gluten free", 1.00);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1l);
        modifyCartRequest.setUsername("claudia");
        modifyCartRequest.setQuantity(3);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyCartRequest);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());

        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1l);
        modifyCartRequest.setUsername("claudia");
        modifyCartRequest.setQuantity(1);
        cartResponseEntity = cartController.removeFromCart(modifyCartRequest);

        assertEquals(HttpStatus.OK, cartResponseEntity.getStatusCode());
        Cart modifiedCart = cartResponseEntity.getBody();
        assertEquals(BigDecimal.valueOf(1.00*2), modifiedCart.getTotal());
    }

    @Test
    public void testAddToCartInvalidUser(){
        fillInData("claudia", "testtest", 1L, "cookies", "gluten free", 1.00);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1l);
        modifyCartRequest.setUsername("julia");
        modifyCartRequest.setQuantity(2);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyCartRequest);
        assertFalse("claudia"== modifyCartRequest.getUsername());
        assertEquals(HttpStatus.NOT_FOUND, cartResponseEntity.getStatusCode());
    }

    @Test
    public void addToCartInvalidItemId(){
        fillInData("claudia", "testtest", 1L, "cookies", "gluten free", 1.00);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(3l);
        modifyCartRequest.setUsername("claudia");
        modifyCartRequest.setQuantity(2);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyCartRequest);
        assertFalse(1l == modifyCartRequest.getItemId());
        assertEquals(HttpStatus.NOT_FOUND, cartResponseEntity.getStatusCode());
    }

    @Test
    public void testRemoveFromCartInvalidUser(){
        fillInData("claudia", "testtest", 1L, "cookies", "gluten free", 1.00);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1l);
        modifyCartRequest.setUsername("claudia");
        modifyCartRequest.setQuantity(3);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyCartRequest);
        assertEquals(HttpStatus.OK, cartResponseEntity.getStatusCode());

        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1l);
        modifyCartRequest.setUsername("julia");
        modifyCartRequest.setQuantity(1);
        cartResponseEntity = cartController.removeFromCart(modifyCartRequest);
        assertNotEquals("claudia", modifyCartRequest.getUsername());
        assertEquals(HttpStatus.NOT_FOUND, cartResponseEntity.getStatusCode());
    }

    @Test
    public void testRemoveFromCartInvalidItemId(){
        fillInData("claudia", "testtest", 1L, "cookies", "gluten free", 1.00);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1l);
        modifyCartRequest.setUsername("claudia");
        modifyCartRequest.setQuantity(3);
        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyCartRequest);
        assertEquals(HttpStatus.OK, cartResponseEntity.getStatusCode());

        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(3l);
        modifyCartRequest.setUsername("claudia");
        modifyCartRequest.setQuantity(1);
        cartResponseEntity = cartController.removeFromCart(modifyCartRequest);
        assertNotEquals(1, modifyCartRequest.getItemId());
        assertEquals(HttpStatus.NOT_FOUND, cartResponseEntity.getStatusCode());
    }

    public void fillInData(String username, String password, Long itemId, String itemName, String itemDescription, double price){
        User user = new User();
        Cart cart = new Cart();
        user.setId(1l);
        user.setUsername(username);
        user.setPassword(password);
        user.setCart(cart);
        when(userRepository.findByUsername(username)).thenReturn(user);
        assertTrue(!(user.getId()==0));

        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setDescription(itemDescription);
        item.setPrice(BigDecimal.valueOf(price));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
    }
}
