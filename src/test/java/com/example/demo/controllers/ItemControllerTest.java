package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testFindAllItemsHappyPath(){
       fillInData("cookie", "gluten free", 1.00);
        ResponseEntity<List<Item>> itemResponseEntity = itemController.getItems();

        assertEquals(HttpStatus.OK, itemResponseEntity.getStatusCode());
    }

    @Test
    public void testFindAllItemsIsEmpty(){
        ResponseEntity<List<Item>> itemResponseEntity = itemController.getItems();
        assertEquals(HttpStatus.NOT_FOUND , itemResponseEntity.getStatusCode());
    }

    @Test
    public void testFindItemByIdHappyPath(){
        fillInData("cookie", "gluten free", 1.00);

        ResponseEntity<Item> itemResponseEntity = itemController.getItemById(1l);
        assertTrue(itemResponseEntity!=null);
        assertEquals(HttpStatus.OK, itemResponseEntity.getStatusCode());
    }

    @Test
    public void testFindByIdNotCompatible(){
        fillInData("cookie", "gluten free", 1.00);

        ResponseEntity<Item> itemResponseEntity = itemController.getItemById(2l);
        assertEquals(HttpStatus.NOT_FOUND, itemResponseEntity.getStatusCode());
    }

    @Test
    public void testFindByNameHappyPath(){
        fillInData("cookie", "gluten free", 1.00);
        ResponseEntity<List<Item>> itemResponseEntity = itemController.getItemsByName("cookie");
        assertEquals(HttpStatus.OK, itemResponseEntity.getStatusCode());
    }

    @Test
    public void testFindByNameNotMatchingName(){
        fillInData("cookie", "gluten free", 1.00);

        ResponseEntity<List<Item>> itemResponseEntity = itemController.getItemsByName("tea");
        assertNotEquals(HttpStatus.OK, itemResponseEntity.getStatusCode());
        assertTrue(itemResponseEntity.getStatusCode()==HttpStatus.NOT_FOUND);
    }

    public void fillInData(String itemName, String itemDescription, double price){
        Item item = new Item();
        item.setId(1l);
        item.setName(itemName);
        item.setDescription(itemDescription);
        item.setPrice(BigDecimal.valueOf(price));

        when(itemRepository.findByName(item.getName())).thenReturn(Collections.singletonList(item));
        when(itemRepository.findByName(itemName)).thenReturn(Collections.singletonList(item));
        when(itemRepository.findById(1l)).thenReturn(Optional.of(item));
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
    }
}