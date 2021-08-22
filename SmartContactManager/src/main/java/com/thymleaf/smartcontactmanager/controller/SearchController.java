package com.thymleaf.smartcontactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.thymleaf.smartcontactmanager.dao.ContactRepository;
import com.thymleaf.smartcontactmanager.dao.ContactUserRepository;
import com.thymleaf.smartcontactmanager.entities.Contact;
import com.thymleaf.smartcontactmanager.entities.ContactUser;

@RestController
public class SearchController {

	@Autowired
	private ContactUserRepository contactUserRepository;
	@Autowired
	private ContactRepository contactRepository;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
		System.out.println(query);
		ContactUser user = this.contactUserRepository.getContactUserByContactUsername(principal.getName());
		List<Contact> contacts = this.contactRepository.findByNameContainingAndContactUser(query, user);
		return ResponseEntity.ok(contacts);
	}

}
