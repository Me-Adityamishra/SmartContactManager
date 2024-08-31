package com.SmartContactManager.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.SmartContactManager.entities.User;

public class CustomUserDetails implements UserDetails{
  private User user;
  
	public CustomUserDetails(User user) {
	super();
	this.user = user;
   }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
     
		 SimpleGrantedAuthority simpleGrantedAuthority=new SimpleGrantedAuthority(user.getRole());

		return List.of(simpleGrantedAuthority);
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user != null ? user.getPassword() : null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return true;
	}

	@Override
	public boolean isEnabled() {
	
		return true;
	}

}
