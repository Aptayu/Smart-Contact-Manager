package com.thymleaf.smartcontactmanager.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thymleaf.smartcontactmanager.dao.ContactUserRepository;
import com.thymleaf.smartcontactmanager.entities.ContactUser;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private ContactUserRepository contactUserRepository;

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println(userName);
//		get the user fromusername
		ContactUser contactUser = contactUserRepository.getContactUserByContactUsername(userName);
//		System.out.println(contactUser.toString());
		model.addAttribute("userDetails", contactUser);
		return "normal/user_dashboard";
	}
}
