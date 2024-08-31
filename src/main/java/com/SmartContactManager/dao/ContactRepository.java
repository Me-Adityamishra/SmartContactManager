package com.SmartContactManager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SmartContactManager.entities.Contact;
import com.SmartContactManager.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer>{

    //pagination
    @Query("from Contact as c where c.user.id= :userId")
    //currentpage-page
    //current per page-5
    public Page<Contact> findContactsByUser(@Param("userId")int userId, Pageable pageable);
    
    //search
    public List<Contact> findByNameContainingAndUser(String name,User user);
}
