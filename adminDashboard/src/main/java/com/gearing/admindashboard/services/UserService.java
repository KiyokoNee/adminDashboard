package com.gearing.admindashboard.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gearing.admindashboard.models.User;
import com.gearing.admindashboard.repositories.RoleRepository;
import com.gearing.admindashboard.repositories.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired BCryptPasswordEncoder bCryptPassEncoder;
	
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
		// Testing that we get to this point
		System.out.println("got here");
		userRepo.deleteById(id);
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
