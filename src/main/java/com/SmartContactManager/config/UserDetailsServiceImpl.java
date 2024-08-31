package com.SmartContactManager.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.SmartContactManager.dao.UserRepository;
import com.SmartContactManager.entities.User;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		//fetching user from database
		User user=(User)userRepository.getUserBuUserEmail(email);
		
		if(user==null)
		{
			 throw new UsernameNotFoundException("User not found with email: " + email);
			
		}
		
		CustomUserDetails customUserDetail=new CustomUserDetails(user);
		
		
		
		return customUserDetail;
	}

}
