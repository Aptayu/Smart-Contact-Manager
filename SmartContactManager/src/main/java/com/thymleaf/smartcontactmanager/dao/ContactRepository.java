package com.thymleaf.smartcontactmanager.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.thymleaf.smartcontactmanager.entities.Contact;

@Component
public interface ContactRepository extends JpaRepository<Contact, Integer> {
//	pagination..
//currentpaerpage, contactperpage

	@Query("from Contact as d where d.contactUser.id =:contactUserId")
	public Page<Contact> findContactsByUser(@Param("contactUserId") int contactUserId, Pageable pageable);

}
