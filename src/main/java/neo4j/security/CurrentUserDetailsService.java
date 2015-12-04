package neo4j.security;

import neo4j.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CurrentUserDetailsService implements UserDetailsService {
	
	@Autowired UserService userService;


    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUser(username);
        return user;
    }
}
