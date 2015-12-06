package neo4j.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import neo4j.domain.*;
import neo4j.repositories.*;
import neo4j.services.DatasetService;
import neo4j.services.PaperService;
import neo4j.services.UserService;

@Configuration
@Controller
public class PageController {
	
	@Autowired UserService userService;
	
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage(@RequestParam Optional<String> error) {
        return "login";
    }
    
    @RequestMapping(value = "/currentUser", method = RequestMethod.GET)
    public String currentUser(final HttpServletRequest request, Principal principal, Model model) {
    	System.out.println("current user is" + principal.getName());
    	model.addAttribute("username", principal.getName());
        return "person";
    }
    
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(final HttpServletRequest request, Principal principal, Model model) {
    	String username = principal.getName();
    	System.out.println("current user is" + username);
    	System.out.println("last visited" + userService.getlastpath(username));
    	model.addAttribute("username", username);
    	if (!userService.getlastpath(username).equals(""))
    		model.addAttribute("lastvisit", userService.getlastpath(username));
        return "index";
    }
    
    @RequestMapping(value="/")
    public String index(){
        return "redirect:/home";
    }    
    
  
}