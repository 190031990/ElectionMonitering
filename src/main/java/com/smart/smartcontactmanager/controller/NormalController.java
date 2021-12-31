package com.smart.smartcontactmanager.controller;


import com.smart.smartcontactmanager.dao.QueriesRepository;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.dao.VolunteerRepository;
import com.smart.smartcontactmanager.entities.Queries;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.entities.Volunteer;
import com.smart.smartcontactmanager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class NormalController {

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueriesRepository queriesRepository;

    @ModelAttribute
    public void addCommonData(Model model,Principal principal)
    {
        String userName=principal.getName();
        System.out.println("USERNAME "+userName);

        User user=userRepository.getUserByUserName(userName);

        System.out.println("USER "+user);

        model.addAttribute("user",user);
    }

    @GetMapping("/home")
    public String dashboard(Model model, Principal principal)
    {
        model.addAttribute("title","User dashboard");
        return "normal/user_dashboard";
    }

    @GetMapping("/donate")
    public String openAddContactForm(Model model)
    {
        model.addAttribute("title","Donate");
        return "normal/donate";
    }

    @RequestMapping("/query")
    public String query(Model model)
    {
        model.addAttribute("title","Enter query");
        model.addAttribute("queries",new Queries());
        return "normal/query";
    }

    //handler for registering volunteer
    @RequestMapping(value="/do_query",method= RequestMethod.POST)
    public String registerVolunteer(@Valid @ModelAttribute("queries") Queries queries,
                                    @RequestParam("profileImage") MultipartFile file,
                                    BindingResult result1, Model model,Principal principal, HttpSession session)
    {
        try {

            if(file.isEmpty())
            {
                //if the file is empty then try our message
                System.out.println("File is Empty");
                queries.setImage("contact.png");
            }
            else {
                //file the file to folder and update the name to contact
                queries.setImage(file.getOriginalFilename());

                File savefile=new ClassPathResource("static/img").getFile();
                Path path= Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is Uploaded");
            }


            if(result1.hasErrors())
            {
                System.out.println("ERROR "+result1.toString());
                model.addAttribute("Queries",queries);
                return "normal/query";
            }
            System.out.println("Queries " + queries);
            Queries result = (Queries) this.queriesRepository.save(queries);
            model.addAttribute("volunteer", result);

            session.setAttribute("message",new Message("Successfully sent (:","alert-success"));
            return "normal/query";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            model.addAttribute("queries",queries);
            session.setAttribute("message",new Message("Something Went Wrong !!"+e.getMessage(),"alert-danger"));
            return "normal/query";
        }

    }


    @RequestMapping("/volunteer")
    public String signup(Model model)
    {
        model.addAttribute("title","Become-Volunteer");
        model.addAttribute("volunteer",new Volunteer());
        return "normal/volunteer";
    }

    //handler for registering volunteer
    @RequestMapping(value="/do_volunteerregister",method= RequestMethod.POST)
    public String registerVolunteer(@Valid @ModelAttribute("volunteer") Volunteer volunteer,
                                    @RequestParam("profileImage") MultipartFile file,
                                    BindingResult result1, Model model,Principal principal, HttpSession session)
    {
        try {

            if(file.isEmpty())
            {
                //if the file is empty then try our message
                System.out.println("File is Empty");
                volunteer.setImage("contact.png");
            }
            else {
                //file the file to folder and update the name to contact
                volunteer.setImage(file.getOriginalFilename());

                File savefile=new ClassPathResource("static/img").getFile();
                Path path= Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is Uploaded");
            }


            if(result1.hasErrors())
            {
                System.out.println("ERROR "+result1.toString());
                model.addAttribute("volunteer",volunteer);
                return "normal/volunteer";
            }
            System.out.println("Volunteer " + volunteer);
            Volunteer result = this.volunteerRepository.save(volunteer);
            model.addAttribute("volunteer", result);

            session.setAttribute("message",new Message("Successfully Voted (:","alert-success"));
            return "normal/volunteer";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            model.addAttribute("volunteer",volunteer);
            session.setAttribute("message",new Message("Something Went Wrong !!"+e.getMessage(),"alert-danger"));
            return "normal/volunteer";
        }

    }

    @RequestMapping("/something")
    public String voting(Model model)
    {
        model.addAttribute("title","voting page");
        return "normal/something";
    }
    @RequestMapping("/laws")
    public String laws(Model model)
    {
        model.addAttribute("title","laws");
        return "normal/laws";
    }
}
