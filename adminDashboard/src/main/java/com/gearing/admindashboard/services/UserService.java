package com.gearing.admindashboard.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gearing.admindashboard.models.Role;
import com.gearing.admindashboard.models.User;
import com.gearing.admindashboard.repositories.RoleRepository;
import com.gearing.admindashboard.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	BCryptPasswordEncoder bCryptPassEncoder;
	
	public List<User> getAll() {
		return userRepo.findAll();
	}
	
	public User findById(Long id) {
		return userRepo.findById(id).isPresent() ? userRepo.findById(id).get() : null;
	}
	
	public void saveUserWithRole(User user, String role) {
		user.setPassword(bCryptPassEncoder.encode(user.getPassword()));
		user.setRoles(roleRepo.findByName(role));
		
		userRepo.save(user);
	}
	
	public void makeUserAdmin(User user) {
		user.setRoles(roleRepo.findByName("ROLE_ADMIN"));
		
		userRepo.save(user);
	}
	
	public void updateUser(User user) {
		userRepo.save(user);
	}
	
	// TODO: for some reason calling delete causes a TransientObjectException
	public void deleteUserById(Long id) {
		Optional<User> optionalUser = userRepo.findById(id);
		// Check that user actually exists to begin with
		if(optionalUser.isEmpty())
			throw new EntityNotFoundException("User not found with id: " + id);
		
		// Retrieve user if they exist
		User user = optionalUser.get();
		
		// Create copy of Role list to prevent concurrent modification exception in enhanced for loop
		List<Role> copyRoles = new ArrayList<>(user.getRoles());
		for(Role role: copyRoles) {
			user.getRoles().remove(role);
		}
		
		// Delete the user, or so help me
		userRepo.delete(user);
	}
	
	public User findByUsername(String username) {
		return userRepo.findByUsername(username);
	}
	
	public boolean adminExists() {
		List<User> users = userRepo.findAll();
		if(users.size() == 0)
			return false;
		for(User user : userRepo.findAll()) {
			String role = user.getRoles().get(0).getName();
			if(role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPER_ADMIN"))
				return true;
		}
		return false;
	}
}
