package com.thymleaf.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.thymleaf.smartcontactmanager.entities.ContactUser;

@Component
public interface ContactUserRepository extends JpaRepository<ContactUser, Integer> {

	@Query("select u from ContactUser u where u.email = :email")
	public ContactUser getContactUserByContactUsername(@Param("email") String email);
}
