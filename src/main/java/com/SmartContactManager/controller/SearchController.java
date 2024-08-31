package com.SmartContactManager.controller;

import com.SmartContactManager.entities.User;

import java.security.Principal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.SmartContactManager.dao.ContactRepository;
import com.SmartContactManager.dao.UserRepository;
import com.SmartContactManager.entities.Contact;
@RestController
public class SearchController {

	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private UserRepository userRepository;

	@GetMapping(value = "/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		System.out.println(query);
		String name = principal.getName();
		User user = this.userRepository.getUserBuUserEmail(name);
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
}
