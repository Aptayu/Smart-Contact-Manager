package com.thymleaf.smartcontactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.thymleaf.smartcontactmanager.dao.ContactUserRepository;
import com.thymleaf.smartcontactmanager.entities.ContactUser;

public class ContactUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private ContactUserRepository contactUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		fetching user by database we will take help from dao repo
		ContactUser contactUser = contactUserRepository.getContactUserByContactUsername(username);
		if (contactUser == null) {
			throw new UsernameNotFoundException("could not find user");
		}
		CustomContactUserDetails customContactUserDetails = new CustomContactUserDetails(contactUser);
		return customContactUserDetails;
	}

}
