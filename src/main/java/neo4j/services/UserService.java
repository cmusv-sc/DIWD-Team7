package neo4j.services;

import java.util.Iterator;
import java.util.Map;

import neo4j.repositories.PaperRepository;
import neo4j.repositories.UserRepository;
import neo4j.security.CurrentUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

	@Autowired UserRepository userRepository;
	
    public User getUser(String username) {
        Iterator<Map<String, Object>> result = userRepository.findUser(username).iterator();
        
        String name;
        String pass;
        if (result.hasNext()) {
            Map<String, Object> row = result.next();
            name = row.get("name").toString();
            pass = row.get("pass").toString();
            User u = new CurrentUser(name,pass);
            return u;
        }
        return null;
    }
    
    public void removelastpath(String username){
    	userRepository.removelastpath(username);
    }
    
    public void setlastpath(String username, String path) {
        userRepository.setlastpath(username,path);
    }
    
    public String getlastpath(String username) {
    	Iterator<Map<String, Object>> result = userRepository.getlastpath(username).iterator();
    	if (result.hasNext()) {
            Map<String, Object> row = result.next();
            	return row.get("path").toString();
    	}
    	return "";
    }
    
    public boolean createuser(String username, String password) {
    	Iterator<Map<String, Object>> result = userRepository.findUser(username).iterator();
    	if (result.hasNext()) {
            return false;
    	}
    	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 
    	String encodedPassword = passwordEncoder.encode(password);
    	userRepository.createuser(username,encodedPassword);
    	return true;
    }
}
