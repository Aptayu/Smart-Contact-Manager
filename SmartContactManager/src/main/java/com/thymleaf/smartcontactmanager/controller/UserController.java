package com.thymleaf.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.thymleaf.smartcontactmanager.dao.ContactUserRepository;
import com.thymleaf.smartcontactmanager.entities.Contact;
import com.thymleaf.smartcontactmanager.entities.ContactUser;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private ContactUserRepository contactUserRepository;

//	Common method which will run for every handler

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println(userName);
//		get the user fromusername
		ContactUser contactUser = contactUserRepository.getContactUserByContactUsername(userName);
//		System.out.println(contactUser.toString());
		model.addAttribute("userDetails", contactUser);
	}

//	When user login(user-dashboard home)
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		return "normal/user_dashboard";
	}

//	open add form controller
	@GetMapping("/add-contact")
	public String openAddContact(Model model) {
		model.addAttribute("title", "Add contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

//	Processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal) {
//		This contact will be associate with one user therefore we can take help from principal
//		Either you can make one contact user repo here we can directly save it using the repo we have already
		try {
			String name = principal.getName();
			ContactUser contactUser = contactUserRepository.getContactUserByContactUsername(name);
			contact.setContactUser(contactUser);
//			Processing image
			if (file.isEmpty()) {
				System.out.println("File is empty");
			} else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("File is uploaded ");
			}
			contactUser.getContacts().add(contact);

			this.contactUserRepository.save(contactUser);

			System.out.println(contact.toString());
			return "normal/add_contact_form";

		} catch (Exception e) {
			e.printStackTrace();
			return "normal/add_contact_form";
		}

	}

}
