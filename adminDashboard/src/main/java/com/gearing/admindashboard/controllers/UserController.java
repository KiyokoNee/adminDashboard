package com.gearing.admindashboard.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearing.admindashboard.models.User;
import com.gearing.admindashboard.services.UserService;
import com.gearing.admindashboard.validator.UserValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {
	@Autowired
	private UserService userServ;
	@Autowired
	private UserValidator userValid;
	
	@GetMapping("/")
	public String toLogin() {
		return "redirect:/login";
	}
	
	@GetMapping("/login")
	public String index(Model model, @RequestParam(required=false) String error,
			@RequestParam(required=false) String logout) {
		if(model.getAttribute("newUser") == null)
			model.addAttribute("newUser", new User());
		if(error != null)
			model.addAttribute("errorMessage", "Invalid Credentials. Please try again");
		if(logout != null)
			model.addAttribute("logoutMessage", "Logout Successful!");
		
		return "index.jsp";
	}
	
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("newUser") User newUser, BindingResult result,
			Model model, HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		
		userValid.validate(newUser, result);
		
		if(result.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newUser", result);
			redirectAttributes.addFlashAttribute("newUser", newUser);
			
			return "redirect:/login";
		}
		
		String password = newUser.getPassword();
		if(userServ.getAll().isEmpty()) {
			userServ.saveUserWithRole(newUser, "ROLE_SUPER_ADMIN");
		}
		else if(!userServ.adminExists()) {
			// I know this is technically redundant but the task list requires it, so...
			userServ.saveUserWithRole(newUser, "ROLE_ADMIN");
		} else {
			
			userServ.saveUserWithRole(newUser, "ROLE_USER");
		}
		
		try {
			request.login(newUser.getUsername(), password);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "redirect:/dashboard";
	}
	
	@GetMapping("/dashboard")
	public String dashboard(Principal principal, Model model) {
		String username = principal.getName();
		User user = userServ.findByUsername(username);
		
		// if they are admin they have their own page they are working on
		if(user.getRoles().get(0).getName().equals("ROLE_ADMIN") || user.getRoles().get(0).getName().equals("ROLE_SUPER_ADMIN"))
			return "redirect:/admin";
		LocalDate signDate = LocalDate.now();
		// Because I didn't think ahead, I had to convert the Date object to a LocalDate. Just use LocalDate, kids!
		LocalDate createDate = LocalDate.ofInstant(user.getCreatedAt().toInstant(), ZoneId.systemDefault());
		
		model.addAttribute("currentuser", user);
		model.addAttribute("signdate", signDate);
		model.addAttribute("createdatepattern", "MMMM d" + getDateSuffix(createDate) + " yyyy");
		model.addAttribute("signdatepattern", "MMMM d" + getDateSuffix(signDate) + " yyyy");
		
		return "dashboard.jsp";
	}
	
	@GetMapping("/admin")
	public String admin(Principal principal, Model model) {
		String username = principal.getName();
		model.addAttribute("currentuser", userServ.findByUsername(username));
		model.addAttribute("allusers", userServ.getAll());
		
		return "admin.jsp";
	}
	
	@PutMapping("/admin/{userId}")
	public String promote(Principal principal, Model model, @PathVariable Long userId) {
		if(principal == null)
			return "redirect:/login";
		User user = userServ.findById(userId);
		
		// Make sure we aren't doing anything crazy if the user is null
		if(user != null)
			userServ.makeUserAdmin(user);
		
		return "redirect:/admin";
	}
	
	@PutMapping("/delete/{userId}")
	public String delete(Principal principal, Model model, @PathVariable Long userId) {
		if(principal == null)
			return "redirect:/login";
		userServ.deleteUserById(userId);
		
		return "redirect:/admin";
	}
	
	private String getDateSuffix(LocalDate date) {
		int dayOfMonth = date.getDayOfMonth();
        if (dayOfMonth == 1 || dayOfMonth == 21 || dayOfMonth == 31)
        	return "'st'";
        if (dayOfMonth == 2 || dayOfMonth == 22)
            return "'nd'";
        if (dayOfMonth == 3 || dayOfMonth == 23)
            return "'rd'";
        return "'th'";
	}
}
