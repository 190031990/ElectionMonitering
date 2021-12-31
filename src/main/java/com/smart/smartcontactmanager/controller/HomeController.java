package com.smart.smartcontactmanager.controller;

import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model)
    {
        model.addAttribute("title","Home-Election Monitoring");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model)
    {
        model.addAttribute("title","About-Election Monitoring");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model)
    {
        model.addAttribute("title","Register-Election Monitoring");
        model.addAttribute("user",new User());
        return "signup";
    }

    //handler for registering user
    @RequestMapping(value="/do_register",method= RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,Model model,HttpSession session)
    {
        try {
            if(result1.hasErrors())
            {
                System.out.println("ERROR "+result1.toString());
                model.addAttribute("user",user);
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageurl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            System.out.println("User " + user);
            User result = this.userRepository.save(user);
            model.addAttribute("user", result);

            session.setAttribute("message",new Message("Successfully Registered (:","alert-success"));
            return "signup";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            model.addAttribute("user",user);
            session.setAttribute("message",new Message("Something Went Wrong !!"+e.getMessage(),"alert-danger"));
            return "signup";
        }

    }

    //handler for custom login
    @GetMapping("/signin")
    public String customLogin(Model model)
    {
        model.addAttribute("title","Login Page");
        return "login";
    }
}
