package com.SmartContactManager.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.SmartContactManager.dao.UserRepository;
import com.SmartContactManager.entities.User;
import com.SmartContactManager.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home-Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About-Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup/")
    public String signup(Model model) {
        model.addAttribute("title", "Register-Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
            HttpSession session) {
        try {
            if (!agreement) {
                System.out.println("You have not accepted the terms and conditions");
                throw new Exception("You have not accepted the terms and conditions");
            }

            if (result.hasErrors()) 
            {
            	System.out.println("ERROR" + result.toString());
                System.out.println("Validation errors found");
                model.addAttribute("user", user);
                return "signup";
            }

            // If no validation errors, proceed with user registration
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Saving the user to the database
            User savedUser= this.userRepository.save(user);
            System.out.println("User saved: " + savedUser);

            // Clearing the form for the next registration
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
            return "signup";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Handling any exceptions that occur during user registration
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
            return "signup";
        }
    }
    //handler for customize login page
    @GetMapping("/signin")
    public String customLogin(Model model)
    {
    	model.addAttribute("title","Login Page");
    	
    	return "login";
    }


}
