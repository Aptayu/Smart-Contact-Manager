package com.thymleaf.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.thymleaf.smartcontactmanager.dao.ContactRepository;
import com.thymleaf.smartcontactmanager.dao.ContactUserRepository;
import com.thymleaf.smartcontactmanager.entities.Contact;
import com.thymleaf.smartcontactmanager.entities.ContactUser;
import com.thymleaf.smartcontactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private ContactUserRepository contactUserRepository;

	@Autowired
	private ContactRepository contactRepository;

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
			Principal principal, HttpSession httpSession) {
//		This contact will be associate with one user therefore we can take help from principal
//		Either you can make one contact user repo here we can directly save it using the repo we have already
		try {
			String name = principal.getName();
			ContactUser contactUser = contactUserRepository.getContactUserByContactUsername(name);
			contact.setContactUser(contactUser);
//			Testing for catch block
//			if (3 > 2) {
//				throw new Exception();
//			}

//			Processing image
			if (file.isEmpty()) {
				System.out.println("File is empty");
				contact.setImage("defaultcontact.png");
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
//			everthing worked well send success message will use session
			httpSession.setAttribute("message",
					new com.thymleaf.smartcontactmanager.helper.Message("Successfully added.", "success"));

			return "normal/add_contact_form";

		} catch (Exception e) {
			e.printStackTrace();
//			send error message
			httpSession.setAttribute("message",
					new com.thymleaf.smartcontactmanager.helper.Message("Something went wrong. Try again", "danger"));

			return "normal/add_contact_form";
		}

	}

//	Show contacts controller
//	per page = 3max
//	Current page ={page}
//	O matlb 1st page pe ho 
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Principal principal, Model model) {
//		Get the contacts of all the user which is logged in
//		one way is to take the help of principal and enjoy
		String name = principal.getName();
		ContactUser contactUser = this.contactUserRepository.getContactUserByContactUsername(name);
		int id = contactUser.getId();
		Pageable pageable = PageRequest.of(page, 3);
//		We are using second method here taking help of contact repo
		Page<Contact> contacts = this.contactRepository.findContactsByUser(id, pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}

//	Getting the details clicking on name
	@GetMapping("/{cid}/contacts")
	public String getDetails(@PathVariable("cid") int cid, Model model, Principal principal, HttpSession session) {
		try {

			// System.out.println(cid);
//			person who is login
			String name = principal.getName();

			Optional<Contact> contatOptional = this.contactRepository.findById(cid);
			Contact contact = contatOptional.get();
//			we will compare "name" with the user contact of the id which person is trying to view
			if (name.equals(contact.getContactUser().getEmail())) {
				model.addAttribute("contact", contact);
			}
			return "normal/contact_detail";

		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));

			return "normal/contact_detail";

		}

	}

//	delete a particular contact
	@GetMapping("/{cid}/delete")
	public String deleteContact(@PathVariable("cid") int cid, Principal principal, HttpSession session) {
		try {

			String name = principal.getName();
			Optional<Contact> contatOptional = this.contactRepository.findById(cid);
			Contact contact = contatOptional.get();

			if (name.equals(contact.getContactUser().getEmail())) {
//				we'll set the user associated with it to null otherwise it can create problems
				contact.setContactUser(null);
//				we should also be deleting the image associated with in our folder.
//				getting the image name8
				String image = contact.getImage();
				this.contactRepository.delete(contact);
			}
			session.setAttribute("message", new Message("Contact deleted successfully", "alert-success"));
			return "redirect:/user/show_contacts/0";

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong. Please try again", "alert-danger"));
			return "redirect:/user/show_contacts/0";

		}

	}

}
