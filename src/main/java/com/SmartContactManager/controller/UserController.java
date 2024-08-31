package com.SmartContactManager.controller;

import java.io.File;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.SmartContactManager.dao.ContactRepository;
import com.SmartContactManager.dao.UserRepository;
import com.SmartContactManager.entities.Contact;
import com.SmartContactManager.entities.User;
import com.SmartContactManager.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
    private BCryptPasswordEncoder bcryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    // method for adding common data to the response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        // principal will give current logged in user name
        String userName = principal.getName();
        System.out.println("USERNAME=" + userName);

        // get user from db
        User user = this.userRepository.getUserBuUserEmail(userName);
        System.out.println(user);
        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    // open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    // processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile file, Principal principal,HttpSession session) {
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserBuUserEmail(name);
            // processing and uploading file

            if (file.isEmpty()) {
                // handle empty file upload
            	contact.setImage("Contact.png");
            }
            else 
            {
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/image").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
            }

            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);
            System.out.println("DATA" + contact);
            System.out.println("Added to database");
            //message success.....
            session.setAttribute("message", new Message("Your contact is added !! Add more ", "success"));
            
        } catch (IOException e) {
            System.out.println("ERROR" + e.getMessage());
            e.printStackTrace();
            //error message.......
            session.setAttribute("message", new Message("Something went wrong !! Try again ", "danger"));
        }
        
        return "normal/add_contact_form";
    }
    //show contacts handler
    //per page=5[n]
    //current page=0[page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page")Integer page, Model m,Principal principal )
    
    {

    	m.addAttribute("title","show user contacts ");
    	//contact list send
    	//String userName=principal.getName();
    	//User user=this.userRepository.getUserBuUserEmail(userName);
    	//List <Contact> contacts=user.getContacts();
    	String userName=principal.getName();
    	User user =this.userRepository.getUserBuUserEmail(userName);
    	Pageable pageable=PageRequest.of(page, 5);
        Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
    	m.addAttribute("contacts",contacts);
    	m.addAttribute("currentPage",page);
    	m.addAttribute("totalPages",contacts.getTotalPages());
    	return "normal/show_contacts";
    }
    //showing particular contact details
    @RequestMapping("/{cId}/contact")
    public String showConatctDetail(@PathVariable("cId")Integer cId,Model model,Principal principal)
    {
    	System.out.println("CID"+cId);
    	
    	Optional<Contact> contactOptional=this.contactRepository.findById(cId);
    	Contact contact=contactOptional.get();
    	
    	
    	//
    	String userName=principal.getName();
    	User user=this.userRepository.getUserBuUserEmail(userName);
    	if(user.getId()==contact.getUser().getId())
    	{
    		model.addAttribute("contact",contact);
    		model.addAttribute("title",contact.getName() );
    	}
    	
    	return "normal/contact_detail";
    }
  //delete Contact handler
  	@RequestMapping(value = "/delete/{cId}")
  	public String deleteContact(@PathVariable("cId") Integer cid, Model model, Principal principal, HttpSession session) {
  		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
  		Contact contact = contactOptional.get();

  		String name = principal.getName();
  		User user = this.userRepository.getUserBuUserEmail(name);
  		if(user.getId()==contact.getUser().getId()) {
  			//assignment remove img
  			//contact.getImage()
  			user.getContacts().remove(contact);
  			//contact.setUser(null);
  			//this.contactRepository.delete(contact);
  			this.contactRepository.deleteById(cid);
  			session.setAttribute("message", new Message("Contact deleted Successfully...", "success"));
//  			return "redirect:/user/show-contacts/0";
  		}
  		return "redirect:/user/show-contacts/0";
  	}

 // Open update form handler
 	@PostMapping("/update-contact/{cId}")
 	public String updateForm(@PathVariable("cId") Integer id, Model model) {
 		model.addAttribute("title", "Update Contact");
 		Optional<Contact> optional = this.contactRepository.findById(id);
 		Contact contact = optional.get();
 		model.addAttribute("contact", contact);
 		return "normal/update_form";
 	}
 	//update Contact handler
 		@RequestMapping(value = "/process-update" , method = RequestMethod.POST)
 		public String updateHandler(@ModelAttribute Contact contact, Model model, HttpSession session, 
 				@RequestParam("profileImage") MultipartFile multiPartFile, Principal principal) {
 			try {
 				//old Contact details
 					Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
 				if(!multiPartFile.isEmpty()) {
 					String[] split = contact.getEmail().split("@");
 					//file is not empty so update the file
 					//delete old photo
 					File deleteFile = new ClassPathResource("static/image").getFile();
 					File file = new File(deleteFile, oldContactDetail.getImage());
 					file.delete();

 					//update new photo
 					File fileObject = new ClassPathResource("static/image").getFile();
 					Path path = Paths.get(fileObject.getAbsolutePath()+File.separator+split[0]+"_"+multiPartFile.getOriginalFilename());
 					Files.copy(multiPartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
 					contact.setImage(split[0]+"_"+multiPartFile.getOriginalFilename());

 				}
 				else
 				{
 					//file is empty
 					contact.setImage(oldContactDetail.getImage());
 				}
 				User user = this.userRepository.getUserBuUserEmail(principal.getName());
 				contact.setUser(user);
 				this.contactRepository.save(contact);
 				session.setAttribute("message",new Message("Your Contact is updated.......","success"));
 				System.out.println("Id :"+contact.getcId());
 			}catch (Exception e) {
 				e.printStackTrace();
 			}
 			return "redirect:/user/"+contact.getcId()+"/contact";
 		}
 		//profile page handler
 		@GetMapping("/profile")
 		public String yourProfilePage(Model model) {
 			model.addAttribute("title", "Profile Page");
 			return "normal/profile";
 		}
    //open setting handler
 		@GetMapping("/setting")
 		public String openSetting()
 		{
 			return "normal/setting";
 			
 		}
 		//change password
 		@PostMapping("/change-password")
 		public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,Principal principal,HttpSession session) {
 		    System.out.println("OLD PASSWORD: " + oldPassword);
 		    System.out.println("NEW PASSWORD: " + newPassword);
 		    
 		    // Your controller logic here...
 		    String userName= principal.getName();
 		    User currentUser= this.userRepository.getUserBuUserEmail(userName);
 		    System.out.println(currentUser.getPassword());
 		    if(this.bcryptPasswordEncoder.matches(oldPassword,currentUser.getPassword()))
 		    {
 		    	//change the password
 		    	currentUser.setPassword(this.bcryptPasswordEncoder.encode(newPassword));
 		    	this.userRepository.save(currentUser);
 		    	session.setAttribute("message", new Message("Your password is successfully changed ", "success"));
 		    }
 		    else
 		    {
 		    	//error
 		    	session.setAttribute("message", new Message("Please Enter correct old password...", "danger"));
 		    	return "redirect:/user/setting";
 		    	
 		    }
 		    return "redirect:/user/index";
 		}

    
}
