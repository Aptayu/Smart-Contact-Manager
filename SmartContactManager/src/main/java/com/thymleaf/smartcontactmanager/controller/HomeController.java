package com.thymleaf.smartcontactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thymleaf.smartcontactmanager.dao.ContactUserRepository;
import com.thymleaf.smartcontactmanager.entities.ContactUser;
import com.thymleaf.smartcontactmanager.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private ContactUserRepository contactUserRepository;

	@RequestMapping("/")
	public String home() {
		return "home";
	}

	@RequestMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("contactUser", new ContactUser());
		return "signUp";
	}

//	This is for registering user
	@PostMapping("/do_register")
	public String doRegister(@Valid @ModelAttribute("contactUser") ContactUser contactUser, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		session.getAttribute(null);
		try {
			if (!agreement) {
				System.out.println();
				throw new Exception("You have not accepted T&C");
			}
			if (bindingResult.hasErrors()) {
				model.addAttribute("contactUser", contactUser);
				return "signUp";
			}
			contactUser.setRole("ROLE_USER");
			contactUser.setPassword(passwordEncoder.encode(contactUser.getPassword()));
			this.contactUserRepository.save(contactUser);
			model.addAttribute("contactUser", new ContactUser());
			session.setAttribute("message", new Message("Successfully registered", "alert-success"));
			return "signUp";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("contactUser", contactUser);
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			return "signUp";
		}

	}

//	Login controller
	@GetMapping("/signin")
	public String CustomLogin(Model model) {
		return "login";
	}

}
