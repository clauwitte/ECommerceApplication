package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitOrderHappyPath(){
        fillInData("cookie", "claudia");
        ResponseEntity<UserOrder> orderResponseEntity = orderController.submit("claudia");
        assertEquals(HttpStatus.OK, orderResponseEntity.getStatusCode());
        UserOrder userOrder = orderResponseEntity.getBody();
        assertEquals(BigDecimal.valueOf(1.00),userOrder.getTotal());
        assertTrue(!userOrder.getItems().isEmpty());

    }

    @Test
    public void testSubmitOrderNotMatchinUsername(){
        fillInData("cookie", "claudia");
        ResponseEntity<UserOrder> orderResponseEntity = orderController.submit("julia");
        assertEquals(HttpStatus.NOT_FOUND, orderResponseEntity.getStatusCode());
    }

    @Test
    public void testGetOrderForUserHappyPath(){
        fillInData("cookie", "claudia");
        ResponseEntity<List<UserOrder>> orderResponseEntity = orderController.getOrdersForUser("claudia");
        assertEquals(HttpStatus.OK, orderResponseEntity.getStatusCode());
        List<UserOrder> userOrders = orderResponseEntity.getBody();
        assertNotNull(userOrders);
    }

    @Test
    public void testGetOrderForUserNotMatchingUsername(){
        fillInData("cookie", "claudia");
        ResponseEntity<List<UserOrder>> orderResponseEntity = orderController.getOrdersForUser("julia");
        assertEquals(HttpStatus.NOT_FOUND, orderResponseEntity.getStatusCode());
    }

    public void fillInData(String itemName, String username){
        Item item = new Item();
        List<Item> items = new ArrayList<>();
        item.setId(1l);
        item.setName(itemName);
        item.setDescription("gluten free");
        item.setPrice(BigDecimal.valueOf(1.00));
        items.add(item);
        Cart cart = new Cart();
        User user = new User();
        user.setId(1l);
        user.setUsername(username);
        user.setPassword("testtest");
        cart.setId(1l);
        cart.setUser(user);
        cart.setTotal(items.get(0).getPrice());
        cart.setItems(items);
        user.setCart(cart);
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.findByUsername("julia")).thenReturn(null);
    }


}
