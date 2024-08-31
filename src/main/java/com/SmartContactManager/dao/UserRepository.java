package com.SmartContactManager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SmartContactManager.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Additional methods if needed
	@Query("select u from User u where u.email= :email")
	public User getUserBuUserEmail(@Param("email") String email);
	
	
}
