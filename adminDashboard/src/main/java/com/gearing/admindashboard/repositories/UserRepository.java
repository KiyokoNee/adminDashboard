package com.gearing.admindashboard.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gearing.admindashboard.models.Role;
import com.gearing.admindashboard.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> findById(Long id);
	
	User findByEmail(String email);
	
	User findByUsername(String username);
	
	List<User> findAllByRoles(Role role);
	
	List<User> findAll();
}
