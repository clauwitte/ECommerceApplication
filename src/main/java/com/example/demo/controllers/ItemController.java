package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {
	private static final Logger log =  LoggerFactory.getLogger(UserController.class);

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		log.info("ItemController getItems has been called");
		List<Item> item = itemRepository.findAll();
		if(item.size()==0){
			log.error("No items found");
			return ResponseEntity.notFound().build();
		}
		List<Item> items = item;
		log.info("All items have been found");
		return ResponseEntity.ok(items);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		log.info("ItemController getItemById {} has been called", id);
		Optional<Item> item = itemRepository.findById(id);
		if (!item.isPresent()) {
			log.error("Error with finding item. Cannot find item with id {}", id);
			return ResponseEntity.notFound().build();
		}
		Optional<Item> optionalItem = item;
		log.info("item with id {} has been found", id);
		return ResponseEntity.of(optionalItem);
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		log.info("ItemController getItemsByName {} has been called", name);
		List<Item> items = itemRepository.findByName(name);
		if(items==null || items.isEmpty()){
			log.error("Error with finding item {}", name);
			return ResponseEntity.notFound().build();
		}
		List<Item> itemList = items;
		log.info("Item {} has been found", name);
		return ResponseEntity.ok(itemList);
	}
	
}
