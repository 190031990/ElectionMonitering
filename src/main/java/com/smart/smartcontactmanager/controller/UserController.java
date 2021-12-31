package com.smart.smartcontactmanager.controller;

import com.smart.smartcontactmanager.dao.ContactRepository;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helper.Message;
import com.smart.smartcontactmanager.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private VolunteerService volunteerService;

    //method for hiding common data to response
    @ModelAttribute
    public void addCommonData(Model model,Principal principal)
    {
        String userName=principal.getName();
        System.out.println("USERNAME "+userName);

        User user=userRepository.getUserByUserName(userName);

        System.out.println("USER "+user);

        model.addAttribute("user",user);
    }


    //dashboard home
    @RequestMapping("/index")
     public String dashboard(Model model, Principal principal)
     {
         model.addAttribute("title","User dashboard");
         return "admin/user_dashboard";
     }

     //open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model)
    {
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact",new Contact());
        return "admin/add_contact_form";
    }

    //processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileImage") MultipartFile file,
                                 Principal principal, HttpSession session) {

        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            //processing and uploading a file....

            if(file.isEmpty())
            {
                //if the file is empty then try our message
                System.out.println("File is Empty");
                contact.setImage("contact.png");
            }
            else {
                //file the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File savefile=new ClassPathResource("static/img").getFile();
                Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is Uploaded");
            }


            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);
            System.out.println("DATA " + contact);
            System.out.println("Added to data base");

            //message success..........

            session.setAttribute("message",new Message("Your Contact is Added !! Add more..","success"));

        }
        catch(Exception e){
            System.out.println("ERROR "+e.getMessage());
            e.printStackTrace();
            //message error
            session.setAttribute("message",new Message("Some thing went Wrong !! Try Again..","danger"));

        }
        return "admin/add_contact_form";
    }

    //show contacts
    //per page=5[n]
    //current page=0[page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal ){
        m.addAttribute("title","show-contacts");


        String userName=principal.getName();
       User user= this.userRepository.getUserByUserName(userName);

        Pageable pageable=PageRequest.of(page,5);
       Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);

        m.addAttribute("contacts",contacts);
        m.addAttribute("currentPage",page);
        m.addAttribute("totalPages",contacts.getTotalPages());
        return "admin/show_contacts";
    }

    //showing particular contact details.

    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId,Model model)
    {
        System.out.println("CID "+cId);

        Optional<Contact> contactOptional=this.contactRepository.findById(cId);
        Contact contact=contactOptional.get();

        model.addAttribute("contact",contact);
        return "admin/contact_detail";
    }

    //delete contact handler

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId,Model model,HttpSession session,Principal principal)
    {
        Optional<Contact> contactOptional=this.contactRepository.findById(cId);

        System.out.println("CID "+cId);

        Contact contact=this.contactRepository.findById(cId).get();

        User user=this.userRepository.getUserByUserName(principal.getName());

        user.getContacts().remove(contact);

        this.userRepository.save(user);



        System.out.println("Deleted");
        session.setAttribute("message",new Message("Contact successfully Deleted...","success"));

        return "redirect:/admin/show-contacts/0";


    }

    //open update form handler
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid,Model m)
    {
        m.addAttribute("title","Update Contact");

        Contact contact=this.contactRepository.findById(cid).get();

        m.addAttribute("contact",contact);

        return "admin/update_form";
    }


    // update contact handler
    @RequestMapping(value="/process-update",method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model m,HttpSession session,Principal principal)
    {
        try{

            //old contact details
           Contact oldcontactDetail=this.contactRepository.findById(contact.getcId()).get();
            if(!file.isEmpty())
            {
                  //rewrite

                //delete old photo

                File deletefile=new ClassPathResource("static/img").getFile();
                File file1=new File(deletefile,oldcontactDetail.getImage());
                file1.delete();
                //update new photo
                File savefile=new ClassPathResource("static/img").getFile();
                Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());
            }
            else
            {
                contact.setImage(oldcontactDetail.getImage());
            }

            User user=this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);

            session.setAttribute("message",new Message("your contact is updated :)","success"));

        }catch(Exception e)
        {

        }
        System.out.println("CONTACT NAME"+contact.getName());
        System.out.println("CONTACT ID "+contact.getcId());
        return "redirect:/admin/"+contact.getcId()+"/contact";
    }

    //your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model)
    {
        model.addAttribute("title","Profile Page");
        return "admin/profile";
    }

    @GetMapping("/requests")
    public String showrequests(Model model)
    {
        model.addAttribute("listVolunteers",volunteerService.getAllVolunteers());
        return "admin/requests";

    }



}
