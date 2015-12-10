package neo4j.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import neo4j.domain.Follower;
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
    
    public List<Follower> getfollowlist(String username) {
    	Iterator<Map<String, Object>> result = userRepository.getfollowlist(username).iterator();
    	List<Follower> res = new ArrayList<Follower>();
    	while (result.hasNext()) {
    		Map<String, Object> row = result.next();
            System.out.println(row.get("name").toString());
            System.out.println(row.get("condition").toString());
            Follower f = new Follower(row.get("name").toString(),row.get("condition").toString());
            res.add(f);
    	}
    	return res;
    }
    
    public List<Follower> getfollowers(String username) {
    	Iterator<Map<String, Object>> result = userRepository.getfollowers(username).iterator();
    	List<Follower> res = new ArrayList<Follower>();
    	while (result.hasNext()) {
    		Map<String, Object> row = result.next();
            System.out.println(row.get("name").toString());
            Follower f = new Follower(row.get("name").toString(),"1");
            res.add(f);
    	}
    	return res;
    }
    
    public void createfollow(String from, String to) {
    	userRepository.createfollow(from,to);
    }
    
    public void deletefollow(String from, String to) {
    	userRepository.deletefollow(from,to);
    }
}
